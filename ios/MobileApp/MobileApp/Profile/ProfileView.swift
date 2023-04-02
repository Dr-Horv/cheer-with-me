import GoogleSignIn
import SwiftUI
import URLImage

struct ProfileView: View {
    @ObservedObject var viewModel: MainViewModel
    
    var body: some View {
        VStack {
            List {
                HStack {
                    if let friend = viewModel.friend {
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
                        Text(friend.nick)
                        Spacer()
                        
                    } else {
                        ProgressView().progressViewStyle(.circular)
                    }
                }
                Section {
                    Button("Sign out") {
                        viewModel.signOut()
                    }
                    .foregroundColor(.red)
                }
            }
        }.task {
            await viewModel.getProfileInfo()
        }
    }
}

struct ProfileView_Previews: PreviewProvider {
    static var previews: some View {
        ProfileView(viewModel: .example)
    }
}
