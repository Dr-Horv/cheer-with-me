//
//  App.swift
//  CheerWithMe
//
//  Created by Johan Lindskogen on 2020-07-23.
//  Copyright © 2020 Johan Lindskogen. All rights reserved.
//

import SwiftUI

@main
struct ApplicationView: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var appDelegate
    
    
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
