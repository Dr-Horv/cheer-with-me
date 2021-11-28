import SwiftUI
import AuthenticationServices
import GoogleSignIn
import UIKit
import Alamofire

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
                
                Button("Sign in with Google", action: {
                    viewModel.isSigningIn = true
                    GIDSignIn.sharedInstance.signIn(with: SingletonState.shared.signInConfig, presenting: (UIApplication.shared.rootViewController)!) {
                        user, error in
                        
                        guard error == nil else {
                            viewModel.isSigningIn = false
                            viewModel.isLoggedIn = false
                            return
                        }
                        
                        if let user = user,
                           let serverAuthCode = user.serverAuthCode,
                           let idToken = user.authentication.idToken {
                            let params = Login(code: serverAuthCode)
                            
                            
                            
                            let headers: HTTPHeaders = [
                                "Authorization": "Bearer \(idToken)",
                                "Accept": "application/json"
                            ]
                            
                            AF.request("http://192.168.1.127:8080/login/google", method: .post, parameters: params, encoder: JSONParameterEncoder.default, headers: headers).response {
                                response in
                                
                                viewModel.isSigningIn = false
                                
                                switch response.result {
                                case .success:
                                    viewModel.isLoggedIn = true
                                case let .failure(error):
                                    print(error)
                                }
                                
                            }
                            
                        }
                        
                    }

                })

                Button("Sign in with Apple", action: {
                        viewModel.logIn()
                    }
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
