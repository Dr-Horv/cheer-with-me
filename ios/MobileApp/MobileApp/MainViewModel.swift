import Foundation
import GoogleSignIn
import Alamofire

class MainViewModel: ObservableObject {
    @Published var isLoggedIn = false
    @Published var username = "Nrussian"
    @Published var isSigningIn = false
    @Published var friend: User?

    func logIn() {
        isSigningIn = true

        DispatchQueue.main.asyncAfter(deadline: .now() + .seconds(2)) {
            self.isSigningIn = false
            self.isLoggedIn = true
        }
    }

    func signInWithGoogle() {
        isSigningIn = true
        GIDSignIn.sharedInstance.signIn(with: SingletonState.shared.signInConfig, presenting: (UIApplication.shared.rootViewController)!) {
            user, error in

            guard error == nil else {
                self.isSigningIn = false
                self.isLoggedIn = false
                return
            }

            guard let user = user,
                  let serverAuthCode = user.serverAuthCode,
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
                    self.isLoggedIn = true
                    SingletonState.shared.token = tokenResponse.accessToken
                }

                self.isSigningIn = false
            }
        }
    }

    func getProfileInfo() {
        AF.request("\(BACKEND_URL)/user/me", headers: SingletonState.shared.authHeaders()).responseDecodable(of: User.self) { response in

            if let me = response.value {
                debugPrint(me)
                self.friend = me
            }
        }
    }
}

extension MainViewModel {
    static var example = MainViewModel()
}

class SingletonState {
    static let shared = SingletonState()
    
    let signInConfig: GIDConfiguration;
    
    var _token: String?
    
    var token: String {
        get {
            return _token ?? ""
        }
        set(newToken) {
            _token = newToken
            KeychainWrapper.standard.set(newToken, forKey: "authToken")
        }
    }
    
    func authHeaders() -> HTTPHeaders {
        let headers: HTTPHeaders = [
            "Authorization": "Bearer \(token)",
            "Accept": "application/json"
        ]
        
        return headers
    }
    
    let mainViewModel = MainViewModel()
    
    private init() {
        _token = KeychainWrapper.standard.string(forKey: "authToken")
        
        self.signInConfig = GIDConfiguration(
            clientID: "100813085034-kupatdninfaoreusett71309uujoumtg.apps.googleusercontent.com",
            serverClientID: "100813085034-huu6nmbj7uicgik0r6ms9oe90j51drl0.apps.googleusercontent.com")
    }
}
