//
//  ContentView.swift
//  ui
//
//  Created by reborn on 2021/10/18.
//  그리드 제스처텍스트 

import SwiftUI

struct ContentView: View {
    @State private var animationAmount:CGFloat = 1
    var data = Array(1...100).map { "목록 \($0)"}
        //화면을 그리드형식으로 꽉채워줌
    @State var color :Color = Color.green
    let columns = [
        GridItem(.adaptive(minimum: 150))
        //(./flexible()) 을이용하면 아이탬 계수를 추가해주면 화면에 맞게 적용, spacing으로 간격조절
    ]
    
    @State var tag:Int? = nil
    var body: some View {
        NavigationView{
            VStack{
                HStack{
                    Button("애니매이션 스크롤 그리드") {
                    self.tag=1
                    }
                    .padding(20)
                    .background(Color.green)
                    .foregroundColor(.white)
                    .clipShape(Circle())
                    
                    Button("모달 리스트 날짜") {
                    self.tag=2
                    }
                    .padding(20)
                    .background(Color.green)
                    .foregroundColor(.white)
                    .clipShape(Circle())
                    Button("맵 토글 색선택") {
                    self.tag=4
                    }
                    .padding(20)
                    .background(Color.green)
                    .foregroundColor(.white)
                    .clipShape(Circle())
                    Button("지오메트리 폼 액션시트") {
                    self.tag=5
                    }
                    .padding(20)
                    .background(Color.green)
                    .foregroundColor(.white)
                    .clipShape(Circle())
                }
          
               
                
                .onAppear {
                    self.animationAmount = 2
                }
                HStack{
                    Button(action: {
                        let provider = (1...10).publisher
                           provider.sink(receiveCompletion: {_ in
                              print("데이터 전달이 끝났습니다.")
                           }, receiveValue: {data in
                               print(data)
                               if color == Color.green {
                                   color = Color.blue
                               }else if color == Color.blue{
                                   color = Color.red
                               }else if color == Color.red{
                                   color = Color.green
                               }
                           })
                          }
                           , label: {Text("color 버튼")
                            .frame(width: 100, height: 50)
                            .background(color)
                            .foregroundColor(.white)
                            .padding(20)
                           }
                    )
                    Text("제스처 텍스트") .frame(width: 100, height: 50)
                        .background(color)
                        .foregroundColor(.white)
                        .padding(20)
                        .gesture(TapGesture().onEnded({ _ in
                            self.tag=3
                        }))
                }
       
              
            HStack {
                NavigationLink(destination: animation_scroll_grid(), tag: 1, selection: $tag) {
                }
                NavigationLink(destination: ui.gesture(), tag: 3, selection: $tag) {
                }
                NavigationLink(destination: modal_list_date(), tag: 2, selection: $tag) {
                }
                NavigationLink(destination: map_toggle_colorpicker(), tag: 4, selection: $tag) {
                }
                NavigationLink(destination: GeometryView(), tag: 5, selection: $tag) {
                }
            }
        ScrollView {
            NavigationLink(
                destination: MyWebView(urlToLoad: "https://www.naver.com")
                    .edgesIgnoringSafeArea(.all) // 전체화면 모드
            ){
                Text("네이버")
                    .font(.system(size: 25)) // 글자크기
                    .fontWeight(.bold)
                    .padding(15)
                    .frame(width: 250, height: 50, alignment: .center)
                    .foregroundColor(Color.white)
                    .background(Color.green)
                    .cornerRadius(20) // 마지막에 설정해줘야 적용됨
            }
            NavigationLink(
                destination: MyWebView(urlToLoad: "https://www.daum.net")
                    .edgesIgnoringSafeArea(.all) // 전체화면 모드
            ){
                Text("다음")
                    .font(.system(size: 25))
                    .fontWeight(.bold)
                    .padding(15)
                    .frame(width: 250, height: 50, alignment: /*@START_MENU_TOKEN@*/.center/*@END_MENU_TOKEN@*/)
                    .foregroundColor(Color.white)
                    .background(Color.yellow)
                    .cornerRadius(20) // 마지막에 설정해줘야 적용됨정
                
            }
            NavigationLink(
                destination: MyWebView(urlToLoad: "https://www.google.com")
                    .edgesIgnoringSafeArea(.all) // 전체화면 모드
            ){
                Text("구글")
                    .font(.system(size: 25))
                    .fontWeight(.bold)
                    .padding(15)
                    .frame(width: 250, height: 50, alignment: /*@START_MENU_TOKEN@*/.center/*@END_MENU_TOKEN@*/)
                    .foregroundColor(Color.white)
                    .background(Color.blue)
                    .cornerRadius(20)
                    
            }
            let numbers = [1, 2, 4, 5]
            let indexAndNum = numbers.enumerated().map { (index,element) in
            return "\(index)"
            }
            LazyVGrid(columns: columns, spacing: 20) {
                ForEach(data, id: \.self) {i in
                    Text(i)
                        .frame(width: 150, height: 50)
                        .background(color)
                        .cornerRadius(50)
                        
                }
            }
            .padding(.horizontal)
            
            LazyVGrid(columns: columns, spacing: 20) {
                ForEach(indexAndNum, id: \.self) {i in
                    Text(i)
                        .frame(width: 150, height: 50)
                        .background(Color.green)
                        .cornerRadius(50)
                        
                }
            }
            .padding(.horizontal)
        }
            }
    } .edgesIgnoringSafeArea(.all)
            .navigationBarBackButtonHidden(true)
            .navigationBarHidden(true)
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
