//
//  SimpleChattApp.swift
//  SimpleChatt
//
//  Created by reborn-m1macmini1 on 2021/11/08.
//

import SwiftUI
import Firebase

@main
struct SimpleChattApp: App {
    
    @UIApplicationDelegateAdaptor(AppDelegate.self) var delegate
    
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}

class AppDelegate : NSObject,UIApplicationDelegate{
    
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil) -> Bool {
        
        
        FirebaseApp.configure()
        return true
    }
}
