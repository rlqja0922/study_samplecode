//
//  ContentView.swift
//  KaKaoLogin
//
//  Created by reborn-m1macmini2 on 2021/11/02.
//

import SwiftUI
import KakaoSDKCommon
import KakaoSDKAuth
import KakaoSDKUser
import Foundation
import Combine


struct initView: View {
    var body: some View  {
        ContentView()
    }
    
}

//struct LoginInfoView: View {
//    var body: some View {
//
//
//    }
//}

struct ContentView: View {
    @Environment(\.presentationMode) var presentation
    @State var fullDissmiss:Bool = false
    @State var Mynickname = UserDefaults.standard.string(forKey: "nickname") ?? ""
    @State var MyEmail = UserDefaults.standard.string(forKey: "email") ?? ""
    @State var profileImageUrl = UserDefaults.standard.url(forKey: "profileImageUrl")
    @State var image: UIImage?
    @State var datas: String?
    
    init() {
        userApi()
        
        print("MynickName : \(Mynickname)")
        print("MyEmail : \(MyEmail)")
        print("profileImageUrl : \(profileImageUrl)")
        print("urlString : \(String(describing: profileImageUrl))")
        
        let data: String = (String(describing: profileImageUrl))
        
        print("data : \(data)")
        UserDefaults.standard.set(data, forKey: "profileImageUrl")
        datafunc()
        print("자료형 : ",type(of:data))
        print("image : \(image)")
    }
    
    func datafunc(){
    
       
        let data = UserDefaults.standard.url(forKey: "profileImageUrl")
//        let data: String = (String(describing: profileImageUrl))
        //url에 정확한 이미지 url 주소를 넣는다.
//        let url = URL(string: "https://k.kakaocdn.net/dn/nnJ4v/btrjPNBiaeM/Ra6OUxTg5cbER9Po1l4VHk/img_640x640.jpg")
        print("profileImageUrl : \(profileImageUrl)")
        print("dataFunc data : \(data)")
        guard let urlData = profileImageUrl else {
            return
        }
        print("url : \(urlData)")
        
        guard let data1 = try? Data(contentsOf: urlData ) else {
    
                return
            }
                image = UIImage(data: data1)
            
    }
    
