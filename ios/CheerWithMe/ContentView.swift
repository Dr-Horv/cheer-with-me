//
//  ContentView.swift
//  CheerWithMe
//
//  Created by Johan Lindskogen on 2019-06-20.
//  Copyright © 2019 Johan Lindskogen. All rights reserved.
//

import SwiftUI
import Combine

struct ContentView : View {
    @State var signedIn: Bool = false
    
    func setSignedIn(signedIn: Bool) {
        self.signedIn = signedIn
    }
    
    var body: some View {
        if self.signedIn {
            return AnyView(MainView())
        } else {
            return AnyView(LoginView(setSignedIn: setSignedIn(signedIn:)))
        }
    }
}

#if DEBUG
struct ContentView_Previews : PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
#endif


extension Color {
    static func hex(_ hex: UInt32, opacity:Double = 1.0) -> Color {
        let red = Double((hex & 0xff0000) >> 16) / 255.0
        let green = Double((hex & 0xff00) >> 8) / 255.0
        let blue = Double((hex & 0xff) >> 0) / 255.0
        return self.init(.sRGB, red: red, green: green, blue: blue, opacity: opacity)
    }
}
