//
//  MainView.swift
//  CheerWithMe
//
//  Created by Johan Lindskogen on 2019-06-27.
//  Copyright © 2019 Johan Lindskogen. All rights reserved.
//

import SwiftUI

struct MainView : View {
    var body: some View {
        NavigationView {
            CheersListView(cheers: allCheers)
        }
    }
}

#if DEBUG
struct MainView_Previews : PreviewProvider {
    static var previews: some View {
        MainView()
    }
}
#endif
