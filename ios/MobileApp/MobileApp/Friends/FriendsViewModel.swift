import Alamofire
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
    @Published var isLoading: Bool = false
    @Published var waitingFriends: [User] = [
//        User(id: 3, nick: "Horvrino", avatarUrl: "https://randomuser.me/api/portraits/men/56.jpg"),
//        User(id: 4, nick: "Tejperino", avatarUrl: "https://randomuser.me/api/portraits/men/74.jpg"),
    ]
    
    @Published var friends: [User] = [
//        User(id: 1, nick: "Malmerino", avatarUrl: "https://randomuser.me/api/portraits/men/25.jpg"),
//        User(id: 2, nick: "Ndushierino", avatarUrl: "https://randomuser.me/api/portraits/men/90.jpg")
    ]

    func getFriends() {
        isLoading = true

        AF.request("\(BACKEND_URL)/friends", headers: SingletonState.shared.authHeaders()).responseDecodable(of: FriendsResponse.self) { response in
            self.isLoading = false

            if let friendResponse = response.value {
                self.friends = friendResponse.friends
                self.waitingFriends = friendResponse.incomingFriendRequests
            }
        }
    }

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
