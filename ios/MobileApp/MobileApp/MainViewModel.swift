import Foundation
import GoogleSignIn

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
    
    let mainViewModel = MainViewModel()
    
    private init() {
        self.signInConfig = GIDConfiguration(
            clientID: "100813085034-kupatdninfaoreusett71309uujoumtg.apps.googleusercontent.com",
            serverClientID: "100813085034-huu6nmbj7uicgik0r6ms9oe90j51drl0.apps.googleusercontent.com")
    }
}
