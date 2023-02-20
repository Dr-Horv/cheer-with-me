import SwiftUI
import URLImage
import URLImageStore

@main
struct MobileApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var appDelegate
    @ObservedObject var viewModel: MainViewModel
    private let auth: AuthProviderProtocol
    
    let urlImageService = URLImageService(fileStore: nil, inMemoryStore: URLImageInMemoryStore())
    

    init() {
        let auth = GoogleAuth()
        self.auth = auth
        viewModel = MainViewModel(authProvider: auth)
    }

    var body: some Scene {
        WindowGroup {
            if viewModel.isLoggedIn {
                ContentView(viewModel: viewModel, google: auth)
                    .environment(\.urlImageService, urlImageService)
            } else {
                LoginView(viewModel: viewModel)
            }
        }
    
    }
}
