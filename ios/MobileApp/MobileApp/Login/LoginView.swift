import SwiftUI
import AuthenticationServices

struct LoginView : View {
    @ObservedObject var viewModel: MainViewModel

//    func handleAuthResult(_ result: Result<ASAuthorization, Error>) -> Void {
//        self.signingIn = true
//
//        switch result {
//        case .success (let authResult):
//            print("Authorization successful.")
//            print(authResult)
//            if let appleIDCredential = authResult.credential as? ASAuthorizationAppleIDCredential {
//                let userIdentifier = appleIDCredential.user
//                let fullName = appleIDCredential.fullName
//                let email = appleIDCredential.email
//
//                let identityToken = appleIDCredential.identityToken.flatMap { String(data: $0, encoding: .utf8) }
//                let authorizationCode = appleIDCredential.authorizationCode.flatMap { String(data: $0, encoding: .utf8) }
//
//                print(userIdentifier, fullName as Any, email as Any, identityToken as Any, authorizationCode as Any)
//
//                BackendService.shared.token = identityToken
//
//                guard let code = authorizationCode else {
//                    preconditionFailure("authorizationCode must be defined")
//                }
//
//                BackendService.shared.register(payload: .init(code: code, nick: username)) { response in
//                    print(response)
//                    BackendService.shared.token = response.accessToken
//                    self.signingIn = false
//                    self.setSignedIn(true)
//                }
//            }
//        case .failure (let error):
//            self.signingIn = false
//            print("Authorization failed: \(error.localizedDescription)")
//        }
//    }

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

                SignInWithAppleButton(.signIn,
                    onRequest: { request in
                        request.requestedScopes = []
                    },
                    onCompletion: { result in
//                        self.handleAuthResult(result)
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
        LoginView(viewModel: MainViewModel())
    }
}