    var body: some View {
        
        VStack() {
            Image(uiImage: (image ?? UIImage(named: "profile"))!)
                .resizable()
                .frame(width: 250, height: 250)
            Spacer().frame(height: 80)
            VStack(alignment: .leading){
                HStack() {
                    Text("이름 :")
                    .font(/*@START_MENU_TOKEN@*/.title/*@END_MENU_TOKEN@*/)
                    .fontWeight(.bold)
                    Text(Mynickname ?? String("닉네임을 불러오지 못했습니다."))
                        .font(.title3)
                        .fontWeight(.bold)
                }
                Spacer().frame(height: 30)
                HStack {
                    Text("이메일 :")
                    .font(/*@START_MENU_TOKEN@*/.title/*@END_MENU_TOKEN@*/)
                    .fontWeight(.bold)
                    Text(MyEmail ?? "이메일을 불러오지 못했습니다.")
                        .font(.title3)
                        .fontWeight(.bold)
                }
                
            }
            
//            Spacer().frame(height: 80)
//            Button(action : {
//
//                //카카오톡이 깔려있는지 확인하는 함수
//                if (UserApi.isKakaoTalkLoginAvailable()) {
//                    //카카오톡이 설치되어있다면 카카오톡을 통한 로그인 진행
//                    UserApi.shared.loginWithKakaoTalk {(oauthToken, error) in
//                        userApi()
//                        print(oauthToken?.accessToken)
//                        print(error)
//                    }
//                }else{
//                    //카카오톡이 설치되어있지 않다면 사파리를 통한 로그인 진행
//                    UserApi.shared.loginWithKakaoAccount {(oauthToken, error) in
//                        userApi()
//                        print(oauthToken?.accessToken)
//                        print(error)
//                    }
//                }
//            }
//            ){
//                Image("kakao_login_medium_narrow")
//                    .renderingMode(.original)
//            }
            Spacer().frame(height: 40 )
            Button(action : {
                // 로그아웃 토큰 삭제 연결끊기
                UserApi.shared.logout {(error) in
                    if let error = error {
                        print("로그아웃 에러 : \(error)")
                    }
                    else {
                        UserDefaults.standard.removeObject(forKey: "nickname")
                        UserDefaults.standard.removeObject(forKey: "email")
                        UserDefaults.standard.removeObject(forKey: "profileImageUrl")
                        self.presentation.wrappedValue.dismiss()
                        print("unlink() success.")
                    }
                }}
            ){
                Text("로그아웃")
                    
                    .frame(width: 180, height: 38)
                    .overlay(RoundedRectangle(cornerRadius: 5).stroke(Color(hex: "#FEE500"), lineWidth: 5))
                    .foregroundColor(.black)
                    .background(Color(hex: "#FEE500"))
                   
            }
            
            //ios가 버전이 올라감에 따라 sceneDelegate를 더이상 사용하지 않게되었다
            //그래서 로그인을 한후 리턴값을 인식을 하여야하는데 해당 코드를 적어주지않으면 리턴값을 인식되지않는다
            //swiftUI로 바뀌면서 가장큰 차이점이다.
            .onOpenURL(perform: { url in
                if (AuthApi.isKakaoTalkLoginUrl(url)) {
                    _ = AuthController.handleOpenUrl(url: url)
                    datafunc()
                    
                    userApi()
            
                }
            })
        }
        .onAppear(perform: {
            print("profileImageUrl : \(profileImageUrl)")
            datafunc()
            userApi()
//            userProfile()
        })
    }
    func userProfile() {
        UserApi.shared.updateProfile(properties: ["nickname":"update_profile_test"]) {(error) in
            if let error = error {
                print(error)
            }
            else {
                print("updateProfile() success.")
            }
        }
    }
     func userApi() {
        UserApi.shared.me() {(user, error) in
            if let error = error {
                print(error)
            }
            else {
                
                if let user = user {

                    //필요한 scope을 아래의 예제코드를 참고해서 추가한다.
//                    if (user.kakaoAccount?.profileNeedsAgreement == true) {
                        if (((user.kakaoAccount?.profile?.nickname) != nil)) {
                           let name = user.kakaoAccount?.profile?.nickname

                            UserDefaults.standard.set(name, forKey: "nickname")
                            
                            print("name : \(name)")
                            
                        }
                        if (((user.kakaoAccount?.profile?.thumbnailImageUrl) != nil)) {
                           let profileImageUrl = user.kakaoAccount?.profile?.thumbnailImageUrl
//                            @AppStorage("nickname") var nickname : URL =
//                            UserDefaults.standard.setValue(user.kakaoAccount?.profile?.profileImageUrl, forKey: "profileImageUrl")
                            print("profileImageUrl : \(profileImageUrl)")
                            
                            print("자료형 : ",type(of:profileImageUrl))
                        }
                        
//                    }
//                        if (user.kakaoAccount?.emailNeedsAgreement == true) {
                            if (((user.kakaoAccount?.email) != nil)) {
                               let email =  user.kakaoAccount?.email
                                UserDefaults.standard.set(email, forKey: "email")
                                print("email : \(email)")
                            }
                            
                                
                    let profileImageUrl = user.kakaoAccount?.profile?.thumbnailImageUrl
                    UserDefaults.standard.set(profileImageUrl, forKey: "profileImageUrl")
                    


                    

                }
            }
        }
                            
    }
}

// hex 코드를 사용하기 위한 Color Extension
// ColorExtentsion.swift


extension Color {
  init(hex: String) {
    let scanner = Scanner(string: hex)
    _ = scanner.scanString("#")
    
    var rgb: UInt64 = 0
    scanner.scanHexInt64(&rgb)
    
    let r = Double((rgb >> 16) & 0xFF) / 255.0
    let g = Double((rgb >>  8) & 0xFF) / 255.0
    let b = Double((rgb >>  0) & 0xFF) / 255.0
    self.init(red: r, green: g, blue: b)
  }
}


