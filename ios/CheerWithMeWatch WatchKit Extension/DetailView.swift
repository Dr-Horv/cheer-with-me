//
//  DetailView.swift
//  CheerWithMeWatch WatchKit Extension
//
//  Created by Johan Lindskogen on 2019-06-20.
//  Copyright © 2019 Johan Lindskogen. All rights reserved.
//

import SwiftUI
import Combine

struct DetailView: View {
    var cheer: Cheer
    
    @State private var result: CheerInput?
    @State private var positionManager: PositionManager = PositionManager()
    @State private var isActive = false
    
    var positionText: String {
        if let coord = positionManager.position?.coordinate {
            return "\(coord.latitude), \(coord.longitude)"
        } else {
            return "No GPS lock"
        }
    }
    
    var body: some View {
        VStack {
            Button(action: {
                
                // self.positionManager.start()
                BackendService().postCheer(CheerInput(lat: 30, lng: 10, type: cheer.type.rawValue))
                    .assign(to: \.result, on: self)
                
                withAnimation {
                    self.isActive.toggle()
                }
            }) {
                CheerIcon(cheer: cheer, size: .large, active: isActive)
            }
            
            Text(self.positionText)
            
            if (self.result == nil) {
                ActivityIndicator(animating: true, style: .large)
            }
            if (self.result != nil) {
                Text("Success!\n\(self.result)").color(.green)
            }
        }
        .navigationBarTitle(Text(cheer.type.rawValue))
    }
}

#if DEBUG
struct DetailView_Previews : PreviewProvider {
    static var previews: some View {
        DetailView(cheer: allCheers[0])
    }
}
#endif
