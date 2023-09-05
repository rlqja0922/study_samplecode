//
//  list.swift
//  register_login
//
//  Created by reborn on 2021/11/02.
//
import Foundation
import SwiftUI

struct list: View {
    var MenuLit = [
    menulist(UserName: "박효신", ImgNamge: "img", meesage: "1번"),
    menulist(UserName: "아이유", ImgNamge: "img1", meesage: "2번"),
    menulist(UserName: "박효신", ImgNamge: "img", meesage: "3번"),
    menulist(UserName: "아이유", ImgNamge: "img1", meesage: "4번"),
    menulist(UserName: "박효신", ImgNamge: "img", meesage: "5번"),
    menulist(UserName: "아이유", ImgNamge: "img1", meesage: "6번"),
    menulist(UserName: "박효신", ImgNamge: "img", meesage: "7번")
    ]
    var body: some View {
        List{
            ForEach(MenuLit){
                menuview($0.UserName,$0.ImgNamge,$0.meesage)
            }
        }
    }
}
struct menuview : View{
    let widthText:CGFloat = 300
    let ImageWH :CGFloat = 40
    var UserNmae:String
    var ImgNamge:String
    var meesage:String
    init(_ UserName1:String = "NoName", _ Image1 : String, _ Message1:String = "NoMsg")
    {
        self.UserNmae = UserName1
        self.ImgNamge = Image1
        self.meesage = Message1
    }
    var body: some View{
        HStack{
            SwiftUI.Image(ImgNamge)
                .resizable()
                .frame(width:ImageWH, height : ImageWH)
                .clipShape(Circle())
            VStack{
                Text(UserNmae)
                    .font(.headline)
                    .frame(width: widthText, height: 20, alignment: .leading)
                Text(meesage)
                    .font(.footnote)
                    .frame(width: widthText, height: 20, alignment: .leading)
            }
        }
    }
}
struct menulist {
    let id : UUID = UUID()
    let UserName : String
    let ImgNamge : String
    var meesage:String
}
extension menulist : Codable{}
extension menulist : Identifiable{}
extension menulist : Equatable{}
struct list_Previews: PreviewProvider {
    static var previews: some View {
        list()
    }
}
