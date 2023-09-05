//
//  ContentView.swift
//  combine_kb
//
//  Created by reborn on 2021/10/13.
//

import SwiftUI
import Combine

struct map_toggle_colorpicker: View {
    @State private var bgColor = Color.white
    @State private var bgColor1 = Color.black
    @State private var publ = ["A","B","C","D","E","F","G","1","2","3","4","5"]
    @State var len : Int = 8
    @State private var someToggle = true
    var body: some View {
        ZStack{
            VStack{
                Text("Hello, world!")
                    .foregroundColor(bgColor)
                    .padding()
                VStack {
                      ColorPicker("배경화면 색상 선택", selection: $bgColor)
                        .foregroundColor(bgColor1)
                    Toggle("뷰 변경",isOn: $someToggle)
                        .toggleStyle(SwitchToggleStyle(tint: bgColor))
                    someToggle ? Text("맵뷰입니다") : Text("combine")
                  }
                 
                
                if someToggle{
                    mapview()
                        .frame(width: UIScreen.main.bounds.width, height: UIScreen.main.bounds.height * (0.5))
                }else{
                    ui.gesture()
                        .frame(width: UIScreen.main.bounds.width, height: UIScreen.main.bounds.height * (0.5))
                }
                
                
            }.onChange(of: bgColor) { _ in
                if bgColor == bgColor1{
                    if bgColor1 == Color.black {
                        bgColor1 = Color.white
                    }else {
                        bgColor1 = Color.black
                    }
                }
            }
        }
    }
    func sub() {
        
        let subscriber = CustomSubscrbier()
        let publisher = publ.publisher
        len = publ.count
        publisher.subscribe(subscriber)
    }
}

import SwiftUI
import MapKit

struct mapview: View {
    @State private var region = MKCoordinateRegion(center: CLLocationCoordinate2D(latitude: 37.5666791, longitude: 126.9782914), span: MKCoordinateSpan(latitudeDelta: 0.5, longitudeDelta: 0.5))
    var body: some View {
        Map(coordinateRegion: $region)
        //Map(coordinateRegion: $region, interactionModes: []) interactionModes 를이용하여 지도 제어 정도를 제한
        //Map(coordinateRegion: $region, interactionModes: [.zoom]) 줌가능
        //Map(coordinateRegion: $region, showsUserLocation: true, userTrackingMode: .constant(.follow)) 줌 이동다됨
    }
}

class CustomSubscrbier: Subscriber{
    
    typealias Input = String //성공타입
    typealias Failure = Never //실패타입
//3
    func receive(completion: Subscribers.Completion<Never>) {
        print("모든 데이터의 발행이 완료되었습니다.")
    }
//1
    func receive(subscription: Subscription) {
        print("데이터의 구독을 시작합니다.")
        //구독할 데이터의 개수를 제한하지않습니다.
        subscription.request(.unlimited)
    }
//2
    func receive(_ input: String) -> Subscribers.Demand {
        print("데이터를 받았습니다.", input)
        return .none
    }
    
}
struct map_toggle_colorpicker_Previews: PreviewProvider {
    static var previews: some View {
        map_toggle_colorpicker()
    }
}
