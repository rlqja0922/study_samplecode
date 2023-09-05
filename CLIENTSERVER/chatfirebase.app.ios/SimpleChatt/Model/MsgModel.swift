//
//  MsgModel.swift
//  SimpleChatt
//
//  Created by reborn-m1macmini1 on 2021/11/08.
//

import SwiftUI
import FirebaseFirestoreSwift

// For Onchange

struct MsgModel: Codable,Identifiable,Hashable {
    
    @DocumentID var id : String?
    var msg : String
    var user : String
    var timeStamp: Date
    
    enum CodingKeys: String,CodingKey{
        case id
        case msg
        case user
        case timeStamp
        
    }
}
