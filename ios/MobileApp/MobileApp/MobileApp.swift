import SwiftUI

@main
struct MobileApp: App {
    @ObservedObject var viewModel = MainViewModel()

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
