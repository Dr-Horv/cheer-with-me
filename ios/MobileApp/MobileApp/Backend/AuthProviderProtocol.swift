protocol AuthProviderProtocol {
    var token: String? { get }
    var isSignedIn: Bool { get }
    var signinClosure: (() -> Void)? { get set }

    func signIn()
    func signInFromCache()
    func signOut()
}

class AuthProviderMock: AuthProviderProtocol {
    var token: String? = "TOKEN"
    var isSignedIn = true
    var signinClosure: (() -> Void)? = {}

    func signIn() {}
    func signInFromCache() {}
    func signOut() {}
}
