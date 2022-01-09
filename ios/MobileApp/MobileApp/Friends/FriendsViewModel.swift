import Foundation

extension User: Equatable {
    static func == (lhs: User, rhs: User) -> Bool {
        lhs.id == rhs.id
    }
}

extension User: Comparable {
    static func < (lhs: User, rhs: User) -> Bool {
        lhs.nick < rhs.nick
    }
}

class FriendsViewModel: ObservableObject {
    @Published var waitingFriends: [User] = [
//        User(id: 3, nick: "Horvrino", avatarUrl: "https://randomuser.me/api/portraits/men/56.jpg"),
//        User(id: 4, nick: "Tejperino", avatarUrl: "https://randomuser.me/api/portraits/men/74.jpg"),
    ]
    
    @Published var friends: [User] = [
//        User(id: 1, nick: "Malmerino", avatarUrl: "https://randomuser.me/api/portraits/men/25.jpg"),
//        User(id: 2, nick: "Ndushierino", avatarUrl: "https://randomuser.me/api/portraits/men/90.jpg")
    ]

    func befriend(person: User) {
        let isWaiting = waitingFriends.contains(person)

        guard isWaiting else { return }

        waitingFriends.removeAll(where: { dude in
            dude.id == person.id
        })

        friends.append(person)
        friends.sort()
    }

    func ignore(person: User) {
        let isWaiting = waitingFriends.contains(person)

        guard isWaiting else { return }

        waitingFriends.removeAll(where: { dude in
            dude.id == person.id
        })
    }
}
