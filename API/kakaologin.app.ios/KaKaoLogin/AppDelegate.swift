//
//  AppDelegate.swift
//  KaKaoLogin
//
//  Created by reborn-m1macmini2 on 2021/11/04.
//

import Foundation
import KakaoSDKAuth

class AppDelegate: UIResponder, UIApplicationDelegate {
    
    func application(_ app: UIApplication, open url: URL, options: [UIApplication.OpenURLOptionsKey : Any] = [:]) -> Bool {
        if (AuthApi.isKakaoTalkLoginUrl(url)) {
            return AuthController.handleOpenUrl(url: url)
        }

        return false
    }
    
}
