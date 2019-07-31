//
//  UserStore.swift
//  CheerWithMe
//
//  Created by Johan Lindskogen on 2019-06-27.
//  Copyright © 2019 Johan Lindskogen. All rights reserved.
//

import Combine



class UserManager: ObservableObject {
    var willChange = PassthroughSubject<Void, Never>()
    
    static var shared = UserManager.init()
    
    var username: String? = nil
    var pushToken: String? = nil
    var authToken: String? = nil
    
    init() {}
    
}
