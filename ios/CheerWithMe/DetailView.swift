//
//  DetailView.swift
//  CheerWithMe
//
//  Created by Johan Lindskogen on 2019-06-24.
//  Copyright © 2019 Johan Lindskogen. All rights reserved.
//

import SwiftUI
import Combine


struct DetailView: View {
    var cheer: Cheer
    
    @State private var isActive = false
    
    var body: some View {
        Button(action: {
            withAnimation {
                self.isActive.toggle()
            }
        }) {
            CheerIcon(cheer: cheer, size: .large, active: isActive)
        }
    }
}

#if DEBUG
struct DetailView_Previews : PreviewProvider {
    static var previews: some View {
        DetailView(cheer: allCheers[0])
    }
}
#endif
