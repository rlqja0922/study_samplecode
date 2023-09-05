//
//  ContentView.swift
//  kb
//
//  Created by reborn on 2021/09/07.
//

import SwiftUI
import FSCalendar
struct ContentView: View {
    @State var tag:Int? = nil
    @State var modal : Bool = false
    @State var multiSelection = Set<UUID>()
    var body: some View {
        NavigationView {
            VStack(alignment: .center) {
                ZStack {
                    Image(systemName: "swift")
                        .resizable()
                        .frame(width: UIScreen.main.bounds.width ,  height: UIScreen.main.bounds.height )
                        .offset(y: -UIScreen.main.bounds.height*(1/10))
                    VStack(alignment: .center) {
                        HStack{
                            Button(action: {
                                modal = true
                            }, label: {
                                Text("modal버튼")
                                    .frame(width:  UIScreen.main.bounds.width * (2/10), height: 100)
                                    .background(Color.green)
                                    .cornerRadius(100)
                                    .foregroundColor(.black)
                            })
                            Button(action: {
                                self.tag=1
                            }, label: {
                                Text("스크린샷,센서")
                                    .frame(width:  UIScreen.main.bounds.width * (2/10), height: /*@START_MENU_TOKEN@*/100/*@END_MENU_TOKEN@*/)
                                    .background(Color.blue)
                                    .cornerRadius(100)
                                    .foregroundColor(.white)
                            })
                            Button(action: {
                                self.tag=2
                            }, label: {
                                Text("동적리스트")
                                    .frame(width:  UIScreen.main.bounds.width * (2/10), height: /*@START_MENU_TOKEN@*/100/*@END_MENU_TOKEN@*/)
                                    .background(Color.red)
                                    .cornerRadius(100)
                                    .foregroundColor(.white)
                            })
                            Button(action: {
                                self.tag=3
                            }, label: {
                                Text("qr코드")
                                    .frame(width:  UIScreen.main.bounds.width * (2/10), height: /*@START_MENU_TOKEN@*/100/*@END_MENU_TOKEN@*/)
                                    .background(Color.yellow)
                                    .cornerRadius(100)
                                    .foregroundColor(.white)
                            })
                        }
                        Spacer()
                        List(listitme, selection : $multiSelection){
                            Text($0.name)
                        }
                        //.navigationBarTitle("리스트")
                        .toolbar(content: {
                            EditButton()
                        })
                    }
                    HStack {
                        NavigationLink(destination: view2(motion: MotionManager()), tag: 1, selection: $tag) {
                        }
                    }
                    HStack {
                        NavigationLink(destination: ContentView2(), tag: 2, selection: $tag) {
                        }
                    }
                    HStack {
                        NavigationLink(destination: qr(), tag: 3, selection: $tag) {
                        }
                    }
                }
            }
        }
        .sheet(isPresented: $modal) {
            modalview()
                .onDisappear(){
                    
                }
        }
        .onAppear(){
            UserDefaults.standard.set("d", forKey: "cal")
        }
        .edgesIgnoringSafeArea(.all)
        .navigationBarBackButtonHidden(true)
        .navigationBarHidden(true)
 
    }
}
struct calender : UIViewRepresentable {
 
    func makeCoordinator() -> Coordinator {
        return Coordinator()
    }
    
    typealias UIViewType = FSCalendar
        func makeUIView(context: Context) -> FSCalendar {
          let calendar = FSCalendar()
          calendar.delegate = context.coordinator
          calendar.dataSource = context.coordinator
            calendar.locale = Locale(identifier: "ko_KR")
            
          return calendar
        }
    
     //selected day
 

    func updateUIView(_ uiView: FSCalendar, context: Context) {}
    class Coordinator: NSObject, FSCalendarDelegate, FSCalendarDataSource {
        var formatter = DateFormatter()
          var SelectedDate = ""
        @IBOutlet weak var calendar: FSCalendar!
        @IBOutlet weak var SegmentedControl: UISegmentedControl!
        
        fileprivate lazy var dateFormatter: DateFormatter = {
                let formatter = DateFormatter()
                formatter.dateFormat = "yyyy/MM/dd"
                return formatter
            }()
        func calendar(_ calendar: FSCalendar, didSelect date: Date, at monthPosition: FSCalendarMonthPosition){
            // Do the same inside this function and you should be fine
             formatter.dateFormat = "yyyy-MM-dd"
             SelectedDate = formatter.string(from: date)
             print("calendar did select date \(self.formatter.string(from: date))")
            UserDefaults.standard.set(self.formatter.string(from:date), forKey: "cal")
           
         }
              
          
    }
    //날짜가 선택되어있을때
    
    

}
struct modalview : View {
    @State private var wakeUp = Date()
    @State var formatter = DateFormatter()
    @State var date1 = UserDefaults.standard.string(forKey: "cal")
    var body : some View {
        VStack(alignment : .center){
           
            calender()
            Text("캘린더 날짜" + UserDefaults.standard.string(forKey: "cal")! )
            HStack{
                DatePicker("날짜를 선택하세요",selection : $wakeUp)
                    .datePickerStyle(WheelDatePickerStyle())
            }
            VStack{
                Text("피커 날짜" +  formatter.string(from: self.wakeUp))
            }
        }.onAppear(){
            UserDefaults.standard.set("오늘", forKey: "cal")
            formatter.dateFormat = "MMMM dd, yyyy"
        }
    }
}
struct list : Identifiable {
    let name : String
    let id = UUID()
}
private var listitme = [
    list(name: "정적리스트"),
    list(name: "test"),
    list(name: "리스트선택?")
]
struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
struct ContentView2: View {
    
    let drink = ["스프라이트", "콜라", "환타", "오렌지주스"]
    let snack = ["프링글스", "엄마손파이", "포카칩"]
    
    var body: some View {
        
        let titles = ["음료수", "과자"] // 분류 제목
        let data = [drink, snack] //위에 정의한 목록 데이터
        
        return List {
            ForEach(data.indices) { index in // data에 포함된 횟수만큼 섹션 생성
                Section(header: Text(titles[index]), footer: HStack { Spacer(); Text("\(data[index].count)건")}
                ) {
                    ForEach(data[index], id: \.self) {
                    
                        Label($0, systemImage: "leaf.fill")
//                        Text($0)
                    }
                }
            }
        }
//         .listStyle(GroupedListStyle())
    }
}
