//
//  SearchBar.swift
//  CheerWithMe
//
//  Created by Johan Lindskogen on 2020-07-23.
//  Copyright © 2020 Johan Lindskogen. All rights reserved.
//

import Foundation
import UIKit
import SwiftUI

struct SearchBar: UIViewRepresentable {

    @Binding var text: String
    var placeholder: String
    let onPressSearch: (String) -> Void

    class Coordinator: NSObject, UISearchBarDelegate {

        @Binding var text: String
        let onPressSearch: (String) -> Void

        init(text: Binding<String>, onPressSearch: @escaping (String) -> Void) {
            _text = text
            self.onPressSearch = onPressSearch
        }

        func searchBar(_ searchBar: UISearchBar, textDidChange searchText: String) {
            text = searchText
        }
        func searchBarSearchButtonClicked(_ searchBar: UISearchBar) {
            onPressSearch(text)
        }
    }

    func makeCoordinator() -> SearchBar.Coordinator {
        return Coordinator(text: $text, onPressSearch: onPressSearch)
    }

    func makeUIView(context: UIViewRepresentableContext<SearchBar>) -> UISearchBar {
        let searchBar = UISearchBar(frame: .zero)
        searchBar.delegate = context.coordinator
        searchBar.placeholder = placeholder
        searchBar.searchBarStyle = .minimal
        searchBar.autocapitalizationType = .none
        return searchBar
    }

    func updateUIView(_ uiView: UISearchBar, context: UIViewRepresentableContext<SearchBar>) {
        uiView.text = text
    }
}
