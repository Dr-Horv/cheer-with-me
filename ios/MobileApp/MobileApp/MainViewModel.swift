import Foundation
import GoogleSignIn
import Alamofire

class MainViewModel: ObservableObject {
    @Published var isLoggedIn = false
    @Published var username = "Nrussian"
    @Published var isSigningIn = false

    func logIn() {
        isSigningIn = true

        DispatchQueue.main.asyncAfter(deadline: .now() + .seconds(2)) {
            self.isSigningIn = false
            self.isLoggedIn = true
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
