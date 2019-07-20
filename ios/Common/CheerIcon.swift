//
//  CheerIcon.swift
//  CheerWithMeWatch WatchKit Extension
//
//  Created by Johan Lindskogen on 2019-06-19.
//  Copyright © 2019 Johan Lindskogen. All rights reserved.
//

import SwiftUI

enum IconSize {
    case large
    case small
    
    var fontSize : CGFloat {
        switch self {
        case .large:
            return 50
        case .small:
            return 22
        }
    }
    
    var outterSize : CGFloat {
        switch self {
        case .large:
            return 100
        case .small:
            return 40
        }
    }
    
    var beerOffset : CGFloat {
        switch self {
        case .large:
            return 3
        case .small:
            return 2
        }
    }
}

struct CheerIcon : View {
    var cheer: Cheer
    var size: IconSize
    var active: Bool = true
    
    var body: some View {
        ZStack {
            Rectangle()
                .foregroundColor(cheer.type.color.opacity(0.5))
                .frame(width: size.outterSize, height: size.outterSize)
            Rectangle()
                .foregroundColor(cheer.type.color)
                .frame(width: size.outterSize, height: size.outterSize)
                .offset(x: 0, y: active ? 0 : size.outterSize)
        Text(cheer.type.imageName).font(.custom("FontAwesome5Pro-Regular", size: size.fontSize))
                .foregroundColor(.black)
                .offset(x: cheer.type == .beer ? size.beerOffset : 0, y: 0)
                .frame(width: size.outterSize, height: size.outterSize)
        }.clipShape(Circle())
    }
}


#if DEBUG
struct CheerIcon_Previews : PreviewProvider {
    static var previews: some View {
        Group {
            CheerIcon(cheer: Cheer(type: .beer), size: .small)
            CheerIcon(cheer: Cheer(type: .beer), size: .large)
            
            CheerIcon(cheer: Cheer(type: .wine), size: .small)
            CheerIcon(cheer: Cheer(type: .wine), size: .large)
        }
        
    }
}
#endif
