import Foundation
import GoogleSignIn
import Alamofire

class MainViewModel: ObservableObject {
    @Published var username = ""
    @Published var isSigningIn = false
    @Published var friend: User?
    private var google: AuthProviderProtocol

    init(authProvider: AuthProviderProtocol) {
        google = authProvider
        google.signinClosure = { self.objectWillChange.send() }
        google.signInFromCache()
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

    func getProfileInfo() async {
        guard let headers = authHeaders else {
            return
        }
        
        do {
            let request = try URLRequest(url: "\(BACKEND_URL)/user/me", method: .get, headers: headers)
            let (data, _) = try await URLSession.shared.data(for: request)
            let me = try JSONDecoder().decode(User.self, from: data)
            DispatchQueue.main.async {
                self.friend = me
            }
        } catch {
            print("Error getProfileInfo: \(error)")
        }
    }
}

extension MainViewModel {
    static var example = MainViewModel(authProvider: AuthProviderMock())
}
