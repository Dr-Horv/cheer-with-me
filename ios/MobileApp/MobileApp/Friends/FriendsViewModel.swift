import Foundation


struct Friend: Identifiable {
    let id: Int
    let name: String
    let avatarUrl: String
}

struct FriendsViewModel {
    let waitingFriends: [Friend] = [
        Friend(id: 3, name: "Horvrino", avatarUrl: "https://randomuser.me/api/portraits/men/56.jpg"),
        Friend(id: 4, name: "Tejperino", avatarUrl: "https://randomuser.me/api/portraits/men/74.jpg"),
    ]
    
    let friends: [Friend] = [
        Friend(id: 1, name: "Malmerino", avatarUrl: "https://randomuser.me/api/portraits/men/25.jpg"),
        Friend(id: 2, name: "Ndushierino", avatarUrl: "https://randomuser.me/api/portraits/men/90.jpg")
    ]
}
