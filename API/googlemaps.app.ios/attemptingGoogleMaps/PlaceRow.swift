//
//  PlaceRow.swift
//  attemptingGoogleMaps
//
//  Created by reborn-m1macmini1 on 2021/11/03.
//

import SwiftUI
import GooglePlaces

struct PlaceRow: View {
    // 1
    var place: GMSPlace
    
    var body: some View {
        HStack {
            // 2
            Text(place.name ?? "")
                .foregroundColor(.white)
            Spacer()
        }
    }
}
