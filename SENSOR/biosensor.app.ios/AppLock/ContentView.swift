//
//  ContentView.swift
//  AppLock
//
//  Created by Azhagusundaram Tamil on 20/01/21.
//

import SwiftUI

struct ContentView: View {
    @EnvironmentObject var appLockVM: AppLockViewModel
    
    var body: some View {
        ZStack {
            // Show HomeView app lock is not enabled or app is in unlocked state
            if !appLockVM.isAppLockEnabled || appLockVM.isAppUnLocked {
                AppHomeView()
            } else {
                AppLockView()
            }
        }
        .onAppear {
            // if 'isAppLockEnabled' value true, then immediately do the app lock validation
            //토글버튼 값에 따라서 생체인식 시작
            if appLockVM.isAppLockEnabled {
                appLockVM.appLockValidation()
            }
        }
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
