//
//  BackendServices.swift
//  CheerWithMeWatch
//
//  Created by Johan Lindskogen on 2019-06-20.
//  Copyright © 2019 Johan Lindskogen. All rights reserved.
//

import SwiftUI
import Combine
import KeychainSwift

let host = "http://192.168.1.71:8080"
// let host = "http://cheer-with-me.fredag.dev"

class RequestStatus<T>: ObservableObject {
    var willChange = PassthroughSubject<Void, Never>()
    
    var data: T? { didSet { willChange.send() }}
    
    var error: Error? = nil
    var loading: Bool = false
    
    func subscribe(publisher: AnyPublisher<T, Error>) {
        self.loading = true
        let _ = publisher.receive(on: RunLoop.main).sink(receiveCompletion: { completion in
            print("completion ", completion)
            self.loading = false
        }, receiveValue: { value in
            self.data = value
        })
    }
}

struct CheerInput: Codable, CustomStringConvertible {
    var description: String {
        "CheerInput(lat: \(lat), lng: \(lng), type: \(type))"
    }
    
    var lat: Double
    var lng: Double
    
    var type: String
}


class BackendService {
    private let session: URLSession
    private let encoder: JSONEncoder
    private let decoder: JSONDecoder
    private let keychain = KeychainSwift()
    
    private var _token: String? = nil
    
    var token: String? {
        set {
            _token = newValue
            if let value = newValue {
                keychain.set(value, forKey: "cheer-with-me-token")
            } else {
                keychain.delete("cheer-with-me-token")
            }
            
        }
        get {
            _token
        }
    }
    
    private init(session: URLSession = .shared, encoder: JSONEncoder = .init(), decoder: JSONDecoder = .init()) {
        self.session = session
        self.encoder = encoder
        self.decoder = decoder
        self._token = keychain.get("cheer-with-me-token")
    }
    
    static let shared: BackendService = .init()
}


extension BackendService {
    func postCheer(cheerInput: CheerInput, completion: @escaping () -> ()) {
        
        guard let inputData = try? encoder.encode(cheerInput) else {
            preconditionFailure("cheerInput cannot be serialized")
        }
        
        guard let token = self.token else {
            preconditionFailure("Token not set")
        }
        
        var request = URLRequest(url: URL(string: "\(host)/echo")!)
        
        request.httpMethod = "POST"
        request.addValue("application/json", forHTTPHeaderField: "content-type")
        request.addValue("Bearer \(token)", forHTTPHeaderField: "authorization")
        request.httpBody = inputData
        
        session.dataTask(with: request) { (data, response, error) in
            completion()
        }.resume()
    }
}

struct UserPayload: Codable {
    let nick: String
}

struct UserResponse: Decodable, Identifiable {
    let id: Int
    let nick: String
    let avatarUrl: String?
}

struct AuthenticationPayload: Codable {
    let code: String
    let nick: String
}

struct AuthenticationResponse: Decodable {
    let accessToken: String
}

extension BackendService {
    func register(payload: AuthenticationPayload, completion: @escaping (AuthenticationResponse) -> ()) {
        
        guard let inputData = try? encoder.encode(payload) else {
            preconditionFailure("Payload cannot be serialized")
        }
        
        guard let token = self.token else {
            preconditionFailure("Token not set")
        }
        
        var request = URLRequest(url: URL(string: "\(host)/login/apple")!)
        
        request.httpMethod = "POST"
        request.addValue("application/json", forHTTPHeaderField: "content-type")
        request.addValue("Bearer \(token)", forHTTPHeaderField: "authorization")
        request.httpBody = inputData
        
        session.dataTask(with: request) { (data, response, error) in
            guard let data = data else {
                print("Error no data")
                return
            }
            
            guard let authResponse = try? self.decoder.decode(AuthenticationResponse.self, from: data) else {
                print("HTTP Error", error as Any)
                return
            }
            completion(authResponse)
        }.resume()
    }
}

struct PostPushTokenPayload: Codable {
    let pushToken: String
    let platform: String
}

extension BackendService {
    func registerDevicePushToken(pushToken: String, completion: @escaping (_ success: Bool) -> ()) {
        let payload = PostPushTokenPayload(pushToken: pushToken, platform: "IOS")
        
        guard let token = self.token else {
            preconditionFailure("Token not set")
        }
        
        guard let inputData = try? encoder.encode(payload) else {
            preconditionFailure("Payload cannot be serialized")
        }
        
        var request = URLRequest(url: URL(string: "\(host)/push/register-device")!)
        
        request.httpMethod = "POST"
        request.addValue("application/json", forHTTPHeaderField: "content-type")
        request.addValue("Bearer \(token)", forHTTPHeaderField: "authorization")
        request.httpBody = inputData
        
        
        session.dataTask(with: request) { (data, response, error) in
            guard let response = response as? HTTPURLResponse else {
                completion(false)
                return
            }
            
            completion(response.status != nil && response.status == .noContent)
        }.resume()
    }
}

