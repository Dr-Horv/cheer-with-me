//
//  HostingController.swift
//  CheerWithMeWatch WatchKit Extension
//
//  Created by Johan Lindskogen on 2019-06-12.
//  Copyright © 2019 Johan Lindskogen. All rights reserved.
//

import SwiftUI

class HostingController : WKHostingController<ContentView> {
    override var body: ContentView {
        return ContentView(cheers: allCheers)
    }
}
