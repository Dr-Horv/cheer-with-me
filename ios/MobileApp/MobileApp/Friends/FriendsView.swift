import SwiftUI
import URLImage

let AVATAR_HEIGHT = 40.0

struct FriendsView: View {
    @ObservedObject var viewModel: FriendsViewModel

    var body: some View {
        if viewModel.isLoading {
            ProgressView().progressViewStyle(.circular)
        }
        List {
            if !viewModel.waitingFriends.isEmpty {
                Section(header: Text("FRIEND REQUESTS")) {
                    ForEach(viewModel.waitingFriends) { friend in
                        friendItem(friend: friend,
                                   showButtons: true,
                                   viewModel: viewModel)
                    }
                }
            }

            Section(header: Text("FRIENDS")) {
                ForEach(viewModel.friends) { friend in
                    friendItem(friend: friend,
                               showButtons: false,
                               viewModel: viewModel)
                }
            }.listStyle(GroupedListStyle())
        }.onAppear {
            viewModel.getFriends()
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
                    print("CLICK")
                }
                CircleButton(color: Color.orange, icon: .check) {
                    withAnimation {
                        viewModel.befriend(person: friend)
                    }
                    print("CLICK")
                }
            }
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
