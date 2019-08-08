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
        HStack {
            ZStack {
                Circle().foregroundColor(.hex(0xf6be27))
                Text(FontIcon.user.rawValue)
                                .font(.custom("FontAwesome5Pro-Regular", size: 25)).foregroundColor(.white)
            }.frame(width: 44, height: 44)
            
            
            Text(username)
        }
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
        List {
            Section(header: Text("Friend requests")) {
                ForEach(requests, id: \.self) { username in
                    HStack {
                        UserRow(username: username)
                        Spacer()
                        Button(action: {
                            print("Pressed \(username): x")
                        }) {
                            ZStack {
                                Circle().stroke(lineWidth: 2)
                                FontAwesomeIcon(icon: .times, size: 25)
                            }.foregroundColor(.hex(0x9aa0a6)).frame(width: 44)
                        }
                        Button(action: {
                            print("Pressed \(username): ✔")
                        }) {
                            ZStack {
                                Circle().stroke(lineWidth: 2)
                                FontAwesomeIcon(icon: .check, size: 25)
                                
                            }.foregroundColor(.hex(0xf9ab02))
                                .frame(width: 44)
                        }
                    }.padding([.top, .bottom])
                        
                }
                
            }
            
            Section(header: Text("Friends")) {
                ForEach(friends, id: \.self) { username in
                    UserRow(username: username)
                }
            }
        }.listStyle(GroupedListStyle())
            .listRowInsets(EdgeInsets(top: 10, leading: 0, bottom: 10, trailing: 0))
    }
}

#if DEBUG
struct FriendsView_Previews: PreviewProvider {
    static var previews: some View {
        Group {
            FriendsView(friends: allFriends, requests: allRequests)
            FriendsView(friends: allFriends, requests: allRequests)
                .environment(\.colorScheme, .dark)
        }
    }
}
#endif


let allFriends = ["Alkohorv", "Andrøhl", "Screwdriver", "Trivodka"]

let allRequests = ["Nrussain", "Joale"]
