import SwiftUI
import URLImage
import Alamofire

let AVATAR_HEIGHT = 40.0


struct FriendsView: View {
    @EnvironmentObject var viewModel: FriendsViewModel
    // var viewModel: FriendsViewModel
    @State var showAddFriend: Bool = false

    var body: some View {
        NavigationView {
            List {
                if !viewModel.outgoingFriendRequests.isEmpty {
                    Section(header: Text("SENT FRIEND REQUESTS")) {
                        ForEach(viewModel.outgoingFriendRequests) { friend in
                            FriendItem(friend)
                        }
                    }.listStyle(GroupedListStyle())
                }

                if !viewModel.waitingFriends.isEmpty {
                    Section(header: Text("FRIEND REQUESTS")) {
                        ForEach(viewModel.waitingFriends) { friend in
                            FriendRequestItem(friend: friend)
                        }
                    }.listStyle(GroupedListStyle())
                }

                Section(header: Text("FRIENDS")) {
                    ForEach(viewModel.friends) { friend in
                        FriendItem(friend)
                    }
                }.listStyle(GroupedListStyle())

                Button(action: { self.showAddFriend = true }) {
                    HStack {
                        Image(systemName: "plus")
                        Text("Add friend")
                    }
                }.foregroundColor(.accentColor)

            }.navigationTitle("Friends")
            .refreshable {
                await viewModel.getFriends()
            }
            .task {
                await viewModel.getFriends()
            }
            .sheet(isPresented: $showAddFriend) {
                FriendSearchView(viewModel: FriendsSearchViewModel(parentViewModel: viewModel))
            }
        }
    }
}

struct FriendItem<AccessoryView: View>: View {
    let friend: User
    let accessoryView: AccessoryView?
    @EnvironmentObject var viewModel: FriendsViewModel
    
    init(friend: User, @ViewBuilder accessoryView: () -> AccessoryView) {
        self.friend = friend
        self.accessoryView = accessoryView()
    }

    var body: some View {
        HStack {
            if let avatarUrl = friend.avatarUrl {
                URLImage(URL(string: avatarUrl)!) { image in
                    image
                        .resizable()
                        .aspectRatio(contentMode: .fit)
                }.frame(width: AVATAR_HEIGHT, height: AVATAR_HEIGHT)
                    .clipShape(Circle()).padding([.trailing], 20)
            } else {
                Circle().frame(width: AVATAR_HEIGHT, height: AVATAR_HEIGHT)
                    .clipShape(Circle()).padding([.trailing], 20)
            }

            Text(friend.nick)
            
            accessoryView
        }
    }
}

extension FriendItem where AccessoryView == EmptyView {
  init(_ friend: User) {
      self.init(friend: friend, accessoryView: { EmptyView() })
  }
}


struct FriendRequestItem: View {
    let friend: User
    @EnvironmentObject var viewModel: FriendsViewModel

    var body: some View {
        FriendItem(friend: friend, accessoryView: {

            Spacer()

            CircleButton(color: Color.gray,
                         icon: .cross) {
                withAnimation {
                    viewModel.ignore(person: friend)
                }
            }
            CircleButton(color: Color.orange, icon: .check) {
                Task {
                    await viewModel.befriend(person: friend)
                }
            }
        })
    }
}

private struct FriendSearchView: View {    
    @ObservedObject var viewModel: FriendsSearchViewModel

    var body: some View {
        VStack {
            Text("Add Friends")
                .bold()

            HStack {
                Image(systemName: "magnifyingglass")
                TextField("Search", text: $viewModel.query)
                    .submitLabel(.search)
                    .onSubmit {
                        Task {
                            await viewModel.searchFriends()
                        }
                    }
            }
            .padding(EdgeInsets(top: 8, leading: 6, bottom: 8, trailing: 6))
            .foregroundColor(.secondary)
            .background(Color(.secondarySystemBackground))
            .cornerRadius(10)

            List {
                if viewModel.isLoading {
                    ProgressView().progressViewStyle(.circular)
                }

                Section {
                    ForEach(viewModel.results) { friend in
                        FriendRequestItem(friend: friend)
                    }
                }
            }
            .listStyle(GroupedListStyle())
            .onDisappear {
                viewModel.results = []
                viewModel.query = ""
            }
        }
        .padding(20)
    }
}

private enum CircleButtonType {
    case check
    case cross
}

private struct CircleButton: View {
    let color: Color
    let icon: CircleButtonType
    let action: () -> Void

    var typeString: String {
        switch icon {
        case .check:
            return "checkmark.circle"
        case .cross:
            return "x.circle"
        }
    }

    var body: some View {
        Button(action: action) {
            Image(systemName: typeString)
                .resizable()
                .padding(5)
                .frame(width: AVATAR_HEIGHT,
                       height: AVATAR_HEIGHT)
        }
        .buttonStyle(PlainButtonStyle())
        .foregroundColor(color)
    }
}

/*
struct FriendsView_Previews: PreviewProvider {
    static var previews: some View {
        FriendsView(viewModel: .example)
    }
}*/

struct FriendsSearchView_Previews: PreviewProvider {
    static var previews: some View {
        FriendSearchView(viewModel: .example)
    }
}
