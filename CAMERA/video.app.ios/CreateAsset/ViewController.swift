//
//  ViewController.swift
//  CreateAsset
//
//  Created by Zedd on 2020/01/28.
//  Copyright © 2020 Zedd. All rights reserved.
//

import UIKit
import Photos

class ViewController: UIViewController{

    override func viewDidLoad() {
        super.viewDidLoad()
    }

    @IBAction func show(_ sender: UIButton) {
        let viewController = UIImagePickerController()
        viewController.delegate = self
        viewController.sourceType = .camera
        viewController.mediaTypes = UIImagePickerController.availableMediaTypes(for: .camera) ?? []
       // viewController.mediaTypes = ["public.movie"]
        // 위 코드로 바꿀시 비디오로만 촬영 swiftui 에서도 적용가능
        self.present(viewController, animated: true, completion: nil)
    }
}

extension ViewController: UIImagePickerControllerDelegate, UINavigationControllerDelegate {
    
/*
    func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [UIImagePickerController.InfoKey : Any]) {
        // 이미지 저장.
        if let image = info[.originalImage] as? UIImage {
            UIImageWriteToSavedPhotosAlbum(image, self, #selector(savedImage), nil)
        }
        
        if let url = info[.mediaURL] as? URL, UIVideoAtPathIsCompatibleWithSavedPhotosAlbum(url.path) {
            UISaveVideoAtPathToSavedPhotosAlbum(url.path, self, #selector(savedVideo), nil)
        }
        picker.dismiss(animated: true, completion: nil)
    }
    
    @objc
    func savedImage(_ image: UIImage, didFinishSavingWithError error: Error?, contextInfo: UnsafeMutableRawPointer?) {
        if let error = error {
            print(error)
            return
        }
        print("success")
    }
    
    @objc
    func savedVideo(_ videoPath: String, didFinishSavingWithError error: Error?, contextInfo: UnsafeMutableRawPointer?) {
        if let error = error {
            print(error)
            return
        }
        print("success")
    }
*/
    func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [UIImagePickerController.InfoKey : Any]) {
        // 이미지 저장.
        if let image = info[.originalImage] as? UIImage {
            PHPhotoLibrary.shared().performChanges({
                PHAssetChangeRequest.creationRequestForAsset(from: image)
            }, completionHandler: { (success, error) in
                if success {
                    print("success")
                } else if let error = error {
                    print(error)
                }
            })
        }
        if let url = info[.mediaURL] as? URL, UIVideoAtPathIsCompatibleWithSavedPhotosAlbum(url.path) {
            PHPhotoLibrary.shared().performChanges({
                PHAssetChangeRequest.creationRequestForAssetFromVideo(atFileURL: url)
            }, completionHandler: { (success, error) in
                if success {
                    print("success")
                    do{//서버 전송용 데이터 변환 로직
                        let data = try Data(contentsOf: url,options: .mappedIfSafe)
                        print(data)
                    }
                    catch{
                        
                    }
                } else if let error = error {
                    print(error)
                }
            })
        }
        picker.dismiss(animated: true, completion: nil)
    }
}
