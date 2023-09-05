//
//  profile.swift
//  register_login
//
//  Created by reborn on 2021/11/02.
//

import SwiftUI

struct profile: View {
    var ProfileImgName:String = "img"
    var nic:String = "박효신"
    var lastMsg:String = "프로필화면"
  
    var body: some View {
        VStack{
            UserMainImage
            Userinfo
            BottomMenu
        }
       
        
    }
  
}
private extension profile {
    var UserMainImage:some View{
        CircleImage(image: Image(ProfileImgName))
    }
    var Userinfo : some View{
        VStack(alignment: .leading){
            Text(nic)
                .font(.title)
            HStack(alignment: .top){
                Text(lastMsg)
                    .font(.subheadline)
                Spacer()
            }
        }
        .padding()
    }
    var BottomMenu : some View{
        HStack{
            BottomMenuUI(ImageName: "img", menuNmae: "menu1")
            BottomMenuUI(ImageName: "img", menuNmae: "menu2")
            BottomMenuUI(ImageName: "img1", menuNmae: "menu3")
            BottomMenuUI(ImageName: "img1", menuNmae: "menu4")
        }
    }
}
struct BottomMenuUI : View {
    var ImageName : String
    var menuNmae : String
    var body : some View{
        VStack{
           CircleImageBtn(image: Image(ImageName))
            Text(menuNmae)
        }
    }
}
struct CircleImageBtn:View{
    var image:Image
    var imgHW:CGFloat = 80
    var body: some View{
        image
            .resizable()
            .frame(width: imgHW, height: imgHW) // 사이즈 조정
            .clipShape(Circle())
            .overlay(Circle().stroke(Color.white, lineWidth: 3)) // 원그리
            .shadow(radius: 5)
        //.scaledToFit() // 줌인 효과
    }
}
struct CircleImage : View {
    var image : Image
    var imgHw:CGFloat = 300
    var body: some View{
        image
            .resizable()
            .frame(width: imgHw, height: imgHw) // 사이즈 조정
            .clipShape(Circle())
            .overlay(Circle().stroke(Color.red, lineWidth: 3).padding(0)) // 원그리
            .shadow(radius: 5)
        //.scaledToFit() // 줌인 효과
    }
}

struct profile_Previews: PreviewProvider {
    static var previews: some View {
        profile()
    }
}
