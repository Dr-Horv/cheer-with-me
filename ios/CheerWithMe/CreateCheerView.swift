//
//  CreateCheerView.swift
//  CheerWithMe
//
//  Created by Johan Lindskogen on 2019-07-23.
//  Copyright © 2019 Johan Lindskogen. All rights reserved.
//

import SwiftUI

struct CheerSwitchButton: View {
    let icon: FontIcon
    let label: String
    
    var body: some View {
        VStack(alignment: .center, spacing: 20) {
            FontAwesomeIcon(icon: icon, size: 20)
                .background(
                    Circle().stroke().frame(width: 44, height: 44)
            )
            Text(label).font(.system(size: 13))
        }.frame(width: 70)
    }
}

struct LowPrioritySpacer: View {
    var body: some View {
        Spacer().layoutPriority(-1)
    }
}

struct CreateCheerView: View {
    @State var cheerType: Cheer.CheerType = Cheer.CheerType.beer
    
    var body: some View {
        VStack {
            LowPrioritySpacer()
            
            VStack {
                Button(action: { print(self.cheerType.imageName) }) {
                    ZStack {
                        Circle().fill(self.cheerType.color)
                            .frame(height: 200)
                        FontAwesomeIcon(icon: self.cheerType.imageName, size: 50)
                    }
                }
            }
            
            LowPrioritySpacer()
            
            VStack {
                HStack {
                    LowPrioritySpacer()
                    Button(action: { self.cheerType = .beer  }) {
                        CheerSwitchButton(icon: .beer, label: "Beer")
                    }
                    LowPrioritySpacer()
                    Button(action: { self.cheerType = .whiskey }) {
                        CheerSwitchButton(icon: .glassWhiskey, label: "Whiskey")
                    }
                    LowPrioritySpacer()
                    Button(action: { self.cheerType = .coffee }) {
                        CheerSwitchButton(icon: .coffee, label: "Coffee")
                    }
                    LowPrioritySpacer()
                }.padding(.top, 20)
                HStack {
                    LowPrioritySpacer()
                    Button(action: { self.cheerType = .wine  }) {
                        CheerSwitchButton(icon: .wineGlassAlt, label: "Wine")
                    }
                    LowPrioritySpacer()
                    Button(action: { self.cheerType = .cocktail }) {
                        CheerSwitchButton(icon: .cocktail, label: "Cocktail")
                    }
                    LowPrioritySpacer()
                    Button(action: { self.cheerType = .wineBottle }) {
                        CheerSwitchButton(icon: .wineBottle, label: "Wine bottle")
                    }
                    LowPrioritySpacer()
                }.padding(.vertical, 20)
            }
        }
    }
}

#if DEBUG
struct CreateCheerView_Previews: PreviewProvider {
    static var previews: some View {
        Group {
            CreateCheerView()
            CreateCheerView().environment(\.colorScheme, .dark).background(Color.black).edgesIgnoringSafeArea(.all)
        }
    }
}
#endif


