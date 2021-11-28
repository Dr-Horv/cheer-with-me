import Foundation

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
