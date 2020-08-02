//
//  Cheer.swift
//  CheerWithMeWatch WatchKit Extension
//
//  Created by Johan Lindskogen on 2019-06-12.
//  Copyright © 2019 Johan Lindskogen. All rights reserved.
//

import SwiftUI

struct Cheer: Identifiable {
    var id = UUID()
    
    var type: CheerType
    
    enum CheerType: String, CaseIterable, Codable, Hashable {
        case beer = "beer"
        case wine = "wine"
        case coffee = "coffee"
        case whiskey = "whiskey"
        case cocktail = "cocktail"
        case wineBottle = "wineBottle"
        
        var color: Color {
            switch self {
            case .beer:
                return .init(.displayP3, red: 1.00, green: 0.78, blue: 0.00, opacity: 1.00)
            case .wine:
                return .init(.displayP3, red: 1.00, green: 0.41, blue: 0.51, opacity: 1.00)
            case .whiskey:
                return .init(.displayP3, red: 0.19, green: 0.56, blue: 0.91, opacity: 1.00)
            case .cocktail:
                return .init(.displayP3, red: 0.57, green: 0.86, blue: 0.32, opacity: 1.00)
            case .wineBottle:
                return .init(.displayP3, red: 0.73, green: 0.42, blue: 1.00, opacity: 1.00)
            case .coffee:
                return .init(.displayP3, red: 0.68, green: 0.50, blue: 0.36, opacity: 1.00)
            }
        }
        
        var imageName: FontIcon {
            switch self {
            case .beer:
                return .beer
            case .wine:
                return .wineGlassAlt
            case .coffee:
                return .coffee
            case .whiskey:
                return .glassWhiskey
            case .cocktail:
                return .cocktail
            case .wineBottle:
                return .wineBottle
            }
        }
    }
}


let allCheers = Cheer.CheerType.allCases.map { Cheer(type: $0) }



enum FontIcon: String {
    case beer = "\u{f0fc}"
    case wineGlassAlt = "\u{f5ce}"
    case coffee = "\u{f0f4}"
    case glassWhiskey = "\u{f7a0}"
    case cocktail = "\u{f561}"
    case wineBottle = "\u{f72f}"
    case map = "\u{f279}"
    case calendar = "\u{f133}"
    case userFriends = "\u{f500}"
    case user = "\u{f007}"
    case times = "\u{f00d}"
    case check = "\u{f00c}"
    case plus = "\u{f067}"
    case userPlus = "\u{f234}"
}
