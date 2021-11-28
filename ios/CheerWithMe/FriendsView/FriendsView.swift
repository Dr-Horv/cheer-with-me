//
//  FriendsView.swift
//  CheerWithMe
//
//  Created by Johan Lindskogen on 2019-07-23.
//  Copyright © 2019 Johan Lindskogen. All rights reserved.
//

import SwiftUI
import URLImage

struct UserAvatar: View {

    let url: URL
    let size: CGFloat

    var body: some View {
        URLImage(url,
                 processors: [ Resize(size: CGSize(width: size, height: size), scale: UIScreen.main.scale) ],
                 content: {
                    $0.image
                        .resizable()
                        .aspectRatio(contentMode: .fill)
                        .clipped()
                 }
        ).frame(width: size, height: size).clipShape(Circle())
    }
}


struct UserPlaceholder: View {
    var body: some View {
        FontAwesomeIcon(icon: .user, size: 25)
            .foregroundColor(.primary)
            .frame(width: 44, height: 44)
            .background(
                Circle().foregroundColor(.hex(0xf6be27))
            )
    }
}



struct UserRow: View {
    let username: String
    let avatarUrl: String?

    func getUrl() -> URL? {
        avatarUrl.flatMap { URL(string: $0) }
    }
    
    func getUserInitials() -> String? {
        username.first.map { String($0) }
    }

    var body: some View {
        HStack(spacing: 16) {
            if let url = getUrl() {
                UserAvatar(url: url, size: 44.0)
            } else {
                UserInitialsPlaceholder(initials: getUserInitials() ?? "")
            }



            Text(username)
            LowPrioritySpacer()
        }.frame(height: 44)
    }
}

struct FontAwesomeIcon: View {
    let icon: FontIcon
    let size: CGFloat

    var body: some View {
        Text(icon.rawValue)
            .font(.custom("FontAwesome5Pro-Regular", size: size))
    }
}

struct FriendsView: View {
    let friends: [UserResponse]
    let requests: [UserResponse]

    @State private var showSearch = false
    @State private var searchQuery = ""
    @State private var results: [UserResponse] = []

    var body: some View {
        ScrollView {
            SmallHeader(text: "Friend requests")
            ForEach(requests) { friend in
                HStack {
                    UserRow(username: friend.nick, avatarUrl: friend.avatarUrl)
                    Button(action: {
                        print("Pressed \(friend.nick): x")
                    }) {
                        FontAwesomeIcon(icon: .times, size: 20).frame(width: 42, height: 40).background(Circle().stroke(lineWidth: 2))
                            .foregroundColor(.hex(0x9aa0a6))
                    }
                    Button(action: {
                        print("Pressed \(friend.nick): ✔")
                        BackendService.shared.acceptFriendRequest(userId: friend.id) { success in
                            print(success)
                        }
                    }) {
                        FontAwesomeIcon(icon: .check, size: 20).frame(width: 42, height: 40).background(Circle().stroke(lineWidth: 2))
                            .foregroundColor(.hex(0xf9ab02))
                    }.padding(.leading, 10)
                }
                .padding(.bottom, 10)

            }

            SmallHeader(text: "Friends")
                .padding(.top)
            ForEach(friends) { friend in
                UserRow(username: friend.nick, avatarUrl: friend.avatarUrl)
                    .padding(.bottom, 10)
            }
        }.padding()
        .navigationBarItems(trailing:
            Button(action: {
                showSearch = true
            }) {
                Image(systemName: "plus.circle")
            }
        )
        .sheet(isPresented: $showSearch, onDismiss: {
            searchQuery = ""
            results = []
        }) {
            UserSearchView(searchQuery: $searchQuery, results: $results)
        }
    }
}

#if DEBUG
struct FriendsView_Previews: PreviewProvider {
    static var previews: some View {
        Group {
            FriendsView(friends: allFriends, requests: allRequests).environment(\.colorScheme, .dark).background(Color.hex(0x202124))

            FriendsView(friends: allFriends, requests: allRequests)

        }
    }
}
#endif


let allFriends = [UserResponse(id: 1, nick: "Alkohorv", avatarUrl: nil),
                  UserResponse(id: 2, nick: "Andrøhl", avatarUrl: nil),
                  UserResponse(id: 3, nick: "Screwdriver", avatarUrl: nil),
                  UserResponse(id: 4, nick: "Trivodka", avatarUrl: nil)]

let allRequests = [UserResponse(id: 5, nick: "Nrussian", avatarUrl: nil),
                   UserResponse(id: 6, nick: "Joale", avatarUrl: nil)]

struct SmallHeader: View {
    let text: String

    var body: some View {
        HStack {
            Text(text.uppercased()).font(.caption).foregroundColor(.secondary).padding(.bottom, 10)
            Spacer()
        }
    }
}
