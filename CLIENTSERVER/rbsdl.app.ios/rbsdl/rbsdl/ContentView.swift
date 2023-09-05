//
//  ContentView.swift
//  rbsdl
//
//  Created by 강정윤 on 2021/07/26.
//

import SwiftUI
import CocoaMQTT
import Foundation
import AlertToast

var rbsdlkey: String?

class UserSettings: ObservableObject {
    
    @Published var username: String {
        didSet {
            UserDefaults.standard.set(username, forKey: "UserName")
        }
    }
    
    @Published var showToast:Bool
    
    init() {
        self.username = UserDefaults.standard.object(forKey: "UserName") as? String ?? ""
        self.showToast = false
    }
}


public struct ContentView: View {
    
    @ObservedObject var userSettings = UserSettings() // 해당ObservedObject 선언하여 ContentView에서 UserSettings클래스내 변수에 접근이 가능
    //@State private var showToast = false
    
    public var body: some View {
        ZStack {
            Color.black.edgesIgnoringSafeArea(/*@START_MENU_TOKEN@*/.all/*@END_MENU_TOKEN@*/)
            
            VStack(alignment: .center){
                Text("리본소프트 도어락테스트앱")
                    .font(.title)
                    .padding()
                    .foregroundColor(.white)
                Spacer()
                Text("이름을 입력하세요")
                    .font(.title3)
                    .foregroundColor(.white)
                HStack {
                    TextField("Enter your name", text: $userSettings.username)
                        .padding(.horizontal, 100.0)
                        .textFieldStyle(RoundedBorderTextFieldStyle())
                        .foregroundColor(.white)
                }
                .textFieldStyle(RoundedBorderTextFieldStyle())
                Text("Hello, \(userSettings.username)!")
                    .foregroundColor(.white)
                
                Spacer()
                
                //Text(LEAWrapper().leadec()).padding()
                MyButton(title: "도어락 열림",iconName: "o.circle", defaultname: $userSettings.username) // struct구조도 인수 인계가 가능
                Spacer()
                /*
                Button("Show Toast"){
                    //showToast.toggle()
                    userSettings.showToast.toggle()
                    
                }
                */
                Spacer()
                    
                    
                
                
            }
            
        }
        .toast(isPresenting: $userSettings.showToast){

                // `.alert` is the default displayMode
                //AlertToast(type: .regular, title: "Message Sent!")
                
                //Choose .hud to toast alert from the top of the screen
                //AlertToast(displayMode: .hud, type: .regular, title: "Message Sent!")
                
                //Choose .banner to slide/pop alert from the bottom of the screen
                AlertToast(displayMode: .banner(.slide), type: .regular, title: "Message Sent!")
            
        }
        
    }
    
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}

public struct MyButton: View {
    
    let mqttClient = CocoaMQTT(clientID: "iOSClinet", host: "192.168.1.168", port: 1883)

    var title: String
    var iconName: String
    
    @Binding var defaultname: String
    
    @ObservedObject var userSettings1 = UserSettings() // 해당ObservedObject 선언하여 ContentView에서 UserSettings클래스내 변수에 접근이 가능
    
   
    
    public var body: some View {
        Button( action: {
            userSettings1.showToast.toggle()
            self.mqttClient.username="rebornsoft2018"
            self.mqttClient.password="reborn2018"
            self.mqttClient.keepAlive=60
            self.mqttClient.autoReconnect = true
            self.mqttClient.connect()
            self.mqttClient.didConnectAck = { mqtt, ack in
                self.mqttClient.subscribe("rbsdoorlock/rbsdlkey")
                self.mqttClient.didReceiveMessage = { mqtt, message, id in
                    print("Message received in topic \(message.topic) with payload \(message.string!)")
                    
                    rbsdlkey = message.string!
                
                    if rbsdlkey == "off line" { // mqtt서버 retain 메시지 특성이용 제어 (해당값이 변경된다면 무한로직되는것을 테스트 필요
                        self.mqttClient.publish("rbsdoorlock/rbsdlkey", withString: "ESPREADYQ")
                    }
                    else if (rbsdlkey?.contains("ESPREADYOK")==true){ //ESPREADYOK## 형태로 메시지 받을시 처리로직, 형식과 다를 경우 제어진행 확인 필요
                        
                        let char1 = rbsdlkey?.utf8CString[10] // ESPREADYOK## #해당 데이터값 암호화로직에 이용
                        let char2 = rbsdlkey?.utf8CString[11] // ESPREADYOK## #해당 데이터값 암호화로직에 이용
                                                
                        self.mqttClient.publish("rbsdoorlock/rbsdlkey", withString: LEAWrapper().leadec(char1!, char2!)) // 도어락제어키 암호화하여 메시지 전송 OPENDL## 형태로 암호화 진행 (##은 랜덤) C++로직으로 구현
                    } else if rbsdlkey == "RFTEND" {
                        self.mqttClient.unsubscribe("rbsdoorlock/rbsdlkey")
                        self.mqttClient.disconnect()
                        
                        postUserName(userName: defaultname) // json API연결
                        
                        
                    }
                    
                }
            }
        }) {
            HStack {
                Image(systemName: iconName)
                    .font(.title3)
                Text(title)
                    .fontWeight(.semibold)
                    .font(.title3)
            }
            .padding()
            .foregroundColor(.white)
            .background(Color.blue)
            .cornerRadius(40)
        }
    }
}

struct PostUserName: Codable {
    let userName: String
}

func postUserName(userName: String) {

    // 넣는 순서도 순서대로여야 하는 것 같다.
    let comment = PostUserName(userName: userName)
    guard let uploadData = try? JSONEncoder().encode(comment)
    else {return}

    // URL 객체 정의
    let url = URL(string: "http://ai-rebornsoft.asuscomm.com:7733/m/test/door_lock_count")

    // URLRequest 객체를 정의
    var request = URLRequest(url: url!)
    request.httpMethod = "POST"
    // HTTP 메시지 헤더
    request.setValue("application/json", forHTTPHeaderField: "Content-Type")

    // URLSession 객체를 통해 전송, 응답값 처리
    let task = URLSession.shared.uploadTask(with: request, from: uploadData) { (data, response, error) in

        // 서버가 응답이 없거나 통신이 실패
        if let e = error {
            NSLog("An error has occured: \(e.localizedDescription)")
            return
        }
        // 응답 처리 로직
        print("comment post success")
    }
    // POST 전송
    task.resume()
    
    UserDefaults.standard.set(userName, forKey: "UserName")
}


