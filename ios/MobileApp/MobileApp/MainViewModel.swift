import Foundation
import GoogleSignIn

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

    @MainActor
    func getProfileInfo() async {
        guard let token = google.token else {
            return
        }
        
        do {
            let url = URL(string: "\(BACKEND_URL)/user/me")!
            var request = URLRequest(url: url)
            request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
            request.setValue("application/json", forHTTPHeaderField: "Accept")

            let (data, _) = try await URLSession.shared.data(for: request)
            let me = try JSONDecoder().decode(User.self, from: data)
            self.friend = me
        } catch {
            print("Error getProfileInfo: \(error)")
        }
    }
}

extension MainViewModel {
    static var example = MainViewModel(authProvider: AuthProviderMock())
}
