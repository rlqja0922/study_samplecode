//
//  qr.swift
//  kb
//
//  Created by reborn on 2021/09/27.
//

import Foundation
import AVFoundation
import SwiftUI

struct qr: View {
    //qr스캐너 변수
    @State var isPresentingScanner = false
    //코드값 출력변수
    @State var scannedCode: String?

    var body: some View {
        NavigationView {
            VStack(spacing: 10) {
                if self.scannedCode != nil {
                    Text((scannedCode)!)
                }
                Button("Scan Code") {
                    self.isPresentingScanner = true
                }
                .sheet(isPresented: $isPresentingScanner) {
                    self.scannerSheet
                }
                Text("Scan a QR code to begin")
                QRGeneratorView()
            }

        }
    }
        //qr코드 스캐닝 화면
    var scannerSheet : some View {
        ZStack{
            CodeScannerView(
                codeTypes: [.qr],
                completion: { result in
                    if case let .success(code) = result {
                        self.scannedCode = code
                        self.isPresentingScanner = false
                    }
                }
            )
        Image("viewfinder") .resizable()
                .frame(width: UIScreen.main.bounds.width * (8.5/10), height: UIScreen.main.bounds.height * (8/10))
        }
           

    }
}
struct QRGeneratorView: View {
    @State private var text = "testQR"
    @State private var text1 = ""
    var body: some View {
        VStack {
            Text(text)
                .padding()
            Button("qr코드 만들기"){
                text1 = text
            }
            Image(uiImage: UIImage(data: getQRCodeDate(text: text1)!)!)
                .resizable()
                .frame(width: 200, height: 200)
        }
    }
    
    func getQRCodeDate(text: String) -> Data? {
        guard let filter = CIFilter(name: "CIQRCodeGenerator") else { return nil }
        let data = text.data(using: .ascii, allowLossyConversion: false)
        filter.setValue(data, forKey: "inputMessage")
        guard let ciimage = filter.outputImage else {
            print("오류")
            return nil
        }
        let transform = CGAffineTransform(scaleX: 10, y: 10)
        let scaledCIImage = ciimage.transformed(by: transform)
        let uiimage = UIImage(ciImage: scaledCIImage)
        return uiimage.pngData()!
        
    }
}
/// A SwiftUI view that is able to scan barcodes, QR codes, and more, and send back what was found.
/// To use, set `codeTypes` to be an array of things to scan for, e.g. `[.qr]`, and set `completion` to
/// a closure that will be called when scanning has finished. This will be sent the string that was detected or a `ScanError`.
/// For testing inside the simulator, set the `simulatedData` property to some test data you want to send back.
public struct CodeScannerView: UIViewControllerRepresentable {
    public enum ScanError: Error {
        case badInput, badOutput
    }
    
    public enum ScanMode {
        case once, oncePerCode, continuous
    }

    public class ScannerCoordinator: NSObject, AVCaptureMetadataOutputObjectsDelegate {
        var parent: CodeScannerView
        var codesFound: Set<String>
        var isFinishScanning = false
        var lastTime = Date(timeIntervalSince1970: 0)

        init(parent: CodeScannerView) {
            self.parent = parent
            self.codesFound = Set<String>()
        }

        public func reset()
        {
            self.codesFound = Set<String>()
            self.isFinishScanning = false
            self.lastTime = Date(timeIntervalSince1970: 0)
        }
//QR코드 스캔 데이터
        public func metadataOutput(_ output: AVCaptureMetadataOutput, didOutput metadataObjects: [AVMetadataObject], from connection: AVCaptureConnection) {
            if let metadataObject = metadataObjects.first {
                guard let readableObject = metadataObject as? AVMetadataMachineReadableCodeObject else { return }
                guard let stringValue = readableObject.stringValue else { return }
                guard isFinishScanning == false else { return }

                switch self.parent.scanMode {
                case .once:
                    found(code: stringValue)
                    // make sure we only trigger scan once per use
                    isFinishScanning = true
                case .oncePerCode:
                    if !codesFound.contains(stringValue) {
                        codesFound.insert(stringValue)
                        found(code: stringValue)
                    }
                case .continuous:
                    if isPastScanInterval() {
                        found(code: stringValue)
                    }
                }
            }
        }
        //성공여부
        func isPastScanInterval() -> Bool {
            return Date().timeIntervalSince(lastTime) >= self.parent.scanInterval
        }
        // 성공
        func found(code: String) {
            lastTime = Date()
            parent.completion(.success(code))
        }
        //에러
        func didFail(reason: ScanError) {
            parent.completion(.failure(reason))
        }
    }
//시뮬레이터용
    #if targetEnvironment(simulator)
    public class ScannerViewController: UIViewController,UIImagePickerControllerDelegate,UINavigationControllerDelegate{
        var delegate: ScannerCoordinator?
        private let showViewfinder: Bool