struct SafeResponse: Decodable {
    let secret: String
    let user: CLongLong
}


extension BackendService {
    func safe(completion: @escaping (SafeResponse) -> ()) {
        guard let token = self.token else {
            preconditionFailure("Token not set")
        }
        
        var request = URLRequest(url: URL(string: "\(host)/safe")!)
        request.addValue("Bearer \(token)", forHTTPHeaderField: "authorization")
        
        session.dataTask(with: request) { (data, response, error) in
            guard let data = data else {
                print("Error no data")
                return
            }
            
            guard let authResponse = try? self.decoder.decode(SafeResponse.self, from: data) else {
                print("HTTP Error", error as Any)
                return
            }
            completion(authResponse)
        }.resume()
    }
}


extension BackendService {
    func searchUsers(_ input: String, completion: @escaping ([UserResponse]) -> Void) {
        guard let token = self.token else {
            preconditionFailure("Token not set")
        }
        
        var request = URLRequest(url: URL(string: "\(host)/users/search?nick=\(input)")!)
        request.addValue("Bearer \(token)", forHTTPHeaderField: "authorization")
        
        session.dataTask(with: request) { (data, response, error) in
            guard let data = data else {
                print("Error no data")
                return
            }
            
            guard let users = try? self.decoder.decode([UserResponse].self, from: data) else {
                print("HTTP Error", error as Any)
                return
            }
            completion(users)
        }.resume()
    }
}


struct FriendRequestResponse: Decodable {
    let friends: [UserResponse]
    let incomingFriendRequests: [UserResponse]
    let outgoingFriendRequests: [UserResponse]
}

// GET  /friends
// POST /friends/sendFriendRequest { userId: 132 }
// POST /friends/acceptFriendRequest { userId: 132 }

extension BackendService {
    func getOutstandingFriendRequests(completion: @escaping (FriendRequestResponse) -> Void) {
        guard let token = self.token else {
            preconditionFailure("Token not set")
        }
        
        var request = URLRequest(url: URL(string: "\(host)/friends/")!)
        request.addValue("Bearer \(token)", forHTTPHeaderField: "authorization")
        
        session.dataTask(with: request) { (data, response, error) in
            guard let data = data else {
                print("Error no data")
                return
            }
            
            guard let friendsResponse = try? self.decoder.decode(FriendRequestResponse.self, from: data) else {
                print("HTTP Error", error as Any)
                return
            }
            completion(friendsResponse)
        }.resume()
    }
}

struct FriendRequestPayload: Codable {
    let userId: Int
}

extension BackendService {
    func acceptFriendRequest(userId: Int, completion: @escaping (Bool) -> Void) {
        guard let token = self.token else {
            preconditionFailure("Token not set")
        }
        
        guard let inputData = try? encoder.encode(FriendRequestPayload(userId: userId)) else {
            preconditionFailure("Payload cannot be serialized")
        }
        
        var request = URLRequest(url: URL(string: "\(host)/friends/acceptFriendRequest")!)
        request.httpMethod = "POST"
        request.addValue("application/json", forHTTPHeaderField: "content-type")
        request.addValue("Bearer \(token)", forHTTPHeaderField: "authorization")
        request.httpBody = inputData
        
        session.dataTask(with: request) { (data, response, error) in
            if error != nil {
                completion(false)
            }
            completion(true)
        }.resume()
    }
}


extension BackendService {
    func sendFriendRequest(userId: Int, completion: @escaping (Bool) -> Void) {
        guard let token = self.token else {
            preconditionFailure("Token not set")
        }
        
        guard let inputData = try? encoder.encode(FriendRequestPayload(userId: userId)) else {
            preconditionFailure("Payload cannot be serialized")
        }
        
        var request = URLRequest(url: URL(string: "\(host)/friends/sendFriendRequest")!)
        request.httpMethod = "POST"
        request.addValue("application/json", forHTTPHeaderField: "content-type")
        request.addValue("Bearer \(token)", forHTTPHeaderField: "authorization")
        request.httpBody = inputData
        
        session.dataTask(with: request) { (data, response, error) in
            if error != nil {
                completion(false)
            }
            completion(true)
        }.resume()
    }
}
