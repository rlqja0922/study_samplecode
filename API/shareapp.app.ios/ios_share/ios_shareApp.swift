//
//  ios_shareApp.swift
//  ios_share
//
//  Created by reborn on 2021/10/27.
//

import SwiftUI
import Firebase
@main
struct ios_shareApp: App {
    init() {
        FirebaseApp.configure()
    }
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}


