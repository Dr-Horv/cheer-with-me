//
//  CreateCheerView.swift
//  CheerWithMe
//
//  Created by Johan Lindskogen on 2019-07-23.
//  Copyright © 2019 Johan Lindskogen. All rights reserved.
//

import SwiftUI
import MapKit

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

struct StaticMap: View {
    let location: AutocompleteResult
    
    var body: some View {
        Map(coordinateRegion: .constant(
                MKCoordinateRegion(
                    center: location.coords,
                    span: MKCoordinateSpan(
                        latitudeDelta: 0.002,
                        longitudeDelta: 0.002))),
            annotationItems: [
                location
        ]) { pin in
            MapPin(coordinate: pin.coords)
        }
    }
}

struct CreateCheerView: View {
    let location: AutocompleteResult?
    @State private var showLocationSearch = false
    @State private var cheerType: Cheer.CheerType = Cheer.CheerType.beer
    
    var body: some View {
        if showLocationSearch {
            PlacesAutocomplete(onClose: { result in
                print(result as Any)
                self.showLocationSearch = false
            }).foregroundColor(.black)
        }
        VStack {
            LowPrioritySpacer()
            
            if let location = location {
                StaticMap(location: location).clipShape(
                    Circle()
                ).frame(height: 200)
                
            } else {
                Button(action: {
                        print(self.cheerType.imageName)
                        self.showLocationSearch = true
                }) {
                    Circle().fill(self.cheerType.color)
                        .frame(height: 200)
                        .overlay(FontAwesomeIcon(icon: self.cheerType.imageName, size: 50))
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
            CreateCheerView(location: nil)
            CreateCheerView(location: nil).environment(\.colorScheme, .dark).background(Color.black).edgesIgnoringSafeArea(.all)
            CreateCheerView(location: AutocompleteResult(id: "ChIJYxsoAwvzT0YRNReWHGqfBxc", name: "Hubben 2.1", coords: CLLocationCoordinate2D(latitude: 57.6882968, longitude: 11.9792283))).environment(\.colorScheme, .dark).background(Color.black).edgesIgnoringSafeArea(.all)
        }
    }
}
#endif


