//
//  UserSearchView.swift
//  CheerWithMe
//
//  Created by Johan Lindskogen on 2020-07-23.
//  Copyright © 2020 Johan Lindskogen. All rights reserved.
//

import SwiftUI

struct UserSearchView: View {
    @Binding var searchQuery: String
    @Binding var results: [UserResponse]
    @State var loading = false
    
    var body: some View {
        VStack {
            SearchBar(text: $searchQuery, placeholder: "Search users",
                      onPressSearch: { query in
                loading = true
                BackendService.shared.searchUsers(query) { users in
                    results = users
                    loading = false
                }
                      }
            )
                
                if loading {
                ProgressView("Fetching users...")
            }
            ForEach(results) { user in
                HStack {
                    UserRow(username: user.nick, avatarUrl: user.avatarUrl)
                    Button(action: {
                        BackendService.shared.sendFriendRequest(userId: user.id) { success in
                            print(success)
                        }
                    }) {
                        FontAwesomeIcon(icon: .userPlus, size: 20).frame(width: 42, height: 40).background(Circle().stroke(lineWidth: 2))
                            .foregroundColor(.hex(0xf9ab02))
                    }
                }.padding()
            }
            Spacer()
        }
    }
}

struct UserSearchContainer_Preview: View {
    @State private var input = ""
    @State private var results: [UserResponse] = allFriends
    
    
    var body: some View {
        UserSearchView(searchQuery: $input, results: $results)
    }
}

struct UserSearchView_Previews: PreviewProvider {
    
    static var previews: some View {
        UserSearchContainer_Preview()
    }
}
