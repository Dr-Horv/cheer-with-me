import Foundation


struct Happening: Identifiable, Codable {
    var id: String { self.happeningId }
    let happeningId: String
    let name: String
    let description: String
}

struct User: Decodable {
    let id: Int64
    let nick: String
    let avatarUrl: String?
}


struct FriendsResponse: Decodable {
    let friends: [User]
    let incomingFriendRequests: [User]
    let outgoingFriendRequests: [User]
}
