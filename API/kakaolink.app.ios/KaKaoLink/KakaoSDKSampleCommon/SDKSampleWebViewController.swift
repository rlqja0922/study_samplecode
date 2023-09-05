//  Copyright 2022 Kakao Corp.
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

import Foundation
import UIKit
import WebKit

class SDKSampleWebViewController : UIViewController, WKUIDelegate, WKNavigationDelegate, UITextFieldDelegate {
    
    var urlContentView: UIView!
    var urlInputField: UITextField!
    var closeBtn: UIButton!
    var clearCacheBtn: UIButton!
    var homeBtn: UIButton!
    var webViewContentView: UIView!
    var webViews = [WKWebView]()
    
    let homeUrl = "https://developers.kakao.com/tool/demo/login/login"
    
    var safeAreaLayoutGuide: UILayoutGuide!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.safeAreaLayoutGuide = view.layoutMarginsGuide
        
        urlContentView = UIView(frame: .zero)
        
        closeBtn = UIButton(frame: .zero)
        closeBtn.backgroundColor = .white
        closeBtn.setImage(UIImage(named: "close"), for: .normal)
        closeBtn.addTarget(self, action: #selector(handleCloseBtn), for: .touchUpInside)
        
        urlInputField = UITextField(frame: .zero)
        urlInputField.delegate = self
        urlInputField.keyboardType = .URL
        urlInputField.textColor = .black
        urlInputField.backgroundColor = #colorLiteral(red: 0.943228976, green: 0.943228976, blue: 0.943228976, alpha: 1)
        urlInputField.autocorrectionType = .no
        urlInputField.layer.masksToBounds = true
        urlInputField.layer.cornerRadius = 5
        urlInputField.text = homeUrl
        urlInputField.addLeftPadding()
        
        let refreshBtn = UIButton(frame: CGRect(x: 0, y: 0, width: 30, height: urlInputField.frame.height))
        refreshBtn.setImage(UIImage(named: "refresh"), for: .normal)
        refreshBtn.addTarget(self, action: #selector(handleRefreshBtn), for: .touchUpInside)
        urlInputField.rightView = refreshBtn
        urlInputField.rightViewMode = .always
        
        homeBtn = UIButton(frame: .zero)
        homeBtn.backgroundColor = .white
        homeBtn.setImage(UIImage(named: "AppIcon"), for: .normal)
        homeBtn.layer.masksToBounds = true
        homeBtn.layer.cornerRadius = 25/2
        homeBtn.addTarget(self, action: #selector(handleHomeBtn), for: .touchUpInside)
        
        clearCacheBtn = UIButton(frame: .zero)
        clearCacheBtn.backgroundColor = .white
        clearCacheBtn.setImage(UIImage(named: "trash"), for: .normal)
        clearCacheBtn.addTarget(self, action: #selector(handleClearCacheBtn), for: .touchUpInside)
        
        webViewContentView = UIView(frame: .zero)
        
        self.view.addSubview(urlContentView)
        self.view.addSubview(webViewContentView)
        urlContentView.addSubview(closeBtn)
        urlContentView.addSubview(urlInputField)
        urlContentView.addSubview(homeBtn)
        urlContentView.addSubview(clearCacheBtn)

       self.view.backgroundColor = .white
        
        if let url = URL(string: homeUrl) {
            let webView = createWebView(frame: webViewContentView.frame, configuration: WKWebViewConfiguration())
            webView.load(URLRequest(url: url))
        }
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        updateConstraints()
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
    }
    
    override func viewWillTransition(to size: CGSize, with coordinator: UIViewControllerTransitionCoordinator) {
        coordinator.animate { [unowned self] _ in
            updateConstraints()
        } completion: {  _ in
            
        }
        super.viewWillTransition(to: size, with: coordinator)
    }
    
    override func viewWillLayoutSubviews() {
        super.viewWillLayoutSubviews()
        for webView in webViews {
            webView.frame = webViewContentView.frame
        }
    }

    override var shouldAutorotate: Bool {
        return true
    }
    
    private func updateConstraints() {
        urlContentView.translatesAutoresizingMaskIntoConstraints = false
        urlContentView.leadingAnchor.constraint(equalTo: self.view.leadingAnchor).isActive = true
        urlContentView.trailingAnchor.constraint(equalTo: self.view.trailingAnchor).isActive = true
        urlContentView.topAnchor.constraint(equalTo: self.safeAreaLayoutGuide.topAnchor).isActive  = true
        urlContentView.bottomAnchor.constraint(equalTo: webViewContentView.topAnchor).isActive = true
        urlContentView.heightAnchor.constraint(equalToConstant: 60).isActive = true
        
        closeBtn.translatesAutoresizingMaskIntoConstraints = false
        closeBtn.leadingAnchor.constraint(equalTo: self.urlContentView.leadingAnchor).isActive = true
        closeBtn.trailingAnchor.constraint(equalTo: urlInputField.leadingAnchor).isActive = true
        closeBtn.heightAnchor.constraint(equalToConstant: 40).isActive = true
        closeBtn.widthAnchor.constraint(equalToConstant: 40).isActive = true
        closeBtn.centerYAnchor.constraint(equalTo: urlContentView.centerYAnchor).isActive = true
        
        urlInputField.translatesAutoresizingMaskIntoConstraints = false
        urlInputField.leadingAnchor.constraint(equalTo: closeBtn.trailingAnchor).isActive = true
        urlInputField.trailingAnchor.constraint(equalTo: clearCacheBtn.leadingAnchor, constant: -5.0).isActive = true
        urlInputField.heightAnchor.constraint(equalToConstant: 30).isActive = true
        urlInputField.centerYAnchor.constraint(equalTo: urlContentView.centerYAnchor).isActive = true
        
        clearCacheBtn.translatesAutoresizingMaskIntoConstraints = false
        clearCacheBtn.trailingAnchor.constraint(equalTo: homeBtn.leadingAnchor, constant: -7.0).isActive = true
        clearCacheBtn.heightAnchor.constraint(equalToConstant: 25).isActive = true
        clearCacheBtn.widthAnchor.constraint(equalToConstant: 25).isActive = true
        clearCacheBtn.centerYAnchor.constraint(equalTo: urlContentView.centerYAnchor).isActive = true
        
        homeBtn.translatesAutoresizingMaskIntoConstraints = false
        homeBtn.trailingAnchor.constraint(equalTo: self.view.trailingAnchor, constant: -5.0).isActive = true
        homeBtn.leadingAnchor.constraint(equalTo: clearCacheBtn.trailingAnchor, constant: 7.0).isActive = true
        homeBtn.heightAnchor.constraint(equalToConstant: 25).isActive = true
        homeBtn.widthAnchor.constraint(equalToConstant: 25).isActive = true
        homeBtn.centerYAnchor.constraint(equalTo: urlContentView.centerYAnchor).isActive = true
        
        webViewContentView.translatesAutoresizingMaskIntoConstraints = false
        webViewContentView.trailingAnchor.constraint(equalTo: self.view.trailingAnchor).isActive = true
        webViewContentView.leadingAnchor.constraint(equalTo: self.view.leadingAnchor).isActive = true
        webViewContentView.topAnchor.constraint(equalTo: urlContentView.bottomAnchor).isActive = true
        webViewContentView.bottomAnchor.constraint(equalTo: safeAreaLayoutGuide.bottomAnchor).isActive = true
    }
    
    @objc private func handleCloseBtn() {
        self.dismiss(animated: true)
    }
    
    @objc private func handleHomeBtn() {
        urlInputField.endEditing(true)
        
        urlInputField.text = homeUrl
        
        
        if let url = URL(string: homeUrl) {
            if let webView = webViews.last {
                webView.load(URLRequest(url: url))
            }
        }
    }
    
    @objc private func handleRefreshBtn() {
        urlInputField.endEditing(true)
        
        if let text = urlInputField.text, let url = URL(string: text) {
            if let webView = webViews.last {
                webView.load(URLRequest(url: url))
            }
        }
    }
    
    @objc private func handleClearCacheBtn() {

        let websiteDataTypes = NSSet(array: [WKWebsiteDataTypeDiskCache, WKWebsiteDataTypeMemoryCache, WKWebsiteDataTypeCookies])
        let date = NSDate(timeIntervalSince1970: 0)
        
        WKWebsiteDataStore.default().removeData(ofTypes: websiteDataTypes as! Set, modifiedSince: date as Date, completionHandler:{

            let alert = UIAlertController(title: "Clear", message: "cache", preferredStyle: .alert)
            alert.addAction(UIAlertAction(title: "OK", style: .default, handler: { (action) in
            }))
            self.present(alert, animated: true, completion: nil)
        })
    }
    
    func createWebView(frame: CGRect, configuration: WKWebViewConfiguration) -> WKWebView {
        let webView = WKWebView(frame: frame, configuration: configuration)
        
        webView.uiDelegate = self
        webView.navigationDelegate = self
        
        webView.autoresizingMask = [.flexibleWidth, .flexibleHeight]
        webView.allowsBackForwardNavigationGestures = true
        
        self.view.addSubview(webView)
        self.webViews.append(webView)
        
        return webView
    }
    
}


extension SDKSampleWebViewController {
    
    /// ---------- 팝업 열기 ----------
    /// - 카카오 JavaScript SDK의 로그인 기능은 popup을 이용합니다.
    /// - window.open() 호출 시 별도 팝업 webview가 생성되어야 합니다.
    ///
    func webView(_ webView: WKWebView, createWebViewWith configuration: WKWebViewConfiguration, for navigationAction: WKNavigationAction, windowFeatures: WKWindowFeatures) -> WKWebView? {
        guard let frame = self.webViews.last?.frame else {
            return nil
        }
        return createWebView(frame: frame, configuration: configuration)
    }
    
    /// ---------- 팝업 닫기 ----------
    /// - window.close()가 호출되면 앞에서 생성한 팝업 webview를 닫아야 합니다.
    ///
    func webViewDidClose(_ webView: WKWebView) {
        self.webViews.popLast()?.removeFromSuperview()
    }
    
    
    func webView(_ webView: WKWebView, runJavaScriptAlertPanelWithMessage message: String, initiatedByFrame frame: WKFrameInfo, completionHandler: @escaping () -> Void) {
        let alert = UIAlertController(title: webView.url?.host, message: message, preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "OK", style: .default, handler: { (action) in
            completionHandler()
        }))
        self.present(alert, animated: true, completion: nil)
    }
    
    func webView(_ webView: WKWebView, runJavaScriptConfirmPanelWithMessage message: String, initiatedByFrame frame: WKFrameInfo, completionHandler: @escaping (Bool) -> Void) {
        let alert = UIAlertController(title: webView.url?.host, message: message, preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "Cancel", style: .default, handler: { (action) in
            completionHandler(false)
        }))
        alert.addAction(UIAlertAction(title: "OK", style: .default, handler: { (action) in
            completionHandler(true)
        }))
        self.present(alert, animated: true, completion: nil)
    }
    
    func webView(_ webView: WKWebView,
                 decidePolicyFor navigationAction: WKNavigationAction,
                 decisionHandler: @escaping (WKNavigationActionPolicy) -> Void
    ) {
        print(navigationAction.request.url?.absoluteString ?? "")
        
        // 카카오 SDK가 호출하는 커스텀 스킴인 경우 open(_ url:) 메소드를 호출합니다.
        if let url = navigationAction.request.url
            , ["kakaokompassauth", "kakaolink"].contains(url.scheme) {

            // 카카오톡 실행
            UIApplication.shared.open(url, options: [:], completionHandler: nil)
            
            decisionHandler(.cancel)
            return
        }
        
        // 서비스 상황에 맞는 나머지 로직을 구현합니다.

        
        decisionHandler(.allow)
    }

    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        textField.endEditing(true)
        handleRefreshBtn()
        return true
    }

}


extension UITextField {
    func addLeftPadding() {
      let paddingView = UIView(frame: CGRect(x: 0, y: 0, width: 10, height: self.frame.height))
      self.leftView = paddingView
      self.leftViewMode = ViewMode.always
    }
}
