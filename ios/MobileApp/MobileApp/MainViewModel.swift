import Foundation
import GoogleSignIn
import Alamofire

class MainViewModel: ObservableObject {
    @Published var username = "Nrussian"
    @Published var isSigningIn = false
    @Published var friend: User?
    private var google: AuthProviderProtocol

    init(google: AuthProviderProtocol) {
        self.google = google
        self.google.signinClosure = { self.objectWillChange.send() }
        self.google.signInFromCache()
    }

    var isLoggedIn: Bool {
        google.token != nil
    }

    var authHeaders: HTTPHeaders? {
        guard let token = google.token else {
            return nil
        }

        return HTTPHeaders([
            "Authorization": "Bearer \(token)",
            "Accept": "application/json"
        ])
    }

    func logIn() {
        isSigningIn = true

        DispatchQueue.main.asyncAfter(deadline: .now() + .seconds(2)) {
            self.isSigningIn = false
        }
    }

    func signInWithGoogle() {
        google.signIn()
    }

    func signOut() {
        google.signOut()
        self.objectWillChange.send()
    }

    func getProfileInfo() {
        guard let headers = authHeaders else {
            return
        }

        AF.request("\(BACKEND_URL)/user/me", headers: headers).responseDecodable(of: User.self) { response in
            if let me = response.value {
                debugPrint(me)
                self.friend = me
            }
        }
    }
}

extension MainViewModel {
    static var example = MainViewModel(google: AuthProviderMock())
}
