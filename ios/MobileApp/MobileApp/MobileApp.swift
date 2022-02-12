import SwiftUI

@main
struct MobileApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var appDelegate
    @ObservedObject var viewModel = SingletonState.shared.mainViewModel

    var body: some Scene {
        WindowGroup {
            if viewModel.isLoggedIn {
                ContentView(viewModel: viewModel)
            } else {
                LoginView(viewModel: viewModel)
            }
        }
    }
}
