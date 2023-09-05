//
//  ActionSheetView.swift
//  Study2
//
//  Created by reborn-m1macmini1 on 2021/10/01.
//

import Foundation
import SwiftUI

struct ActionSheetView: View {
    @State var showActionSheet: Bool = false
    @State var actionSheetOption: ActionSheetOption = .isOtherPost
    
    enum ActionSheetOption {
        case isMyPost
        case isOtherPost
    }
    
    var body: some View {
        
        VStack {
            HStack {
                Circle()
                    .frame(width: 30, height: 30)
                Text("@안녕")
                Spacer()
                
                Button(action: {
                    showActionSheet.toggle()
                }) {
                    Image(systemName: "ellipsis")
                }
                .actionSheet(isPresented: $showActionSheet, content: {
                    getActionSheet()
                })
            }
            .padding(.horizontal)
            
            Image("common")
                .aspectRatio(1.0, contentMode: .fit)
        }
    }
    
    func getActionSheet() -> ActionSheet {
        
        let title = Text("옵션")
        let shareButton: ActionSheet.Button = .default(Text("공유"))
        let deleteButton: ActionSheet.Button = .destructive(Text("삭제"))
        let reportButton: ActionSheet.Button = .destructive(Text("신고"))
        let cancelButton: ActionSheet.Button = .cancel()
        
        switch actionSheetOption {
        case .isMyPost:
            return ActionSheet(title: title,
                               message: nil,
                               buttons: [shareButton, deleteButton, cancelButton])
        case.isOtherPost:
            return ActionSheet(title: title,
                               message: nil,
                               buttons: [shareButton, reportButton, cancelButton])
        }
    }
}

struct ActionSheetView_Previews: PreviewProvider {
    static var previews: some View {
        ActionSheetView()
    }
}
