import SwiftUI
import URLImage

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
                        }.frame(width: 60, height: 60)
                            .clipShape(Circle()).padding([.trailing], 20)
                        
                        Text(friend.name)
                        
                        
                        Spacer()
                        
                        Circle().stroke(Color.gray, lineWidth: 2).frame(width: 60, height: 60).onTapGesture {
                            print("CLICK 2")
                        }
                        
                        Circle().stroke(Color.orange, lineWidth: 2).frame(width: 60, height: 60).onTapGesture {
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
                        }.frame(width: 60, height: 60)
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
