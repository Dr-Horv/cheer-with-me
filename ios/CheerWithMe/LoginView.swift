//
//  LoginView.swift
//  CheerWithMe
//
//  Created by Johan Lindskogen on 2019-06-24.
//  Copyright © 2019 Johan Lindskogen. All rights reserved.
//

import SwiftUI
import AuthenticationServices

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
            
            SignInWithAppleButton(.signIn,
                onRequest: { request in
                    request.requestedScopes = [.fullName, .email]
                },
                onCompletion: { result in
                    switch result {
                    case .success (let authResult):
                        print("Authorization successful.")
                        print(authResult)
                        if let appleIDCredential = authResult.credential as? ASAuthorizationAppleIDCredential {
                            let userIdentifier = appleIDCredential.user
                            let fullName = appleIDCredential.fullName
                            let email = appleIDCredential.email
                            
                            let identityToken = appleIDCredential.identityToken.flatMap { String(data: $0, encoding: .utf8) }
                            let authorizationCode = appleIDCredential.authorizationCode.flatMap { String(data: $0, encoding: .utf8) }
                            
                            print(userIdentifier, fullName as Any, email as Any, identityToken as Any, authorizationCode as Any)
                            
                            BackendService.shared.token = identityToken
                            
                            guard let code = authorizationCode else {
                                preconditionFailure("authorizationCode must be defined")
                            }
                            
                            let _ = BackendService.shared.register(payload: .init(code: code)).sink(receiveCompletion: { completion in
                                print(completion)
                            }, receiveValue: { response in
                                print(response)
                            })
                        }
                    case .failure (let error):
                        print("Authorization failed: \(error.localizedDescription)")
                    }
                }
            ).frame(width: 300, height: 50, alignment: .center).signInWithAppleButtonStyle(.white)
            
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
