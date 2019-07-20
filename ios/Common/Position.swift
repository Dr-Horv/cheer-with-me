//
//  Location.swift
//  CheerWithMeWatch WatchKit Extension
//
//  Created by Johan Lindskogen on 2019-06-20.
//  Copyright © 2019 Johan Lindskogen. All rights reserved.
//

import CoreLocation
import SwiftUI
import Combine

class PositionManager: NSObject, BindableObject, CLLocationManagerDelegate {
    var willChange = PassthroughSubject<Void, Never>()
    
    var locationManager: CLLocationManager
    var position: CLLocation?
    
    override init() {
        self.locationManager = CLLocationManager()
        super.init()
        self.locationManager.delegate = self
        self.locationManager.desiredAccuracy = kCLLocationAccuracyNearestTenMeters
    }
    
    func start() {
        self.locationManager.requestLocation()
    }
    
    
    func locationManager(_ manager: CLLocationManager, didChangeAuthorization status: CLAuthorizationStatus) {
        if status == .authorizedWhenInUse {
            manager.requestLocation()
        }
    }
    
    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        if let lastLocation = locations.last {
            self.position = lastLocation
            RunLoop.main.perform {
                self.willChange.send()
            }
        }
    }
    
    func locationManager(_ manager: CLLocationManager, didFailWithError error: Error) {
        print(error)
        self.locationManager.requestWhenInUseAuthorization()
    }
}
