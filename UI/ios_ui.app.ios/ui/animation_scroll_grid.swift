//
//  animation_scroll_grid.swift
//  ui
//
//  Created by reborn on 2021/10/18.
//

import SwiftUI
import Combine

struct animation_scroll_grid: View {
    @State private var animationAmount: CGFloat = 1
    @State private var publ = ["A","B","C","D","E","F","G","1","2","3","4","5"]
   
    private var colors: [Color] = [.blue, .yellow, .green]
    private var gridItems = [GridItem(.flexible()),
                             GridItem(.flexible()),
                             GridItem(.flexible())]
    @State var co : String = "combine"
    @State var len : Int = 8
      var body: some View {
          print(animationAmount)
          return VStack {
              Stepper("Scale Amount", value: $animationAmount.animation(
                          Animation.easeInOut(duration: 1).repeatCount(3, autoreverses: true)),
                      in: 1...10)
              
              Button(action: {
                  let provider = (1...10).publisher
                     provider.sink(receiveCompletion: {_ in
                        print("데이터 전달이 끝났습니다.")
                     }, receiveValue: {data in
                         print(data)
                         co = String(data)
                     })
                  sub()
                    }
                     , label: {Text(co)  .padding(10)
                      .background(Color.green)
                      .foregroundColor(.white)
                     })
              Spacer()
              HStack{
                  Button("애니매이션") {
                  
                      if animationAmount < 5{
                          self.animationAmount += 1
                      }else {
                          self.animationAmount = 1
                      }
                  }
                  .padding(40)
                  .background(Color.green)
                  .foregroundColor(.white)
                  .clipShape(Circle())
                  .scaleEffect(animationAmount)
              }
              ScrollView{
                  LazyVGrid(columns: gridItems, spacing: 5) {
                          ForEach((0...len), id: \.self) { index in
                              CellContent(index: index,
                                          color: colors[index % colors.count], text: publ[index % publ.count])
                          
                          }
                      }
                      .padding(5)
              }
          }
       
      }
    struct CellContent: View {
        var index: Int
        var color: Color
        var text : String
        
        var body: some View {
            Text("\(index) pub1 \(text)")
                .frame(minWidth: 50, maxWidth: .infinity, minHeight: 100)
                .background(color)
                .cornerRadius(8)
                .font(.system(.largeTitle))
            }
        }
    
    class CustomSubscrbier: Subscriber{
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
        
        typealias Input = String //성공타입
        typealias Failure = Never //실패타입
        
    }
    func sub() {
        
        let subscriber = CustomSubscrbier()
        let publisher = publ.publisher
        len = publ.count
        publisher.subscribe(subscriber)
    }
}


//struct animation_scroll_grid_Previews: PreviewProvider {
//    static var previews: some View {
//        animation_scroll_grid(index: Int, color: Color)
//    }
//}
