import SwiftUI
import AuthenticationServices
import GoogleSignIn
import UIKit
import Alamofire


struct AccessTokenResponse: Codable {
    let accessToken: String
}

struct LoginView : View {
    @ObservedObject var viewModel: MainViewModel
    
    var body: some View {
        VStack {
            Spacer()
            
            VStack {
                Text("🎉").font(.largeTitle)
                Text("CheerWithMe").font(.largeTitle)
            }
            
            if viewModel.isSigningIn {
                ProgressView("Signing in...").progressViewStyle(CircularProgressViewStyle())
            } else {
                HStack {
                    TextField("Username", text: $viewModel.username).textFieldStyle(RoundedBorderTextFieldStyle())
                        .padding()
                }
                .padding()
                
                Button("Sign in with Google",
                       action: viewModel.signInWithGoogle
                )
                
                Button("Sign in with Apple",
                       action: viewModel.logIn
                ).frame(width: 300, height: 50, alignment: .center).signInWithAppleButtonStyle(.white)
            }
            
            Spacer()
        }
    }
}

struct LoginView_Previews : PreviewProvider {
    static var previews: some View {
        LoginView(viewModel: .example)
    }
}
