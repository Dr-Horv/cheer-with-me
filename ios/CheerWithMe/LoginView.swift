//
//  LoginView.swift
//  CheerWithMe
//
//  Created by Johan Lindskogen on 2019-06-24.
//  Copyright © 2019 Johan Lindskogen. All rights reserved.
//

import SwiftUI

struct LoginView : View {
    @State private var username: String = ""
    
    var onPressSignIn: (String) -> Void
    
    var body: some View {
        VStack {
            Spacer()
            
            VStack {
                Text("🎉").font(.largeTitle)
                Text("CheerWithMe").font(.largeTitle)
            }
            
            HStack {
                TextField("Username", text: $username).textFieldStyle(RoundedBorderTextFieldStyle())
                    .padding()
            }
            .padding()
            
            AuthenticationButton()
                .frame(width: 300, height: 50)
            
            Button(action: {
                self.onPressSignIn(self.username)
            }) {
                Text("Sign in")
            }
            
            Spacer()
        }
    }
}

#if DEBUG
struct LoginView_Previews : PreviewProvider {
    static var previews: some View {
        LoginView(onPressSignIn: { username in print(username) })
    }
}
#endif
