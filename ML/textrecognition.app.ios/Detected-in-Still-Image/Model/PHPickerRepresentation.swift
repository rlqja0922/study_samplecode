//
//  PHPickerRepresentable.swift
//  rbsfacerecognition
//
//  Created by reborn-mac-intel on 2021/08/25.
//

import SwiftUI
import PhotosUI

struct PHPickerRepresentable: UIViewControllerRepresentable {
    @Binding var pickedImages: [UIImage]
    let onDismiss: () -> Void
    private let picker: PHPickerViewController
    
    init(selectionLimit: Int, pickedImages: Binding<[UIImage]>, onDismiss: @escaping () -> Void) {
        var configuration = PHPickerConfiguration()
        configuration.selectionLimit = selectionLimit
        configuration.filter = .images
        self.picker = PHPickerViewController(configuration: configuration)
        self._pickedImages = pickedImages
        self.onDismiss = onDismiss
    }
    
    func makeUIViewController(context: UIViewControllerRepresentableContext<PHPickerRepresentable>) -> PHPickerViewController {
        picker.delegate = context.coordinator
        return picker
    }
    
    func makeCoordinator() -> Coordinator {
        Coordinator(control: self)
    }
    
    func updateUIViewController(_ uiViewController: PHPickerViewController, context: Context) {
        
    }
    
    final class Coordinator: NSObject, PHPickerViewControllerDelegate {
        let control: PHPickerRepresentable
        
        init(control: PHPickerRepresentable) {
            self.control = control
        }
        
        func picker(_ picker: PHPickerViewController, didFinishPicking results: [PHPickerResult]) {
            if results.isEmpty {
                picker.dismiss(animated: true, completion: nil)
                return
            }
            
            // 선택한 사진을 하나씩로드
            let dispatchSemaphore = DispatchSemaphore(value: 0)
            var pickedImage: [UIImage] = []
            for result in results.enumerated() {
                if !result.element.itemProvider.canLoadObject(ofClass: UIImage.self) { continue }
                result.element.itemProvider.loadObject(ofClass: UIImage.self) { (image, error) in
                    if let image: UIImage = image as? UIImage {
                        pickedImage.append(image)
                    }
                    dispatchSemaphore.signal()
                }
                dispatchSemaphore.wait()
            }

            DispatchQueue.main.async { [weak self] in
                self?.control.pickedImages = pickedImage
                picker.dismiss(animated: true) {
                    self?.control.onDismiss()
                }
            }
        }
    }
}
