//
//  ContentView.swift
//  lea_wrapping
//
//  Created by reborn-mac-intel on 2021/11/01.
//

import SwiftUI

class UserSettings: ObservableObject {
    
    @Published var txt1 : String {
        didSet {
            UserDefaults.standard.set(txt1, forKey: "TXT1")
        }
    }
    
    init() {
        self.txt1 = UserDefaults.standard.object(forKey: "TXT1") as? String ?? ""
    }
    
}
struct ContentView: View {

    let char1 = 0x61 // A
    let char2 = 0x62 // B
    @ObservedObject var userSettings = UserSettings()

    var body: some View {
        VStack{
            Text("평문 : OPENDL##(AB)")
            Button(action: {
                userSettings.txt1 = LEAWrapper().leadec(CChar(char1), CChar(char2))
            }) {
                Text("LEA암호화")
            }
            Text("암호화문 : \(userSettings.txt1)")
            
        }
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
