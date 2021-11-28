import Foundation

class MainViewModel: ObservableObject {
    @Published var isLoggedIn = false
    @Published var username = "Nrussian"
    @Published var isSigningIn = false

    func logIn() {
        isLoggedIn = true
    }
}
