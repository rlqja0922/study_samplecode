//
//  DrawingControls.swift
//  DrawingPadSwiftUI
//
//  Created by Martin Mitrevski on 19.07.19.
//  Copyright © 2019 Mitrevski. All rights reserved.
//

import SwiftUI

struct DrawingControls: View {
    @Binding var color: Color
    @Binding var drawings: [Drawing]
    @Binding var lineWidth: CGFloat
    
    @State private var colorPickerShown = false

    private let spacing: CGFloat = 40
    
    var body: some View {
        NavigationView {
            VStack {
                HStack(spacing: spacing) {
                    Button("컬러 선택") {
                        self.colorPickerShown = true
                    }
                    Button("이전") {
                        if self.drawings.count > 0 {
                            self.drawings.removeLast()
                        }
                    }
                    Button("초기화") {
                        self.drawings = [Drawing]()
                    }
                }
                HStack {
                    Text("선굵기조절")
                        .padding()
                    Slider(value: $lineWidth, in: 1.0...15.0, step: 1.0)
                        .padding()
                }
            }

        }
        .frame(height: 200)
        .sheet(isPresented: $colorPickerShown, onDismiss: {
            self.colorPickerShown = false
        }, content: { () -> ColorPicker in
            ColorPicker(color: self.$color, colorPickerShown: self.$colorPickerShown)
        })
    }
}
