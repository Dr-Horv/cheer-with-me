import Foundation

struct Friend: Identifiable {
    let id: Int
    let name: String
    let avatarUrl: String
}

extension Friend: Equatable {
    static func == (lhs: Friend, rhs: Friend) -> Bool {
        lhs.id == rhs.id
    }
}

extension Friend: Comparable {
    static func < (lhs: Friend, rhs: Friend) -> Bool {
        lhs.name < rhs.name
    }
}

class FriendsViewModel: ObservableObject {
    @Published var waitingFriends: [Friend] = [
        Friend(id: 3, name: "Horvrino", avatarUrl: "https://randomuser.me/api/portraits/men/56.jpg"),
        Friend(id: 4, name: "Tejperino", avatarUrl: "https://randomuser.me/api/portraits/men/74.jpg"),
    ]
    
    @Published var friends: [Friend] = [
        Friend(id: 1, name: "Malmerino", avatarUrl: "https://randomuser.me/api/portraits/men/25.jpg"),
        Friend(id: 2, name: "Ndushierino", avatarUrl: "https://randomuser.me/api/portraits/men/90.jpg")
    ]

    public func befriend(person: Friend) {
        let isWaiting = waitingFriends.contains(person)

        guard isWaiting else { return }

        waitingFriends.removeAll(where: { dude in
            dude.id == person.id
        })

        friends.append(person)
        friends.sort()
    }
}
