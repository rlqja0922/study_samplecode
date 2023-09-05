//  Copyright 2019 Kakao Corp.
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.

import UIKit

import KakaoSDKCommon

public class SDKDebugNavigationController : UINavigationController {
}


public class SDKDebugViewController: UIViewController, UITextViewDelegate {
    static public let shared : SDKDebugViewController = SDKDebugViewController()

    var safeArea: UILayoutGuide!
    var debugTextView: UITextView!
   
    public init() {
        super.init(nibName: nil, bundle: nil)
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("Never will happen")
    }
    
    override public func loadView() {
        super.loadView()
        
        self.safeArea = view.layoutMarginsGuide
        
        setupViews()
    }

    override public func viewDidLoad() {
        super.viewDidLoad()

        self.navigationItem.rightBarButtonItems = [UIBarButtonItem(title: "Close", style: UIBarButtonItem.Style.plain, target: self, action: #selector(self.close)),
                                                   UIBarButtonItem(title: "ClearLog", style: UIBarButtonItem.Style.plain, target: self, action: #selector(self.clearLog))]
        self.title = "SDK Sample Log"
    }
    
    override public func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
    }
    
    public func setupViews() {
        self.debugTextView = UITextView(frame: self.view.frame)
        self.debugTextView.backgroundColor = UIColor.black
        self.debugTextView.font = UIFont.systemFont(ofSize: 12)
        self.debugTextView.textColor = UIColor.green
        self.debugTextView.isEditable = false
        self.debugTextView.isScrollEnabled = true
        self.debugTextView.isUserInteractionEnabled = true
        self.debugTextView.scrollsToTop = false
        self.debugTextView.showsVerticalScrollIndicator = true
        self.debugTextView.delegate = self
        self.view.addSubview(self.debugTextView)
        
        self.debugTextView.translatesAutoresizingMaskIntoConstraints = false
        self.debugTextView.topAnchor.constraint(equalTo: safeArea.topAnchor).isActive = true
        self.debugTextView.leftAnchor.constraint(equalTo: view.leftAnchor).isActive = true
        self.debugTextView.bottomAnchor.constraint(equalTo: safeArea.bottomAnchor).isActive = true
        self.debugTextView.rightAnchor.constraint(equalTo: view.rightAnchor).isActive = true
    }
    
    public func showLog() {        
        DispatchQueue.main.async {[self] in
            
            if self.debugTextView != nil {
                
                let debugLog = SdkLog.shared.debugLog
                self.debugTextView.text = debugLog
                print("self.debugTextView.contentSize.height:\(self.debugTextView.contentSize.height),  self.debugTextView.bounds.height:\(self.debugTextView.bounds.height) self.debugTextView.frame.size.height:\(self.debugTextView.frame.size.height)")
                self.debugTextView.scrollRangeToVisible(NSRange(location:debugLog.count, length:  0))
                print("self.debugTextView.contentOffset:\(self.debugTextView.contentOffset.x), \(self.debugTextView.contentOffset.y)")
            }
        }
    }
    
    @objc func close() {
        self.dismiss(animated: true) {
            
        }
    }
    
    @objc func clearLog() {
        let alert = UIAlertController(title: "Clear Log", message: "really clear?", preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "OK", style: .default, handler:{ (UIAlertAction) in            
            SdkLog.shared.clearLog()
            let debugLog = SdkLog.shared.debugLog
            self.debugTextView.text = debugLog
            self.debugTextView.scrollRangeToVisible(NSRange(location:debugLog.count, length:  0))
        }))
        alert.addAction(UIAlertAction(title: "cancel", style: .cancel, handler:{ (UIAlertAction) in
        }))
        self.present(alert, animated: true, completion: {
            print("completion block")
        })
    }
}
