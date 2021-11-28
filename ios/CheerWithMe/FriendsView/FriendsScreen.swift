//
//  FriendsScreen.swift
//  CheerWithMe
//
//  Created by Johan Lindskogen on 2020-07-23.
//  Copyright © 2020 Johan Lindskogen. All rights reserved.
//

import Foundation
import SwiftUI


struct FriendsScreen: View {
    @State private var friends: [UserResponse] = []
    @State private var requests: [UserResponse] = []
    @State private var loading = false
    
    var body: some View {
        NavigationView {
            if loading {
                ProgressView("Loading friends...")
            }
            FriendsView(friends: friends, requests: requests)
                .navigationBarTitle("Friends")
        }
        .onAppear(perform: fetch)
    }
    
    private func fetch() {
        print("appear!")
        self.loading = true
        BackendService.shared.getOutstandingFriendRequests { response in
            if let response = response {
                print("response!")
                self.friends = response.friends
                self.requests = response.incomingFriendRequests
            }
            self.loading = false
        }
    }
}
