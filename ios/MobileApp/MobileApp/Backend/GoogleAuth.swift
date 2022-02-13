import Alamofire
import Foundation
import GoogleSignIn

class GoogleAuth: AuthProviderProtocol, ObservableObject {
    @Published var token: String?
    var signinClosure: (() -> Void)? = {}

    var isSignedIn: Bool {
        token != nil
    }

    private var signInConfig = GIDConfiguration(
        clientID: "100813085034-kupatdninfaoreusett71309uujoumtg.apps.googleusercontent.com",
        serverClientID: "100813085034-huu6nmbj7uicgik0r6ms9oe90j51drl0.apps.googleusercontent.com"
    )

    func signIn() {
        GIDSignIn.sharedInstance.signIn(with: signInConfig, presenting: (UIApplication.shared.rootViewController)!) {
            user, error in

            guard error == nil,
                  let user = user
            else {
                return
            }

            self.sendGoogleTokenToBackend(user: user)
        }
    }

    func sendGoogleTokenToBackend(user: GIDGoogleUser) {
        guard let serverAuthCode = user.serverAuthCode,
              let idToken = user.authentication.idToken else {
            return
        }

        let params = Login(code: serverAuthCode)
        let headers: HTTPHeaders = [
            "Authorization": "Bearer \(idToken)",
            "Accept": "application/json"
        ]

        AF.request("\(BACKEND_URL)/login/google", method: .post, parameters: params, encoder: JSONParameterEncoder.default, headers: headers).responseDecodable(of: AccessTokenResponse.self) {
            response in

            if let tokenResponse = response.value {
                let token = tokenResponse.accessToken
                self.token = token
                KeychainWrapper.standard.set(token, forKey: "authToken")
                self.signinClosure?()
            }
        }
    }

    func signInFromCache() {
        if let token = KeychainWrapper.standard.string(forKey: "authToken") {
            self.token = token
            signinClosure?()
            return
        }

        GIDSignIn.sharedInstance.restorePreviousSignIn { user, error in
            if let user = user,
               error == nil {
                self.sendGoogleTokenToBackend(user: user)
            }
        }
    }

    func signOut() {
        GIDSignIn.sharedInstance.signOut()
        KeychainWrapper.standard.removeObject(forKey: "authToken")
        token = nil
    }
}
