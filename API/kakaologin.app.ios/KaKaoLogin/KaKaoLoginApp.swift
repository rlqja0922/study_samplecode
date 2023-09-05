//
//  KaKaoLoginApp.swift
//  KaKaoLogin
//
//  Created by reborn-m1macmini2 on 2021/11/02.
//

import SwiftUI
import KakaoSDKCommon
import KakaoSDKAuth

@main
struct KaKaoLoginApp: App {
    init() {
        // Kakao SDK 초기화
        KakaoSDKCommon.initSDK(appKey: "2bd0a116140b1ac6a468fa66eb84f9b0")
    }

    var body: some Scene {
        WindowGroup {
            LoginView()
            // onOpenURL()을 사용해 커스텀 URL 스킴 처리
//            ContentView().onOpenURL(perform: { url in
//                if (AuthApi.isKakaoTalkLoginUrl(url)) {
//                    AuthController.handleOpenUrl(url: url)
//                }
//            })
        }
    }
    

}

