//
//  ImagePickerPresentationData.swift
//  onwalktest
//
//  Created by reborn-m1macmini1 on 2021/09/06.
//

import SwiftUI

 struct ImagePickerPresentationData: Identifiable {
    enum Presentation: View {
        // ImagePicker
        case picker(image: Binding<UIImage?>, sourceType: UIImagePickerController.SourceType, isShown: Binding<Bool>)
        
        // PHPicker
        case phpicker(pickedImages: Binding<[UIImage]>, selectionLimit: Int, onDismiss: () -> Void)
        
        var body: some View {
            switch self {
            case .picker(let image, let sourceType, let isShown):
                return AnyView(ImagePicker(image: image, isShown: isShown, sourceType: sourceType))
                
            case .phpicker(let pickedImages, let selectionLimit, let onDismiss):
                return AnyView(PHPickerRepresentable(selectionLimit: selectionLimit, pickedImages: pickedImages, onDismiss: onDismiss))
            }
        }
    }
    
    var id: String = UUID().uuidString
    
    var presentation: Presentation?
    
    init(presentation: Presentation?) {
        self.presentation = presentation
    }
}
