import GoogleSignIn
import SwiftUI
import URLImage
import Alamofire

struct ProfileView: View {
    @ObservedObject var viewModel: MainViewModel
    let friend = Friend(id: 2, name: "Ndushierino", avatarUrl: "https://randomuser.me/api/portraits/men/90.jpg")
    
    var body: some View {
        VStack {
            List {
                HStack {
                    if let avatarUrl = friend.avatarUrl {
                        URLImage(URL(string: avatarUrl)!) { image in
                            image
                                .resizable()
                                .aspectRatio(contentMode: .fit)
                        }.frame(width: AVATAR_HEIGHT, height: AVATAR_HEIGHT)
                            .clipShape(Circle()).padding([.trailing], 20)
                    } else {
                        Circle()
                            .frame(width: AVATAR_HEIGHT, height: AVATAR_HEIGHT)
                            .clipShape(Circle()).padding([.trailing], 20)
                    }
                    Text(friend.name)
                    Spacer()
                }
                Section {
                    Button("Sign out") {
                        GIDSignIn.sharedInstance.signOut()
                        viewModel.isLoggedIn = false
                    }
                    .foregroundColor(.red)
                }
            }
        }.onAppear {
            AF.request("\(BACKEND_URL)/user/me", headers: SingletonState.shared.authHeaders()).responseDecodable(of: User.self) { response in
                
                if let me = response.value {
                    debugPrint(me)
                    friend = me
                }
            }
        }
    }
}

struct ProfileView_Previews: PreviewProvider {
    static var previews: some View {
        ProfileView(viewModel: .example)
    }
}
