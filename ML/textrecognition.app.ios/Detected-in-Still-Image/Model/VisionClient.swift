//
//  VisionClient.swift
//  Detected-in-Still-Image
//
//  Created by satoutakeshi on 2021/06/20.
//

import Foundation
import Vision

enum VisionRequestTypes {
    case unknown
    case word(rectBox: [CGRect], info: [[String: String]])
    case character(rectBox: [CGRect], info: [[String: String]])
    case textRecognize(info: [[String: String]])

    struct Set: OptionSet {
        typealias Element = VisionRequestTypes.Set
        let rawValue: Int8
        init(rawValue: Int8) {
            self.rawValue = rawValue
        }

        static let text             = Set(rawValue: 1 << 2)
        static let textRecognize    = Set(rawValue: 1 << 3)

        static let all: Set         = [
                                       .text,
                                        .textRecognize
                                       ]
    }
}

final class VisionClient: ObservableObject {

    enum VisionError: Error {
        case typeNotSet
        case visionError(error: Error)
    }

    private var requestTypes: VisionRequestTypes.Set = []
    private var imageViewFrame: CGRect = .zero

    /// ensure in main thread when you use the value
    @Published var result: VisionRequestTypes = .unknown
    @Published var error: VisionError?

    func configure(type: VisionRequestTypes.Set, imageViewFrame: CGRect) {
        self.requestTypes = type
        self.imageViewFrame = imageViewFrame
    }

    func performVisionRequest(image: CGImage,
                              orientation: CGImagePropertyOrientation) {
        guard !requestTypes.isEmpty else {
            error = VisionError.typeNotSet
            return 
        }
        let imageRequestHandler = VNImageRequestHandler(cgImage: image,
                                                        orientation: orientation,
                                                        options: [:])

        let requests = makeImageRequests()
        do {
            try imageRequestHandler.perform(requests)
        } catch {
            self.error = VisionError.visionError(error: error)
        }
    }

    private func makeImageRequests() -> [VNRequest] {
        var requests: [VNRequest] = []


        if requestTypes.contains(.text) {
            requests.append(textDetectionRequest)
        }

        if requestTypes.contains(.textRecognize) {
            requests.append(textRecognizeRequest)
        }

        return requests
    }


    lazy var textDetectionRequest: VNDetectTextRectanglesRequest = {
        let textDetectRequest = VNDetectTextRectanglesRequest(completionHandler: { [weak self] request, error in
            guard let self = self else { return }
            if let error = error {
                print(error.localizedDescription)
                self.error = VisionError.visionError(error: error)
                return
            }

            guard let results = request.results as? [VNTextObservation] else {
                return
            }

            let wordBoxes = results.map { observation -> CGRect in
                let rectBox = self.boundingBox(forRegionOfInterest: observation.boundingBox, withinImageBounds: self.imageViewFrame)
                print("detected Rect: \(rectBox.debugDescription)")
                return rectBox
            }
            let wordInfo = ["wordBoxes count": "\(results.count)"]
            self.result = .word(rectBox: wordBoxes, info: [wordInfo])

            let charRects = self.makeTextRect(textObservations: results, onImageWithBounds: self.imageViewFrame)
            let charInfo = ["char count": "\(charRects.count)"]
            self.result = .character(rectBox: charRects, info: [charInfo])
        })
        // Tell Vision to report bounding box around each character.
        textDetectRequest.reportCharacterBoxes = true
        return textDetectRequest
    }()

    lazy var textRecognizeRequest: VNRecognizeTextRequest = {
        let textRecognizeRequest = VNRecognizeTextRequest { [weak self] request, error in
            guard let self = self else { return }
            if let error = error {
                print(error.localizedDescription)
                self.error = VisionError.visionError(error: error)
            }

            guard let results = request.results as? [VNRecognizedTextObservation] else {
                return
            }
            let candidatesTexts = results.compactMap { observation -> [String]? in
                guard observation.confidence > 0.3 else { return nil }
                let recognizedTexts = observation.topCandidates(3)
                return recognizedTexts.map { $0.string }
            }

            let info = candidatesTexts.compactMap { strings -> [String: String] in
                var output: [String: String] = [:]
                for (index, string) in strings.enumerated() {
                    output["candiate \(index)"] = string
                }
                return output
            }
            self.result = .textRecognize(info: info)
        }

        textRecognizeRequest.recognitionLevel = .fast

        textRecognizeRequest.progressHandler = { request , progress, error in
            print("progress: \(progress)")
        }
        return textRecognizeRequest
    }()



    // MARK: - Private
    private func boundingBox(forRegionOfInterest: CGRect,
                     withinImageBounds bounds: CGRect) -> CGRect {

        let imageWidth = bounds.width
        let imageHeight = bounds.height

        // Begin with input rect.
        var rect = forRegionOfInterest

        // Reposition origin.
        rect.origin.x = rect.origin.x * imageWidth + bounds.origin.x
        rect.origin.y = rect.origin.y * imageHeight + bounds.origin.y

        // Rescale normalized coordinates.
        rect.size.width *= imageWidth
        rect.size.height *= imageHeight

        return rect
    }


    private func makeRectanglePoints(onRects rects: [VNRectangleObservation],
                                     onImageWithBounds bounds: CGRect) -> [[Bool: [CGPoint]]] {
        var rectPoints: [[Bool: [CGPoint]]] = []

        for rect in rects {
            let topLeftX = rect.topLeft.x * bounds.width
            let topLeftY = rect.topLeft.y * bounds.height
            let topLeft = CGPoint(x: topLeftX, y: topLeftY)

            let topRightX = rect.topRight.x * bounds.width
            let topRightY = rect.topRight.y * bounds.height
            let topRight = CGPoint(x: topRightX, y: topRightY)

            let bottomLeftX = rect.bottomLeft.x * bounds.width
            let bottomLeftY = rect.bottomLeft.y * bounds.height
            let bottomLeft = CGPoint(x: bottomLeftX, y: bottomLeftY)

            let bottomRightX = rect.bottomRight.x * bounds.width
            let bottomRightY = rect.bottomRight.y * bounds.height
            let bottomRight = CGPoint(x: bottomRightX, y: bottomRightY)

            rectPoints.append([true: [topLeft, topRight, bottomRight, bottomLeft]])

        }
        return rectPoints
    }

    // MARK: - Text Detection
    private func makeTextRect(textObservations: [VNTextObservation], onImageWithBounds bounds: CGRect) -> [CGRect] {
        let charBoxRects = textObservations.compactMap { observation -> [CGRect]? in
            guard let charBoxes = observation.characterBoxes else {
                return nil
            }

            return charBoxes.compactMap { charObservation in
                self.boundingBox(forRegionOfInterest: charObservation.boundingBox, withinImageBounds: bounds)
            }
        }
        return charBoxRects.flatMap { $0 }
    }

    // MARK: - Text Recoginaze

    private func makeNormalizedPoints(region: VNFaceLandmarkRegion2D, faceBounds: CGRect) -> [CGPoint]? {
        guard region.pointCount > 1 else {
            return nil
        }
        let points = region.normalizedPoints.map { point -> CGPoint in
            let x = point.x * faceBounds.width + faceBounds.origin.x
            let y = point.y * faceBounds.height + faceBounds.origin.y
            return CGPoint(x: x, y: y)
        }
        return points
    }
}
