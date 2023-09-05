//
//  HomeModel.swift
//  SimpleChatt
//
//  Created by reborn-m1macmini1 on 2021/11/08.
//

import SwiftUI
import Firebase
import grpc

class HomeModel: ObservableObject{
    
    @Published var txt = ""
    @Published var msgs : [MsgModel] = []
    @AppStorage("current_user") var user = ""
    let ref = Firestore.firestore()
    
    init() {
        readAllMsgs()
    }
    
    func onAppear(){
        
        // Checking whether user is joined already....
        
        if user == ""{
            // Join Alert...
            
            UIApplication.shared.windows.first?.rootViewController?.present(alertView(), animated: true)
        }
    }
    
    func alertView() -> UIAlertController {
        
        let alert = UIAlertController(title: "채팅에 접속하세요!", message: "닉네임을 적으세요", preferredStyle: .alert)
        
        alert.addTextField { (txt) in
            txt.placeholder = "Reborn Soft"
        }
        
        let join = UIAlertAction(title: "join", style: .default) { (_) in
            
            // checking for empty click...
            
            let user = alert.textFields![0].text ?? ""
            
            if user != ""{
                
                self.user = user
                return
            }
            
            //repromiting alert view...
            
            UIApplication.shared.windows.first?.rootViewController?.present(alert, animated: true)

        }
        alert.addAction(join)
        
        return alert
    }
    
    func readAllMsgs(){
        
        ref.collection("Msgs").order(by: "timeStamp", descending: false).addSnapshotListener { (snap, err)  in
            
            if err != nil{
                print(err!.localizedDescription)
                return
            }
            
            guard let data = snap else{return}
            
            data.documentChanges.forEach { (doc) in
                // adding when data is added...
                    
                
                
                if doc.type == .added{

                    let msg = try! doc.document.data(as: MsgModel.self)!

                    DispatchQueue.main.async {
                        self.msgs.append(msg)
                    }
                }

                
//                if doc.type == .added{
//                    do{
//                             let msg = try doc.document.data(as: MsgModel.self)!
//
//                             DispatchQueue.main.async {
//                                 self.msgs.append(msg)
//                             }
//                    }catch{
//                            print(error.localizedDescription)
//                        }
//                }
                
         
            }
        }
    }
    
    func writeMsg() {
        let msg = MsgModel(msg: txt, user: user, timeStamp: Date())
        
        let _ = try! ref.collection("Msgs").addDocument(from: msg) { (err) in
            
            if err != nil{
                print(err!.localizedDescription)
                return
            }
            
            self.txt = ""
        }
        
    } 
}

