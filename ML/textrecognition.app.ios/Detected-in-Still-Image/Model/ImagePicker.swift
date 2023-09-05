//
//  ImagePicker.swift
//  SwiftUICamera
//
//  Created by Mohammad Azam on 2/10/20.
//  Copyright © 2020 Mohammad Azam. All rights reserved.
//

import Foundation
import SwiftUI


struct ImagePicker: UIViewControllerRepresentable {
    
    typealias UIViewControllerType = UIImagePickerController
    typealias Coordinator = ImagePickerCoordinator
    
    @Binding var image: UIImage?
    @Binding var isShown: Bool
    var sourceType: UIImagePickerController.SourceType = .camera
    
    
    class ImagePickerCoordinator: NSObject, UINavigationControllerDelegate, UIImagePickerControllerDelegate {
        let parent:ImagePicker
//        @State var  imagePickerController = UIImagePickerController()
        @Binding var image: UIImage?
        @Binding var isShown: Bool
        init(_ parent: ImagePicker, image: Binding<UIImage?>, isShown: Binding<Bool>) {
            self.parent = parent
            _image = image
            _isShown = isShown
            
            
        }
        
        func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [UIImagePickerController.InfoKey : Any]) {
            if let uiImage = info[UIImagePickerController.InfoKey.originalImage] as? UIImage {
                parent.image=uiImage
                image = uiImage
                isShown = false
                print("선택")
                if parent.sourceType == .camera {
                    UIImageWriteToSavedPhotosAlbum(uiImage, uiImage, nil, nil)
                }
            }
        }
        
//        func imagePickerControllerDidCancel(_ picker: UIImagePickerController) {
//            isShown = false
//        }
    }
    
    
    
    func updateUIViewController(_ uiViewController: UIImagePickerController, context: UIViewControllerRepresentableContext<ImagePicker>) {
        
    }
    
    func makeCoordinator() -> ImagePickerCoordinator {
        return ImagePickerCoordinator(self, image: $image, isShown: $isShown)
    }
    
    func makeUIViewController(context: UIViewControllerRepresentableContext<ImagePicker>) -> UIImagePickerController {
        
        let picker = UIImagePickerController()
        picker.sourceType = sourceType
        picker.delegate = context.coordinator
        return picker
        
    }
    
    
}

