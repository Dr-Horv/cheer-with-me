//
//  PushNotificationManager.swift
//  CheerWithMe
//
//  Created by Johan Lindskogen on 2019-06-27.
//  Copyright © 2019 Johan Lindskogen. All rights reserved.
//

import SwiftUI
import Combine
import UserNotifications



class PushNotificationManager: NSObject, ObservableObject, UNUserNotificationCenterDelegate {
    var willChange = PassthroughSubject<Void, Never>()
    
    let manager = UNUserNotificationCenter.current()
    
    private override init() {
        super.init()
        self.manager.delegate = self
    }
    
    static var shared = PushNotificationManager.init()
    
    
    func requestPermissions() {
        manager.requestAuthorization(options: [.alert, .badge, .sound]) {
            [weak self] granted, error in
            guard granted else { return }
            self?.getSettings()
        }
    }
    
    private func getSettings() {
        manager.getNotificationSettings { settings in
            guard settings.authorizationStatus == .authorized else { return }
            
            DispatchQueue.main.async {
                UIApplication.shared.registerForRemoteNotifications()
            }
        }
    }
    
}
