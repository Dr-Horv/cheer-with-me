import SwiftUI
import AuthenticationServices

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

                SignInWithAppleButton(.signIn,
                    onRequest: { request in
                        request.requestedScopes = []
                    },
                    onCompletion: { result in
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
