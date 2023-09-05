//
//  ContentView.swift
//  attemptingGoogleMaps
//
//  Created by reborn-m1macmini1 on 2021/11/02.
//

import SwiftUI


struct ContentView: View {
    var body: some View {
        VStack {
        GoogleMapView()
            .edgesIgnoringSafeArea(.top)
            //.frame(height: 300)
            //PlacesList()
        }
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
        
    }
}
