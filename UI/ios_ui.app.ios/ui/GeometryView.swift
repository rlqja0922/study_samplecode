//
//  GeometryView.swift
//  CombineTest
//
//  Created by reborn-m1macmini2 on 2021/10/07.
//

import SwiftUI

struct GeometryView: View {
    @State var username: String = ""
    @State var isPrivate: Bool = true
    @State var notificationsEnabled: Bool = false
    @State private var previewIndex = 0
    
    @State private var showingSheet = false
    @State var showActionSheet: Bool = false
    
    var previewOptions = ["3, 2, 1 ,10, 20"]
    var body: some View {
        GeometryReader { geometry in
            VStack{
                HStack{
                    Text("Left")
                        .frame(width: geometry.size.width * 0.33, height: 50)
                        .background(Color.yellow)
                    Text("Right")
                        .frame(width: geometry.size.width * 0.67, height: 50)
                        .background(Color.green)
   
                }
                HStack{
                    Button("액션시트1") {
                        self.showingSheet.toggle()
                    }  .actionSheet(isPresented: $showingSheet) {
                        ActionSheet(title: Text("타이틀"),
                                    message: Text("메시지"),
                                    buttons: [.default(Text("버튼")),
                                                .cancel(Text("취소"))
                                    ])
                    }
                    Button("액션시트2") {
                        showActionSheet.toggle()
                    }.actionSheet(isPresented: $showActionSheet, content: getActionSheet)
                }
                Form {
                    Section(header: Text("프로필")) {
                        TextField("이름", text: $username)
                        Toggle(isOn: $isPrivate) {
                            Text("비공개 계정")
                        }
                    }
                    Section(header: Text("밝기")) {
                        Toggle(isOn: $notificationsEnabled) {
                            Text("깨우기")
                        }
                        Picker(selection: $previewIndex, label: Text("잠금")) {
                            ForEach(0..<previewOptions.count) {
                                Text(self.previewOptions[$0])
                            }
                        }
                    }
                    Section(header: Text("소프트웨어")) {
                        HStack {
                            Text("Version")
                            Spacer()
                            Text("2.2.1")
                        }
                    }
                    Section {
                        Button(action: {
                            print("here")
                        }) {
                            Text("재설정")
                        }
                    }
                }
                .navigationBarTitle("설정")
            }
            
        }
          
    }
    
    func getActionSheet() -> ActionSheet {
        
        let button1: ActionSheet.Button = .default(Text("허용".uppercased()))
        let button2: ActionSheet.Button = .destructive(Text("거부".uppercased()))
        let button3: ActionSheet.Button = .cancel()
        
        
        let message = Text("Message")
        
        let title = Text("ActionSheet Title")
        
        return ActionSheet(title: title,
                           message: message,
                           buttons: [button1, button2, button3])
    }
}

struct GeometryView_Previews: PreviewProvider {
    static var previews: some View {
        GeometryView()
    }
}

