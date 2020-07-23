//
//  MapView.swift
//  CheerWithMe
//
//  Created by Johan Lindskogen on 2019-07-31.
//  Copyright © 2019 Johan Lindskogen. All rights reserved.
//

import MapKit
import SwiftUI

let coordinate = CLLocationCoordinate2D(latitude: 57.7089, longitude: 11.9746)
let span = MKCoordinateSpan(latitudeDelta: 0.2, longitudeDelta: 0.2)

struct MapView: View {
    @State private var region = MKCoordinateRegion(center: coordinate, span: span)
    
    var body: some View {
        Map(coordinateRegion: $region)
    }
}

#if DEBUG
struct MapView_Previews: PreviewProvider {
    static var previews: some View {
        MapView()
    }
}
#endif
