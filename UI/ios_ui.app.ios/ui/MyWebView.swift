//
//  MyWebView.swift
//  ExSwiftUI
//
//  Created by reborn-m1macmini2 on 2021/09/08.
//

import SwiftUI
import WebKit

// Uikit 의 uiview 를 사용할수 있도록 한다.
// UIViewControllerRepresentable
struct MyWebView: UIViewRepresentable {
    
   
    var urlToLoad: String
    
    func makeUIView(context: Context) -> some WKWebView {
        // unwrapping
        guard let url = URL(string: self.urlToLoad)  else {
            return WKWebView()
            
        }
        
        // 웹뷰 인스턴스 생성
        let webview = WKWebView()
        
        // 웹뷰 로드 실행
        webview.load(URLRequest(url: url))
        
        
        return webview
    }
    func updateUIView(_ uiView: UIViewType, context: UIViewRepresentableContext<MyWebView>) {
        
    }
}

