//
//  ContentView.swift
//  CheerWithMeWatch WatchKit Extension
//
//  Created by Johan Lindskogen on 2019-06-12.
//  Copyright © 2019 Johan Lindskogen. All rights reserved.
//

import SwiftUI

struct ContentView : View {
    var cheers: [Cheer]
    
    var body: some View {
        List {
            ForEach(cheers) { cheer in
                NavigationButton(destination: DetailView(cheer: cheer)) {
                    CheerIcon(cheer: cheer, size: .small)
                        .padding([.vertical, .trailing], 10)
                }
            }
        }
        .navigationBarTitle(Text("Cheer"))
    }
}

#if DEBUG
struct ContentView_Previews : PreviewProvider {
    static var previews: some View {
        ContentView(cheers: allCheers)
    }
}
#endif
