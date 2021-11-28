//
//  UserPlaceholder.swift
//  CheerWithMe
//
//  Created by Johan Lindskogen on 2020-08-02.
//  Copyright © 2020 Johan Lindskogen. All rights reserved.
//

import Foundation
import SwiftUI

struct UserInitialsPlaceholder: View {
    let initials: String
    var body: some View {
        Circle()
            .foregroundColor(.hex(0xf6be27))
            .frame(width: 44, height: 44)
            .overlay(
                Text(initials.uppercased())
                    .font(.title3)
                    .bold()
            )
            .foregroundColor(.primary)
            
    }
}

struct UserPlaceholder_Previews: PreviewProvider {
    static var previews: some View {
        
        VStack {
            UserInitialsPlaceholder(initials: "JL")
            UserInitialsPlaceholder(initials: "H")
            UserInitialsPlaceholder(initials: "N")
            UserInitialsPlaceholder(initials: "JH")
        }
        
    }
}
