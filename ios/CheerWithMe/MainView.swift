//
//  MainView.swift
//  CheerWithMe
//
//  Created by Johan Lindskogen on 2019-06-27.
//  Copyright © 2019 Johan Lindskogen. All rights reserved.
//

import SwiftUI

struct MainView : View {
    @State var selectedTab = Tab.cheer
    
    enum Tab: Int {
       case cheer, map, calendar, friends, me
    }
    
    func tabbarItem(icon: String, text: String) -> some View {
        VStack {
            Image(icon)
            Text(text)
        }
    }
    
    var body: some View {
        TabView(selection: $selectedTab) {
            CreateCheerView().tabItem {
                self.tabbarItem(icon: "beer", text: "Cheer")
            }.tag(Tab.cheer)
            MapView()
                .tabItem {
                    self.tabbarItem(icon: "map", text: "Map")
            }.tag(Tab.map)
            NavigationView {
                CheersListView(cheers: allCheers)
            }
            .tabItem {
                self.tabbarItem(icon: "calendar", text: "Events")
            }.tag(Tab.calendar)
            FriendsScreen()
               .tabItem {
                self.tabbarItem(icon: "user-friends", text: "Friends")
           }.tag(Tab.friends)
            DetailView(cheer: allCheers[3])
                .tabItem {
                    self.tabbarItem(icon: "user", text: "Me")
            }.tag(Tab.me)
        }.accentColor(.primary)
    }
}

#if DEBUG
struct MainView_Previews : PreviewProvider {
    static var previews: some View {
        MainView()
    }
}
#endif
