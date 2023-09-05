//
//  ContentView.swift
//  gesture
//
//  Created by reborn on 2021/11/16.
//

import SwiftUI
import UIKit
struct ContentView: View {
    @State var viewchange = true
    @State var dragOffset = CGSize.zero
    @State var dragOffset1 = CGFloat.zero
    @State var dragOffset2 = CGFloat.zero
    @State var currentAmount: CGFloat = 0
    @State var scale: CGFloat = 1.0
    @State var col = Color.blue
    @State private var lastDragPosition: DragGesture.Value?
    @State var isCompleted : Bool = false
    @State var isSuccess : Bool = false
    var body: some View {
        if viewchange == false{
            VStack{
                Text("longpress")
                    .frame(width: 100, height: 100)
                    .background(col)
                    .gesture(LongPressGesture(minimumDuration: 1)
                                .onEnded{_ in
                        col = Color.black
                    })
                Rectangle()
                    .fill(isSuccess ? Color.green : Color.blue)
                    .frame(height : 50)
                    .frame(width : isCompleted ? . infinity : 0)
                    .frame(maxWidth : .infinity, alignment: .leading)
                    .background(Color.gray)
                HStack(spacing : 20 ){
                    Text("2초간 유지")
                        .foregroundColor(.white)
                        .padding()
                        .background(Color.black.cornerRadius(10))
                        .onLongPressGesture(minimumDuration: 1.5, maximumDistance: 50){ (isPressed) in
                            if isPressed {
                                withAnimation(.easeIn(duration:1.5)){
                                    isCompleted = true
                                }
                            }else {
                                DispatchQueue.main.asyncAfter(deadline: .now() + 0.1){
                                    if !isSuccess{
                                        withAnimation(.spring()){
                                            isCompleted = false
                                        }
                                    }
                                }
                            }
                        } perform: {
                            isSuccess = true
                        }
                    Text("리셋")
                        .foregroundColor(.white)
                        .padding()
                        .background(Color.black.cornerRadius(10))
                        .onTapGesture {
                            withAnimation(.spring()){
                                isCompleted = false
                                isSuccess = false
                            }
                        }
                }
            }.frame(width: 500, height: 500)
                .background(Color.red)
                .gesture(DragGesture()
                            .onChanged({ ( value ) in //움직일때
                              lastDragPosition = value
                            })
                            .onEnded{//손땠을때
                    value in
                    if value.startLocation.x > value.location.x {
                        dragOffset1 = value.startLocation.x - value.location.x
                        if dragOffset1  > 100 {
                            viewchange = true
                        }
                    }else {
                        dragOffset1 = value.location.x - value.startLocation.x
                        if dragOffset1  > 100 {
                            viewchange = true
                        }
                    }
                    if value.startLocation.y > value.location.y {
                        dragOffset2 = value.startLocation.y - value.location.y
                        if dragOffset2  > 100 {
                            viewchange = true
                        }
                    }else {
                        dragOffset2 = value.location.y - value.startLocation.y
                        if dragOffset2  > 100 {
                            viewchange = true
                        }
                    }
                }
            )
        }else {
            VStack{
                Image(systemName: "swift")
                    .resizable()
                    .frame(width: 100, height: 100)
                    .scaledToFit()
                    .scaleEffect(1 + currentAmount)
                    .gesture(MagnificationGesture()
                                .onChanged{value in
                                           currentAmount = value - 1}
                                .onEnded{value in
                        withAnimation(.spring()){
                            currentAmount = 0
                        }
                    })
                    .zIndex(1.0)
                HStack{
                    Text("gesture")
                }.frame(width: 100, height: 50)
                    .offset(dragOffset)
                    .gesture(DragGesture()
                                .onChanged{
                        gesture in
                        dragOffset = gesture.translation
                        }
                                .onEnded{
                        gesture in
                        dragOffset = .zero
                    }
                    )
                HStack{
                    Text("가로 : ")
                    Text(String(format: "%.0f", dragOffset1 as CVarArg))
                }
                HStack{
                    Text("세로 : ")
                    Text(String(format: "%.0f", dragOffset2 as CVarArg))
                }
            }.frame(width: 500, height: 500)
                .background(Color.blue)
                .gesture(DragGesture()
                            .onChanged({ ( value ) in
                              lastDragPosition = value
                            })
                            .onEnded{
                    value in
                    if value.startLocation.x > value.location.x {
                        dragOffset1 = value.startLocation.x - value.location.x
                        if dragOffset1  > 100 {
                            viewchange = false
                        }
                    }else {
                        dragOffset1 = value.location.x - value.startLocation.x
                        if dragOffset1  > 100 {
                            viewchange = false
                        }
                    }
                    if value.startLocation.y > value.location.y {
                        dragOffset2 = value.startLocation.y - value.location.y
                        if dragOffset2  > 100 {
                            viewchange = false
                        }
                    }else {
                        dragOffset2 = value.location.y - value.startLocation.y
                        if dragOffset2  > 100 {
                            viewchange = false
                        }
                    }
                }
            )
        }
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
