//
//  login.swift
//  register_login
//
//  Created by reborn on 2021/11/02.
//

import SwiftUI

struct login: View {
    @State var email : String = ""
    @State var password : String = ""
    @State private var isOn = true
    init()  {
        UISwitch.appearance().onTintColor = .gray
        UISwitch.appearance().thumbTintColor = .blue
    }
    var body: some View {
        VStack{
            VStack{
                Text("LOGIN")
                    .font(.title)
                    .fontWeight(.medium)
                    .padding()
            }
            .frame(minWidth: 250, alignment: .center)
            HStack{
                Image(systemName: "envelope")
                TextField("ID",text:$email)
                    .padding()
                    .background(RoundedRectangle(cornerRadius: 10).strokeBorder())
            }
            HStack{
                Image(systemName: "lock")
                SecureField("password",text:$password)
                    .padding()
                    .background(RoundedRectangle(cornerRadius: 10).strokeBorder())
            }
            HStack{
                Toggle(isOn : $isOn) {
                    Text("자동로그인")
                }
                .frame(width : 150, height: 40)
                Button(action: {}, label: {
                    Text("로그인")
                        .frame(width:80 , height: 10)
                        .padding()
                        .background(RoundedRectangle(cornerRadius: 10).strokeBorder())
                })
            }
        }
    }
}

struct login_Previews: PreviewProvider {
    static var previews: some View {
        login()
    }
}
