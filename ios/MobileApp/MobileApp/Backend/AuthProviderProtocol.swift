protocol AuthProviderProtocol {
    var token: String? { get }
    var isSignedIn: Bool { get }

    func signIn()
    func signInFromCache()
}

class AuthProviderMock: AuthProviderProtocol {
    var token: String? = "TOKEN"
    var isSignedIn = true

    func signIn() {}
    func signInFromCache() {}
}
