//
//  Home.swift
//  SimpleChatt
//
//  Created by reborn-m1macmini1 on 2021/11/08.
//

import SwiftUI


struct Home: View {
    @StateObject var homeData = HomeModel()
    @AppStorage("current_user") var user = ""
    @State var scrolled = false
    var body: some View {
      
        VStack(spacing: 0){
            
            HStack{
                
                Text("Rebornsoft")
                    .font(.title)
                    .fontWeight(.heavy)
                    .foregroundColor(.white)



                Spacer(minLength: 0)
            }
            .padding()
            .padding(.top,UIApplication.shared.windows.first?.safeAreaInsets.top)
            .background(Color("Color"))
            
            ScrollViewReader{reader in
                
                ScrollView{
                    
                    VStack(spacing: 15) {
                        
                    ForEach(homeData.msgs){msg in
                        
                        ChatRow(chatData: msg)
                            .onAppear{
                                // First Time Scroll
                                if msg.id == self.homeData.msgs.last!.id && scrolled{
                                    
                                    reader.scrollTo(homeData.msgs.last!.id, anchor: .bottom)
                                    scrolled = true
                                }
                                
                            }
                        }
                    .onChange(of: homeData.msgs, perform: { Value in
                        
                        // You can restrict only for current user scroll...
                        reader.scrollTo(homeData.msgs.last!.id, anchor: .bottom)
                        })
                    }
                    
                    .padding(.vertical)
                
                }
            }
            
            HStack(spacing: 15){
                
                TextField("메세지를 입력하세요", text: $homeData.txt)
                    .padding(.horizontal)
                // Fixed Height For Animaition...
                    .frame(height: 45)
                    .background(Color.primary.opacity(0.06))
                    .clipShape(Capsule())
                
                if homeData.txt != ""{
                    
                    Button(action: homeData.writeMsg, label: {
                        
                        Image(systemName: "paperplane.fill")
                            .font(.system(size: 22))
                            .foregroundColor(.white)
                            .frame(width: 45, height: 45)
                            .background(Color("Color"))
                            .clipShape(Circle())
                    })
                }
            }
            .animation(.default)
            .padding()
            
        }
            
    
        .onAppear(perform: {
            
            homeData.onAppear()
            
        })
        .ignoresSafeArea(.all, edges: .top)
    }
}

