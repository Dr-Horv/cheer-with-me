//
//  FriendsView.swift
//  CheerWithMe
//
//  Created by Johan Lindskogen on 2019-07-23.
//  Copyright © 2019 Johan Lindskogen. All rights reserved.
//

import SwiftUI

struct UserRow: View {
    let username: String
    
    var body: some View {
        HStack(spacing: 16) {
            Text(FontIcon.user.rawValue)
                .font(.custom("FontAwesome5Pro-Regular", size: 25)).foregroundColor(.primary).frame(width: 44, height: 44).background(Circle().foregroundColor(.hex(0xf6be27)))
            
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
    let friends: [String]
    let requests: [String]
    
    var body: some View {
        ScrollView {
            SmallHeader(text: "Friend requests")
            ForEach(requests, id: \.self) { username in
                HStack {
                    UserRow(username: username)
                    Button(action: {
                        print("Pressed \(username): x")
                    }) {
                        FontAwesomeIcon(icon: .times, size: 20).frame(width: 42, height: 40).background(Circle().stroke(lineWidth: 2))
                            .foregroundColor(.hex(0x9aa0a6))
                    }
                    Button(action: {
                        print("Pressed \(username): ✔")
                    }) {
                        FontAwesomeIcon(icon: .check, size: 20).frame(width: 42, height: 40).background(Circle().stroke(lineWidth: 2))
                            .foregroundColor(.hex(0xf9ab02))
                    }.padding(.leading, 10)
                }
                .padding(.bottom, 10)
                
            }
            
            SmallHeader(text: "Friends")
                .padding(.top)
            ForEach(friends, id: \.self) { username in
                UserRow(username: username)
                    .padding(.bottom, 10)
            }
        }.padding()
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


let allFriends = ["Alkohorv", "Andrøhl", "Screwdriver", "Trivodka"]

let allRequests = ["Nrussain", "Joale"]

struct SmallHeader: View {
    let text: String
    
    var body: some View {
        HStack {
            Text(text.uppercased()).font(.caption).foregroundColor(.secondary)
            LowPrioritySpacer()
        }
    }
}
