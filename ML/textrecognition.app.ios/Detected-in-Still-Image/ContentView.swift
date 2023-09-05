//
//  ContentView.swift
//  Detected-in-Still-Image
//
//  Created by satoutakeshi on 2021/06/15.
//

import SwiftUI
import AVFoundation

struct ContentView: View {
    
    @State private var presentation: ImagePickerPresentationData?
    @State var  imagePickerController = UIImagePickerController()
    @State var isPresent:Bool = false
    @State private var showSheet: Bool = false
    @State private var showImagePicker: Bool = false
    @State private var sourceType: UIImagePickerController.SourceType = .camera
    @State var sheetNum:Int = 0
    @State var image: UIImage?
    

    func checkCameraPermission() {
        AVCaptureDevice.requestAccess(for: .video, completionHandler: {(granted: Bool) in
            if granted {
                print("Camera 권한 허용")
            } else {
                print("Camera 권한 거부")
            }
            
        })
    }
    
    var body: some View {
    
        GeometryReader{ geometry in
            
            NavigationView {
                VStack(alignment: .center){
                    
                    Form {
                        
                        List {
                            NavigationLink("Text", destination: DetectorView(image: image ?? UIImage(named: "testimg")!, requestType: [.text])) //
                        }
                        List { // 이미지 변수에 현 사진첩으로 등록한 이미지를 보냄
                            NavigationLink("Text Recognize", destination: DetectorView(image: image ??  UIImage(named: "testimg")!, requestType: [.textRecognize]))
                        }
            
                    }.offset(y: 20)
                    Text("텍스트 이미지를 등록 해주세요")
                        
                    Image(uiImage: image ?? UIImage(named: "testimg")!) // 사진첩, 카메라 등록사진을 보여줌
                        .resizable()
                        .frame(width: geometry.size.width * 0.8,height: geometry.size.height * 0.3)
//                        .overlay(RoundedRectangle(cornerRadius: 10))
                            .offset(y: -100)
                    Button(action: {
                        self.showSheet = true
                    }, label: {
                        Text("텍스트 이미지 등록")
                            .foregroundColor(.white)
                    })
                    .frame(width: geometry.size.width * 0.8,height: geometry.size.height * 0.1)
                    
                    .background(Color.blue)
                    .padding(.bottom)
                    
                    .actionSheet(isPresented: $showSheet) {
                        ActionSheet(title: Text("Select Photo"), message: Text("Choose"), buttons: [
                            .default(Text("Photo Library")) {
                                // 사진첩
                                self.showImagePicker = true
                                sheetNum = 1
                            },
                            .default(Text("Camera")) {
                                // 카메라
                                self.showImagePicker = true
                                sheetNum = 2
                            },
                            .cancel()
                        ])
                    
                }

                }
               
                .sheet(isPresented: $showImagePicker){
                    if sheetNum == 1 {
                        ImagePicker(image: self.$image, isShown: self.$showImagePicker, sourceType: .photoLibrary)
                    }
                    else if sheetNum == 2 {
                        ImagePicker(image: self.$image, isShown: self.$showImagePicker, sourceType: .camera)
                    }
                }
                .navigationTitle("이미지 텍스트 인식 테스트")
               
            }
            
        }

    }
}



struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}


