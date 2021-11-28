import SwiftUI

@main
struct MobileApp: App {
    let viewModel = MainViewModel()

    var body: some Scene {
        WindowGroup {
            if viewModel.isLoggedIn {
                ContentView()
            } else {
                Text("Not logged in")
            }
        }
    }
}
