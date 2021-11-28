import SwiftUI
import URLImage

let AVATAR_HEIGHT = 40.0

struct FriendsView: View {
    let viewModel = FriendsViewModel()
    
    var body: some View {
        List {
            Section(header: Text("FRIEND REQUESTS")) {
                ForEach(viewModel.waitingFriends) { friend in
                    friendItem(friend: friend,
                               showButtons: true)
                }
            }
            
            Section(header: Text("FRIENDS")) {
                ForEach(viewModel.friends) { friend in
                    friendItem(friend: friend,
                               showButtons: false)
                }
            }.listStyle(GroupedListStyle())
        }
    }
}

private struct friendItem: View {
    let friend: Friend
    let showButtons: Bool

    var body: some View {
        HStack {
            URLImage(URL(string: friend.avatarUrl)!) { image in
                image
                    .resizable()
                    .aspectRatio(contentMode: .fit)
            }.frame(width: AVATAR_HEIGHT, height: AVATAR_HEIGHT)
                .clipShape(Circle()).padding([.trailing], 20)

            Text(friend.name)

            if showButtons {
                Spacer()

                CircleButton(color: Color.gray, icon: .cross)
                CircleButton(color: Color.orange, icon: .check)
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

    var typeString: String {
        switch icon {
        case .check:
            return "checkmark.circle"
        case .cross:
            return "x.circle"
        }
    }

    var body: some View {
        Button(action: {
            print("CLICK \(self.typeString)")
        }) {
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
        FriendsView()
    }
}
