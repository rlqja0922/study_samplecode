//
//  ContentView.swift
//  register_login
//
//  Created by reborn on 2021/11/02.
//

import SwiftUI

struct ContentView: View {
    @State var view1 = true
    @State var view2 = false
    @State var view3 = false
    @State var view4 = false
    @State var view5 = false
    var body: some View {
        VStack{
            HStack{
                Button(action: {
                    view1 = true
                    view2 = false
                    view3 = false
                    view4 = false
                    view5 = false
                }, label: {Text("회원가입")})
                Button(action: {
                    view1 = false
                    view2 = true
                    view3 = false
                    view4 = false
                    view5 = false
                }, label: {Text("로그인")})
                Button(action: {
                    view1 = false
                    view2 = false
                    view3 = true
                    view4 = false
                    view5 = false
                }, label: {Text("프로필")})
                Button(action: {
                    view1 = false
                    view2 = false
                    view3 = false
                    view4 = true
                    view5 = false
                }, label: {Text("메뉴")})
                Button(action: {
                    view1 = false
                    view2 = false
                    view3 = false
                    view4 = false
                    view5 = true
                }, label: {Text("리스트")})
            }
            VStack{
                if view1 == true{
                    register()
                }else {
                    
                }
                if view2 == true{
                    login()
                }else {
                    
                }
                if view3 == true{
                    profile()
                }else {
                    
                }
                if view4 == true{
                    menu()
                }else {
                    
                }
                if view5 == true{
                    list()
                }else {
                    
                }
            }
               
            
        }
    }
}


struct register: View {
    
    let genderType = ["남성","여성","비밀"]
    
    @State var name = ""
    @State var gender = 0
    @State var bornIn = 0
    
    var resultScript:String{
        if(name.isEmpty){
            return "이름을 입력해주세요."
        }else{
            return "\(name)님은 성별이 \(genderType[gender])이며 나이는 \(120 - bornIn)입니다."
        }
        
    }
    
    
    var body: some View {
        NavigationView{
            Form{
                Section(header: Text("이름")){
                    TextField("이름을 입력해주세요", text: $name)
                        .keyboardType(.default) //키보드를 기본 키보드로 보여줍니다.
                }
                
                Section(header: Text("생년월일")){
                    //선택하는 값을 bornIn 변수에 할당합니다.
                    Picker("출생년도",selection: $bornIn){
                        // 1900부터 2018까지 Text를 만듭니다.
                        ForEach(1900 ..< 2019 ){
                            Text("\(String($0))년생")
                        }
                    }
                }
                

                Section(header: Text("성별")){
                    //선택하는 값을 bornIn 변수에 할당합니다.
                    Picker("성별",selection: $gender){
                        // 1900부터 2018까지 Text를 만듭니다.
                        ForEach( 0  ..< genderType.count ){
                            Text("\(self.genderType[$0])")
                        }
                    }.pickerStyle(SegmentedPickerStyle())
                }
                
                
                Section(header: Text("결과")){
                    Text("\(resultScript)")
                }
            }.navigationBarTitle("회원가입")
        }
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
