//
//  startView.swift
//  KaKaoLogin
//
//  Created by reborn-m1macmini2 on 2021/11/03.
//

import SwiftUI
import KakaoSDKCommon
import KakaoSDKAuth
import KakaoSDKUser


struct goInitView: View {
    @AppStorage("firstNaviLinkActive") var firstNaviLinkActive : Bool = UserDefaults.standard.bool(forKey: "firstNaviLinkActive")
    @Environment(\.presentationMode) var presentationMode: Binding<PresentationMode>
    var body: some View {
        NavigationView{
                NavigationLink(destination: ContentView(), isActive: $firstNaviLinkActive){}
        
    
    }
    
}
}

struct LoginView: View {

    @Environment(\.presentationMode) var presentationMode: Binding<PresentationMode>
    @State var isActive = false

    var body: some View {
        
        NavigationView{
            VStack {
                NavigationLink(
                    destination: ContentView(),
                    isActive: $isActive){
                    Button(action: {
                        
                        checkToken()
                    }){
                        Image("kakao_login_medium_narrow")
                              .renderingMode(.original)
                    }
                }
            }
                
            }
        
         .onOpenURL(perform: { url in
            if (AuthApi.isKakaoTalkLoginUrl(url)) {
                _ = AuthController.handleOpenUrl(url: url)
            
        
            }
        })
    }
        
    
    func checkToken(){
        if (AuthApi.hasToken()) {
            UserApi.shared.accessTokenInfo { (token, error) in
                if let error = error {
                    if let sdkError = error as? SdkError, sdkError.isInvalidTokenError() == true  {
                        //로그인 필요
                        print("token 1: \(token)")
                        login()
                    }
                    else {
                        //기타 에러
                        print("token 2: \(token)")
                    }
                }
                else {
                    ContentView().userApi()
                    login()
                    print("token 3: \(token)")
                   
                    //토큰 유효성 체크 성공(필요 시 토큰 갱신됨)
                }
            }
        }
        else {
            login()
            print("로그인 필요")
            //로그인 필요
        }
    }
func login(){
    //            카카오톡이 깔려있는지 확인하는 함수
                if (UserApi.isKakaoTalkLoginAvailable()) {
                    //카카오톡이 설치되어있다면 카카오톡을 통한 로그인 진행
                    UserApi.shared.loginWithKakaoTalk {(oauthToken, error) in
                        print(oauthToken?.accessToken)
                        print("oauthToken \(oauthToken)")
                        print("error : \(error)")
                        if oauthToken?.accessToken != "" {
                            self.isActive = true
                        }

                    }
                }else{
                    //카카오톡이 설치되어있지 않다면 사파리를 통한 로그인 진행
                    UserApi.shared.loginWithKakaoAccount {(oauthToken, error) in
    //                    userApi()

                        print(" token : \(oauthToken?.accessToken)")
                        print(" oauthtoken : \(oauthToken)")
                        print("error : \(error)")
                        if oauthToken?.accessToken == "" || oauthToken?.accessToken == nil{
                                   
                        }else {
                            self.isActive = true
                        }

                    }
                }

}
}
 

    



