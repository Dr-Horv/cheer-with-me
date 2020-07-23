//
//  AuthenticationController.swift
//  CheerWithMe
//
//  Created by Johan Lindskogen on 2019-07-02.
//  Copyright © 2019 Johan Lindskogen. All rights reserved.
//

import SwiftUI
import AuthenticationServices

struct AuthenticationButton : UIViewRepresentable {
    func makeUIView(context: Context) -> ASAuthorizationAppleIDButton {
        let requests = [ASAuthorizationAppleIDProvider().createRequest(),
                        ASAuthorizationPasswordProvider().createRequest()]
        
        let buttonColor: ASAuthorizationAppleIDButton.Style = context.environment.colorScheme == .dark ? .white : .black
        
        // Create an authorization controller with the given requests.
        let authorizationController = ASAuthorizationController(authorizationRequests: requests)
        authorizationController.delegate = context.coordinator
        authorizationController.presentationContextProvider = context.coordinator
        authorizationController.performRequests()
        
        
        let button = ASAuthorizationAppleIDButton(type: .default, style: buttonColor)
        button.addTarget(
            context.coordinator,
            action: #selector(Coordinator.handleButtonPressed(sender:)),
            for: .touchUpInside)
        
        return button
    }
    
    func updateUIView(_ uiView: ASAuthorizationAppleIDButton, context: Context) { }
    
    func makeCoordinator() -> Coordinator {
        Coordinator(self)
    }
    
    class Coordinator: NSObject, ASAuthorizationControllerDelegate, ASAuthorizationControllerPresentationContextProviding {
        let button: AuthenticationButton
        let provider: ASAuthorizationAppleIDProvider
        var user: String?
        
        init(_ button: AuthenticationButton) {
            self.button = button
            self.provider = ASAuthorizationAppleIDProvider()
        }
        
        @objc func handleButtonPressed(sender: ASAuthorizationAppleIDButton) {
            let request = self.provider.createRequest()
            if let previousUser = self.user {
                request.user = previousUser
                request.requestedOperation = .operationRefresh
                print("Previous user is set, performing refresh instead!")
            }
            
            
            request.requestedScopes = [.fullName, .email]
            
            let authorizationController = ASAuthorizationController(authorizationRequests: [request])
            authorizationController.delegate = self
            authorizationController.presentationContextProvider = self
            authorizationController.performRequests()
        }
        
        func authorizationController(controller: ASAuthorizationController, didCompleteWithAuthorization authorization: ASAuthorization) {
            print("didCompleteWithAuthorization", authorization)
            
            if let appleIDCredential = authorization.credential as? ASAuthorizationAppleIDCredential {
                let userIdentifier = appleIDCredential.user
                let fullName = appleIDCredential.fullName
                let email = appleIDCredential.email
                
                self.user = userIdentifier
                
                let identityToken = appleIDCredential.identityToken.flatMap { String(data: $0, encoding: .utf8) }
                let authorizationCode = appleIDCredential.authorizationCode.flatMap { String(data: $0, encoding: .utf8) }
                
                print(userIdentifier, fullName as Any, email as Any, identityToken as Any, authorizationCode as Any)
                
                BackendService.shared.token = identityToken
                
                
                self.provider.getCredentialState(forUserID: userIdentifier) { state, error in
                    if let error = error {
                        print("error \(error)")
                    } else {
                        print("state \(state)")
                    }
                }
            }
            
        }
        
        func authorizationController(controller: ASAuthorizationController, didCompleteWithError error: Error) {
            print("didCompleteWithError error", error)
        }
        
        func presentationAnchor(for controller: ASAuthorizationController) -> ASPresentationAnchor {
            UIApplication.shared.windows[0]
        }
    }
    
}

#if DEBUG
struct AuthenticationButton_Previews : PreviewProvider {
    static var previews: some View {
        AuthenticationButton()
    }
}
#endif