        public init(showViewfinder: Bool = false) {
            self.showViewfinder = showViewfinder
            super.init(nibName: nil, bundle: nil)
        }

        required init?(coder: NSCoder) {
            fatalError("init(coder:) has not been implemented")
        }

        override public func loadView() {
            view = UIView()
            view.isUserInteractionEnabled = true
            let label = UILabel()
            label.translatesAutoresizingMaskIntoConstraints = false
            label.numberOfLines = 0

            label.text = "You're running in the simulator, which means the camera isn't available. Tap anywhere to send back some simulated data."
            label.textAlignment = .center
            let button = UIButton()
            button.translatesAutoresizingMaskIntoConstraints = false
            button.setTitle("Or tap here to select a custom image", for: .normal)
            button.setTitleColor(UIColor.systemBlue, for: .normal)
            button.setTitleColor(UIColor.gray, for: .highlighted)
            button.addTarget(self, action: #selector(self.openGallery), for: .touchUpInside)

            let stackView = UIStackView()
            stackView.translatesAutoresizingMaskIntoConstraints = false
            stackView.axis = .vertical
            stackView.spacing = 50
            stackView.addArrangedSubview(label)
            stackView.addArrangedSubview(button)

            view.addSubview(stackView)

            NSLayoutConstraint.activate([
                button.heightAnchor.constraint(equalToConstant: 50),
                stackView.leadingAnchor.constraint(equalTo: view.layoutMarginsGuide.leadingAnchor),
                stackView.trailingAnchor.constraint(equalTo: view.layoutMarginsGuide.trailingAnchor),
                stackView.centerYAnchor.constraint(equalTo: view.centerYAnchor)
            ])
        }

        override public func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
            guard let simulatedData = delegate?.parent.simulatedData else {
                print("Simulated Data Not Provided!")
                return
            }

            delegate?.found(code: simulatedData)
        }

        @objc func openGallery(_ sender: UIButton){
            let imagePicker = UIImagePickerController()
            imagePicker.delegate = self
            self.present(imagePicker, animated: true, completion: nil)
        }

