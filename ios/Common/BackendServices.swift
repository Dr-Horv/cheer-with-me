//
//  BackendServices.swift
//  CheerWithMeWatch
//
//  Created by Johan Lindskogen on 2019-06-20.
//  Copyright © 2019 Johan Lindskogen. All rights reserved.
//

import SwiftUI
import Combine

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
    
    var token: String? = nil
    
    private init(session: URLSession = .shared, encoder: JSONEncoder = .init(), decoder: JSONDecoder = .init()) {
        self.session = session
        self.encoder = encoder
        self.decoder = decoder
    }
    
    static let shared: BackendService = .init()
}


extension BackendService {
    func postCheer(cheerInput: CheerInput) -> AnyPublisher<CheerInput, Error> {
        
        guard let inputData = try? encoder.encode(cheerInput) else {
            preconditionFailure("cheerInput cannot be serialized")
        }
        
        guard let token = self.token else {
            preconditionFailure("Token not set")
        }
        
        var request = URLRequest(url: URL(string: "\(host)/echo")!)
        
        request.httpMethod = "POST"
        request.addValue("application/json", forHTTPHeaderField: "content-type")
        request.addValue("Nick \(token)", forHTTPHeaderField: "authorization")
        request.httpBody = inputData
            
        return session.dataTaskPublisher(for: request)
            .map { $0.data }
            .decode(type: CheerInput.self, decoder: decoder)
            .eraseToAnyPublisher()
    }
}

struct UserPayload: Codable {
    let nick: String
}

struct UserResponse: Decodable {
    let id: Int
    let nick: String
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
        
        URLSession.shared.dataTask(with: request) { (data, response, error) in
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
    func post(pushToken: String, withAuthentication token: String) -> AnyPublisher<PostPushTokenPayload, Error> {
        let payload = PostPushTokenPayload(pushToken: pushToken, platform: "APPLE")
        
        guard let inputData = try? encoder.encode(payload) else {
            preconditionFailure("Payload cannot be serialized")
        }
        
        var request = URLRequest(url: URL(string: "\(host)/push/register-device")!)
        
        request.httpMethod = "POST"
        request.addValue("application/json", forHTTPHeaderField: "content-type")
        request.addValue("Nick \(token)", forHTTPHeaderField: "authorization")
        request.httpBody = inputData
        
        return session.dataTaskPublisher(for: request)
            .map { $0.data }
            .decode(type: PostPushTokenPayload.self, decoder: decoder)
            .eraseToAnyPublisher()
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
        
        URLSession.shared.dataTask(with: request) { (data, response, error) in
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
