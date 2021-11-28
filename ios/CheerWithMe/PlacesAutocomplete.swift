//
//  GooglePlacesAutocomplete.swift
//  CheerWithMe
//
//  Created by Johan Lindskogen on 2020-08-03.
//  Copyright © 2020 Johan Lindskogen. All rights reserved.
//

import Foundation
import SwiftUI
import UIKit
import GooglePlaces
import MapKit

struct AutocompleteResult: Identifiable {
    let id: String
    let name: String
    let coords: CLLocationCoordinate2D
}


struct PlacesAutocomplete: UIViewControllerRepresentable {
    let onClose: (AutocompleteResult?) -> Void
    
    func makeCoordinator() -> Coordinator {
        Coordinator(onClose: onClose)
    }
    
    func makeUIViewController(context: Context) -> some UIViewController {
        let viewController = GMSAutocompleteViewController()
        viewController.delegate = context.coordinator
        viewController.tableCellBackgroundColor = UIColor.darkGray
        
        let fields = GMSPlaceField(rawValue: UInt(GMSPlaceField.name.rawValue) | UInt(GMSPlaceField.placeID.rawValue) | UInt(GMSPlaceField.coordinate.rawValue))!
        
        viewController.placeFields = fields
        
        return viewController
    }
    func updateUIViewController(_ uiViewController: UIViewControllerType, context: Context) {
        
    }
    
    class Coordinator: NSObject, GMSAutocompleteViewControllerDelegate {
        let onClose: (AutocompleteResult?) -> Void
        
        init(onClose: @escaping (AutocompleteResult?) -> Void) {
            self.onClose = onClose
        }
        
        func viewController(_ viewController: GMSAutocompleteViewController, didAutocompleteWith place: GMSPlace) {
            print("Place name: \(String(describing: place.name))")
            print("Place ID: \(String(describing: place.placeID))")
            print("Place attributions: \(String(describing: place.coordinate))")
            
            if  let placeId = place.placeID,
                let placeName = place.name {
                onClose(AutocompleteResult(id: placeId, name: placeName, coords: place.coordinate))
            } else {
                onClose(nil)
            }
            
        }
        
        func viewController(_ viewController: GMSAutocompleteViewController, didFailAutocompleteWithError error: Error) {
            print("Error: ", error.localizedDescription)
            onClose(nil)
            
        }
        
        func wasCancelled(_ viewController: GMSAutocompleteViewController) {
            // Close modal
            onClose(nil)
        }
        
    }
}
