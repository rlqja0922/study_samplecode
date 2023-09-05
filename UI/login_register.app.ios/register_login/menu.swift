//
//  menu.swift
//  register_login
//
//  Created by reborn on 2021/11/02.
//

import SwiftUI

struct menu: View {
    var body: some View {
        
        Menu {
            Button("Cancel", action: {})
            //버튼 아래에 메뉴를 한번 더 추가해준다.
            Menu("More") {
                Button("Rename", action: {})
                //메뉴안에 아이콘을 추가해준다.
                Button(action: {
                }) {
                    HStack {
                        Text("Developer mode")
                        Image(systemName: "person.crop.circle.fill")
                    }
                }
            }
            Button(action: {
            }) {
                HStack {
                    Text("Serch")
                    Image(systemName: "magnifyingglass.circle")
                }
            }
            Button("Add", action: {})
            //최상단 메뉴 버튼
        } label: {
            Label("메뉴열기", systemImage: "plus")
        }
    }
}

struct menu_Previews: PreviewProvider {
    static var previews: some View {
        menu()
    }
}
