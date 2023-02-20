import SwiftUI
import URLImage
import Alamofire

let AVATAR_HEIGHT = 40.0

struct FriendsView: View {
    @ObservedObject var viewModel: FriendsViewModel

    var body: some View {
        NavigationView {
            List {
                if !viewModel.outgoingFriendRequests.isEmpty {
                    Section(header: Text("SENT FRIEND REQUESTS")) {
                        ForEach(viewModel.outgoingFriendRequests) { friend in
                            friendItem(friend: friend,
                                       showButtons: false,
                                       viewModel: viewModel)
                        }
                    }.listStyle(GroupedListStyle())
                }

                if !viewModel.waitingFriends.isEmpty {
                    Section(header: Text("FRIEND REQUESTS")) {
                        ForEach(viewModel.waitingFriends) { friend in
                            friendItem(friend: friend,
                                       showButtons: true,
                                       viewModel: viewModel)
                        }
                    }.listStyle(GroupedListStyle())
                }

                Section(header: Text("FRIENDS")) {
                    ForEach(viewModel.friends) { friend in
                        friendItem(friend: friend,
                                   showButtons: false,
                                   viewModel: viewModel)
                    }
                }.listStyle(GroupedListStyle())
                
                NavigationLink(destination:
                    FriendSearchView(viewModel: FriendsSearchViewModel(parentViewModel: viewModel))
                ) {
                    HStack {
                        Image(systemName: "plus")
                        Text("Add friend")
                    }
                }.foregroundColor(.accentColor)

            }.navigationTitle("Friends")
            .refreshable {
                await viewModel.getFriends()
            }.task {
                await viewModel.getFriends()
            }
            
        }
    }
}

private struct friendItem: View {
    let friend: User
    let showButtons: Bool
    @State var viewModel: FriendsViewModel

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

            if showButtons {
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
            }
        }
    }
}

private struct FriendSearchView: View {    
    @ObservedObject var viewModel: FriendsSearchViewModel
    
    var body: some View {
        List {
            Section{
                TextField("Search", text: $viewModel.query)
                    .submitLabel(.search)
                    .onSubmit {
                        Task {
                            await viewModel.searchFriends()
                        }
                    }
            }
            
            if viewModel.isLoading {
                ProgressView().progressViewStyle(.circular)
            }
            
                
            Section {
                ForEach(viewModel.results) { friend in
                    friendItem(friend: friend,
                               showButtons: true,
                               viewModel: viewModel.parentViewModel)
                }
            }
        }.listStyle(GroupedListStyle())
        .onDisappear {
            viewModel.results = []
            viewModel.query = ""
        }
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

struct FriendsView_Previews: PreviewProvider {
    static var previews: some View {
        FriendsView(viewModel: .example)
    }
}
