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
    
    @State var userManager: UserManager = .shared
    
    func onSubmit(with username: String) {
        
        
    }
    
    var body: some View {
        if self.signedIn {
            return AnyView(MainView())
        } else {
            return AnyView(LoginView(onPressSignIn: onSubmit(with:)))
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
