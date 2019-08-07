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
            ZStack {
                Circle().stroke().frame(width: 44, height: 44)
                FontAwesomeIcon(icon: icon, size: 20)
                
            }.frame(width: 20, height: 20)
            
            Text(label).font(.system(size: 13))
        }
    }
}

struct CreateCheerView: View {
    @State var icon = FontIcon.beer
    
    var body: some View {
        VStack {
            Spacer()
            
            VStack {
                Button(action: { print(self.icon) }) {
                    ZStack {
                        Circle().fill(Color.hex(0xf9ab02))
                        FontAwesomeIcon(icon: self.icon, size: 50)
                            .padding(80)
                    }.aspectRatio(1.0, contentMode: .fit)
                }
            }
            
            Spacer()
            
            VStack {
                HStack {
                    Spacer()
                    Button(action: { self.icon = .wineGlassAlt  }) {
                        CheerSwitchButton(icon: .wineGlassAlt, label: "Wine")
                    }
                    Spacer()
                    Button(action: { self.icon = .glassWhiskey }) {
                        CheerSwitchButton(icon: .glassWhiskey, label: "Whiskey")
                    }
                    Spacer()
                    Button(action: { self.icon = .coffee }) {
                        CheerSwitchButton(icon: .coffee, label: "Coffee")
                    }
                    Spacer()
                }.padding(.top, 20)
                HStack {
                    Spacer()
                    Button(action: { self.icon = .wineGlassAlt  }) {
                        CheerSwitchButton(icon: .wineGlassAlt, label: "Wine")
                    }
                    Spacer()
                    Button(action: { self.icon = .glassWhiskey }) {
                        CheerSwitchButton(icon: .glassWhiskey, label: "Whiskey")
                    }
                    Spacer()
                    Button(action: { self.icon = .coffee }) {
                        CheerSwitchButton(icon: .coffee, label: "Coffee")
                    }
                    Spacer()
                }.padding(.vertical, 20)
            }
        }
        
    }
}

#if DEBUG
struct CreateCheerView_Previews: PreviewProvider {
    static var previews: some View {
        CreateCheerView()
    }
}
#endif


