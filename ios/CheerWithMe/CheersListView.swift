//
//  CheersListView.swift
//  CheerWithMe
//
//  Created by Johan Lindskogen on 2019-06-24.
//  Copyright © 2019 Johan Lindskogen. All rights reserved.
//

import SwiftUI

struct CheersListView : View {
    var cheers: [Cheer]
    
    var body: some View {
        List {
            ForEach(cheers) { cheer in
                NavigationLink(destination: DetailView(cheer: cheer)) {
                    CheerIcon(cheer: cheer, size: .small, active: true)
                }
            }
        }
    }
}

#if DEBUG
struct CheersListView_Previews : PreviewProvider {
    static var previews: some View {
        CheersListView(cheers: allCheers)
    }
}
#endif
