import SwiftUI

@main
struct MobileApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var appDelegate
    @ObservedObject var viewModel: MainViewModel
    private let auth: AuthProviderProtocol

    init() {
        let auth = GoogleAuth()
        self.auth = auth
        viewModel = MainViewModel(authProvider: auth)
    }

    var body: some Scene {
        WindowGroup {
            if viewModel.isLoggedIn {
                ContentView(viewModel: viewModel, google: auth)
            } else {
                LoginView(viewModel: viewModel)
            }
        }
    }
}