        public func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [UIImagePickerController.InfoKey : Any]){
            if let qrcodeImg = info[.originalImage] as? UIImage {
                let detector:CIDetector=CIDetector(ofType: CIDetectorTypeQRCode, context: nil, options: [CIDetectorAccuracy:CIDetectorAccuracyHigh])!
                let ciImage:CIImage=CIImage(image:qrcodeImg)!
                var qrCodeLink=""

                let features=detector.features(in: ciImage)
                for feature in features as! [CIQRCodeFeature] {
                    qrCodeLink += feature.messageString!
                }

                if qrCodeLink=="" {
                    delegate?.didFail(reason: .badOutput)
                }else{
                    delegate?.found(code: qrCodeLink)
                }
            }
            else{
                print("Something went wrong")
            }
            self.dismiss(animated: true, completion: nil)
        }
    }
    //스캐닝뷰
    #else
    public class ScannerViewController: UIViewController {
        var captureSession: AVCaptureSession!
        var previewLayer: AVCaptureVideoPreviewLayer!
        var delegate: ScannerCoordinator?
        let videoCaptureDevice = AVCaptureDevice.default(for: .video)

        private let showViewfinder: Bool

        private lazy var viewFinder: UIImageView? = {
            guard let image = UIImage(named: "viewfinder") else {
                return nil
            }

            let imageView = UIImageView(image: image)
            imageView.translatesAutoresizingMaskIntoConstraints = false
            return imageView
        }()

        public init(showViewfinder: Bool) {
            self.showViewfinder = showViewfinder
            super.init(nibName: nil, bundle: nil)
        }

        public required init?(coder: NSCoder) {
            self.showViewfinder = false
            super.init(coder: coder)
        }

        override public func viewDidLoad() {
            super.viewDidLoad()


            NotificationCenter.default.addObserver(self,
                                                   selector: #selector(updateOrientation),
                                                   name: Notification.Name("UIDeviceOrientationDidChangeNotification"),
                                                   object: nil)

            view.backgroundColor = UIColor.black
            captureSession = AVCaptureSession()

            guard let videoCaptureDevice = videoCaptureDevice else {
                return
            }
// 비디오 캡쳐 (스캐닝뷰를 이미지화해서 캡쳐)
            guard let videoInput = try? AVCaptureDeviceInput(device: videoCaptureDevice) else { return }

            if (captureSession.canAddInput(videoInput)) {
                captureSession.addInput(videoInput)
            } else {
                delegate?.didFail(reason: .badInput)
                return
            }

            let metadataOutput = AVCaptureMetadataOutput()

            if (captureSession.canAddOutput(metadataOutput)) {
                captureSession.addOutput(metadataOutput)

                metadataOutput.setMetadataObjectsDelegate(delegate, queue: DispatchQueue.main)
                metadataOutput.metadataObjectTypes = delegate?.parent.codeTypes
            } else {
                delegate?.didFail(reason: .badOutput)
                return
            }
        }

        override public func viewWillLayoutSubviews() {
            previewLayer?.frame = view.layer.bounds
        }

        @objc func updateOrientation() {
            guard let orientation = view.window?.windowScene?.interfaceOrientation else { return }
            guard let connection = captureSession.connections.last, connection.isVideoOrientationSupported else { return }
            connection.videoOrientation = AVCaptureVideoOrientation(rawValue: orientation.rawValue) ?? .portrait
        }

        override public func viewDidAppear(_ animated: Bool) {
            super.viewDidAppear(animated)
            updateOrientation()
        }

        override public func viewWillAppear(_ animated: Bool) {
            super.viewWillAppear(animated)
            //스캐닝화면 쳐질때
            if previewLayer == nil {
                previewLayer = AVCaptureVideoPreviewLayer(session: captureSession)
            }
            previewLayer.frame = view.layer.bounds
            previewLayer.videoGravity = .resizeAspectFill
            view.layer.addSublayer(previewLayer)
            addviewfinder()

            delegate?.reset()

            if (captureSession?.isRunning == false) {
                captureSession.startRunning()
            }
        }
// 스캐닝화면안에 커스터마이징
        private func addviewfinder() {
            guard showViewfinder, let imageView = viewFinder else { return }
            
            view.addSubview(imageView)
            NSLayoutConstraint.activate([
                imageView.centerYAnchor.constraint(equalTo: view.centerYAnchor),
                imageView.centerXAnchor.constraint(equalTo: view.centerXAnchor),
                imageView.widthAnchor.constraint(equalToConstant: 200),
                imageView.heightAnchor.constraint(equalToConstant: 200),
            ])
        }

        override public func viewDidDisappear(_ animated: Bool) {
            super.viewDidDisappear(animated)

            if (captureSession?.isRunning == true) {
                captureSession.stopRunning()
            }

            NotificationCenter.default.removeObserver(self)
        }

        override public var prefersStatusBarHidden: Bool {
            return true
        }

        override public var supportedInterfaceOrientations: UIInterfaceOrientationMask {
            return .all
        }
        //초점맞추기
        /** Touch the screen for autofocus */
        public override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
            guard touches.first?.view == view,
                  let touchPoint = touches.first,
                  let device = videoCaptureDevice
            else { return }
            
            let videoView = view
            let screenSize = videoView!.bounds.size
            let xPoint = touchPoint.location(in: videoView).y / screenSize.height
            let yPoint = 1.0 - touchPoint.location(in: videoView).x / screenSize.width
            let focusPoint = CGPoint(x: xPoint, y: yPoint)
            
            do {
                try device.lockForConfiguration()
            } catch {
                return
            }
            
            // Focus to the correct point, make continiuous focus and exposure so the point stays sharp when moving the device closer
            device.focusPointOfInterest = focusPoint
            device.focusMode = .continuousAutoFocus
            device.exposurePointOfInterest = focusPoint
            device.exposureMode = AVCaptureDevice.ExposureMode.continuousAutoExposure
            device.unlockForConfiguration()
        }
    }
    #endif
//설정
    public let codeTypes: [AVMetadataObject.ObjectType]
    public let scanMode: ScanMode
    public let scanInterval: Double
    public let showViewfinder: Bool
    public var simulatedData = ""
    public var completion: (Result<String, ScanError>) -> Void

    public init(codeTypes: [AVMetadataObject.ObjectType], scanMode: ScanMode = .once, showViewfinder: Bool = false, scanInterval: Double = 2.0, simulatedData: String = "", completion: @escaping (Result<String, ScanError>) -> Void) {
        self.codeTypes = codeTypes
        self.scanMode = scanMode
        self.showViewfinder = showViewfinder
        self.scanInterval = scanInterval
        self.simulatedData = simulatedData
        self.completion = completion
    }

    public func makeCoordinator() -> ScannerCoordinator {
        return ScannerCoordinator(parent: self)
    }

    public func makeUIViewController(context: Context) -> ScannerViewController {
        let viewController = ScannerViewController(showViewfinder: showViewfinder)
        viewController.delegate = context.coordinator
        return viewController
    }

    public func updateUIViewController(_ uiViewController: ScannerViewController, context: Context) {

    }
}

struct CodeScannerView_Previews: PreviewProvider {
    static var previews: some View {
        CodeScannerView(codeTypes: [.qr]) { result in
            // do nothing
        }
    }
}
