import Foundation



struct Coordinate: Codable {
    let lat: Double
    let lng: Double
}

struct Location: Codable {
    let coordinate: Coordinate
}

struct Happening: Identifiable, Codable {
    var id: String { self.happeningId }
    let happeningId: String
    let admin: User
    let name: String
    let description: String
    let time: Date
    let location: Location?
    let attendees: [User]
    let awaiting: [User]
    let cancelled: Bool
}

struct User: Identifiable, Codable {
    let id: Int64
    let nick: String
    let avatarUrl: String?
}


struct FriendsResponse: Codable {
    let friends: [User]
    let incomingFriendRequests: [User]
    let outgoingFriendRequests: [User]
}
