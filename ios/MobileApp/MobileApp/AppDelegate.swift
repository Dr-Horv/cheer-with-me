import UIKit
import Foundation
import GoogleSignIn


struct Login: Encodable {
    let code: String
}


class AppDelegate: NSObject, UIApplicationDelegate {
    
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
      GIDSignIn.sharedInstance.restorePreviousSignIn { user, error in
        if error != nil || user == nil {
            SingletonState.shared.mainViewModel.isLoggedIn = false
            SingletonState.shared.mainViewModel.isSigningIn = false
        } else {
            SingletonState.shared.mainViewModel.isLoggedIn = true
            SingletonState.shared.mainViewModel.isSigningIn = false
            
        }
      }
      return true
    }
    
    
    func application(_ app: UIApplication, open url: URL, options: [UIApplication.OpenURLOptionsKey : Any] = [:]) -> Bool {
      var handled: Bool

      handled = GIDSignIn.sharedInstance.handle(url)
      if handled {
        return true
      }

      // Handle other custom URL types.

      // If not handled by this app, return false.
      return false
    }

}


extension UIApplication {
  var currentKeyWindow: UIWindow? {
    UIApplication.shared.connectedScenes
      .filter { $0.activationState == .foregroundActive }
      .map { $0 as? UIWindowScene }
      .compactMap { $0 }
      .first?.windows
      .filter { $0.isKeyWindow }
      .first
  }

  var rootViewController: UIViewController? {
    currentKeyWindow?.rootViewController
  }
}
