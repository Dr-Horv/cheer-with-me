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
    
    @State private var positionManager: PositionManager = PositionManager()
    @State private var isActive = false
    
    @State private var requestStatus: RequestStatus<CheerInput> = .init()
    
    var positionText: String {
        if let coord = positionManager.position?.coordinate {
            return "\(coord.latitude), \(coord.longitude)"
        } else {
            return "No GPS lock"
        }
    }
    
    var dataString: some View {
        if let cheerinput = self.requestStatus.data {
            return AnyView(
                Text("Success!\n\(cheerinput.description)").foregroundColor(.green).lineLimit(2).multilineTextAlignment(.center)
            )
        } else if self.requestStatus.loading {
            return AnyView(
                ActivityIndicator(animating: true, style: .large)
            )
        } else {
            return AnyView(
                EmptyView()
            )
        }
    }
    
    var body: some View {
        VStack {
            Button(action: {
                
                self.positionManager.start()
                self.requestStatus.subscribe(publisher: BackendService.shared.postCheer(cheerInput: CheerInput(lat: 30.4, lng: 10.4, type: self.cheer.type.rawValue)))
                
                withAnimation {
                    self.isActive.toggle()
                }
            }) {
                CheerIcon(cheer: cheer, size: .large, active: isActive)
            }
            
            Text(self.positionText)
            
            dataString
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
