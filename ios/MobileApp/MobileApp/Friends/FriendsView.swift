import SwiftUI
import URLImage

let AVATAR_HEIGHT = 40.0

struct FriendsView: View {
    let viewModel = FriendsViewModel()
    
    var body: some View {
        List {
            Section(header: Text("FRIEND REQUESTS")) {
                ForEach(viewModel.waitingFriends) { friend in
                    HStack {
                        URLImage(URL(string: friend.avatarUrl)!) { image in
                            image
                                .resizable()
                                .aspectRatio(contentMode: .fit)
                        }.frame(width: AVATAR_HEIGHT, height: AVATAR_HEIGHT)
                            .clipShape(Circle()).padding([.trailing], 20)
                        
                        Text(friend.name)
                        
                        
                        Spacer()
                        
                        Circle().stroke(Color.gray, lineWidth: 2).frame(width: AVATAR_HEIGHT, height: AVATAR_HEIGHT).onTapGesture {
                            print("CLICK 2")
                        }
                        
                        Circle().stroke(Color.orange, lineWidth: 2).frame(width: AVATAR_HEIGHT, height: AVATAR_HEIGHT).onTapGesture {
                            print("CLICK")
                        }
                    }
                }
            }
            
            Section(header: Text("FRIENDS")) {
                ForEach(viewModel.friends) { friend in
                    HStack {
                        URLImage(URL(string: friend.avatarUrl)!) { image in
                            image
                                .resizable()
                                .aspectRatio(contentMode: .fit)
                        }.frame(width: AVATAR_HEIGHT, height: AVATAR_HEIGHT)
                            .clipShape(Circle()).padding([.trailing], 20)
                        
                        Text(friend.name)
                    }
                }
            }.listStyle(GroupedListStyle())
        }
    }
}

struct FriendsView_Previews: PreviewProvider {
    static var previews: some View {
        FriendsView()
    }
}
