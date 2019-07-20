//
//  ActivityIndicator.swift
//  BusApp
//
//  Created by Johan Lindskogen on 2019-06-16.
//  Copyright © 2019 Johan Lindskogen. All rights reserved.
//

import SwiftUI
import UIKit

struct ActivityIndicator : UIViewRepresentable {
    var animating: Bool
    var hidesWhenStopped = true
    var style: UIActivityIndicatorView.Style = UIActivityIndicatorView.Style.medium
    
    func makeUIView(context: Context) -> UIActivityIndicatorView {
        let control = UIActivityIndicatorView()
        
        updateValues(for: control)
        
        return control
    }
    
    func updateValues(for uiView: UIActivityIndicatorView) {
        if uiView.style != style {
            uiView.style = style
        }
        
        if uiView.hidesWhenStopped != hidesWhenStopped {
            uiView.hidesWhenStopped = hidesWhenStopped
        }
        
        if animating != uiView.isAnimating {
            if animating {
                uiView.startAnimating()
            } else {
                uiView.stopAnimating()
            }
        }
    }
    
    func updateUIView(_ uiView: UIActivityIndicatorView, context: Context) {
        print("Update UI View")
        updateValues(for: uiView)
    }
}
