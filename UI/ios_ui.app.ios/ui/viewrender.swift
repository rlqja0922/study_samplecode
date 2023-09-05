//
//  view2.swift
//  kb
//
//  Created by reborn on 2021/09/07.
//

import SwiftUI
import CoreMotion
import AVFoundation

extension View {
    //화면을 캡쳐해서 이미지화
    func asImage() -> UIImage {
        let controller = UIHostingController(rootView: self)

        // locate far out of screen
        controller.view.frame = CGRect(x: 0, y: CGFloat(Int.max), width: 1, height: 1)
        UIApplication.shared.windows.first!.rootViewController?.view.addSubview(controller.view)

        let size = controller.sizeThatFits(in: UIScreen.main.bounds.size)
        controller.view.bounds = CGRect(origin: .zero, size: size)
        controller.view.sizeToFit()

        let image = controller.view.asImage()
        controller.view.removeFromSuperview()
        return image
    }
}

extension UIView {
    //화면을 캡쳐해서 이미지화
    func asImage() -> UIImage {
        let renderer = UIGraphicsImageRenderer(bounds: bounds)
        return renderer.image { rendererContext in
// [!!] Uncomment to clip resulting image
//             rendererContext.cgContext.addPath(
//                UIBezierPath(roundedRect: bounds, cornerRadius: 20).cgPath)
//            rendererContext.cgContext.clip()

// As commented by @MaxIsom below in some cases might be needed
// to make this asynchronously, so uncomment below DispatchQueue
// if you'd same met crash
//            DispatchQueue.main.async {
                 layer.render(in: rendererContext.cgContext)
//            }
        }
    }
}
import Combine
import CoreMotion
class MotionManager: ObservableObject {
    //센서자이로매니저
    private var motionManager: CMMotionManager
    @Published
    var x: Double = 0.0
    @Published
    var y: Double = 0.0
    @Published
    var z: Double = 0.0
    init() {
        self.motionManager = CMMotionManager()
        //1초마다받기
        self.motionManager.magnetometerUpdateInterval = 1
        //에러처리
        self.motionManager.startMagnetometerUpdates(to: .main) { (magnetometerData, error) in
            guard error == nil else {
                print(error!)
                return
            }
            if let magnetData = magnetometerData {
                self.x = magnetData.magneticField.x
                self.y = magnetData.magneticField.y
                self.z = magnetData.magneticField.z
            }
        }
    }
}
import Foundation

class ScannerViewModel: ObservableObject {
    
    /// Defines how often we are going to try looking for a new QR-code in the camera feed.
    let scanInterval: Double = 1.0
    
    @Published var torchIsOn: Bool = false
    @Published var lastQrCode: String = "Qr-code goes here"
    
    
    func onFoundQrCode(_ code: String) {
        self.lastQrCode = code
    }
}
struct viewrender : View {
 @State var image : Image? = nil
    @State private var showingScanner = false
    @Environment(\.presentationMode) var presentation
    @ObservedObject
       var motion: MotionManager
    @ObservedObject var viewModel = ScannerViewModel()
    @State var scan1 : String = "scan"
    @State var isPresentingScanner = false
    @State var scannedCode: String?
    var textView: some View {

        ZStack {
            VStack{
                Text("자이로센서")
                    .padding()
                    .background(Color.blue)
                    .foregroundColor(.white)
                    .clipShape(Capsule())
                Text("Magnetometer Data")
                            Text("X: \(motion.x)")
                            Text("Y: \(motion.y)")
                            Text("Z: \(motion.z)")
            }
        }
    }
    
    var body: some View {
            VStack {
                
                textView
              
                Image(uiImage:render())
                    .border(Color.black)

               
            }
        }
    private func render() -> UIImage {
        textView.asImage()
    }
}


struct view2_Previews: PreviewProvider {
    static var previews: some View {
        viewrender(motion: MotionManager())
    }
}
