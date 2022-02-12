import Alamofire
import Foundation
import MapKit

struct Coordinate: Codable {
    let lat: Double
    let lng: Double
}

struct Location: Codable {
    let coordinate: Coordinate

    func coord() -> CLLocationCoordinate2D {
        return CLLocationCoordinate2D(latitude: coordinate.lat,
                                      longitude: coordinate.lng)
    }
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

class EventsViewModel: ObservableObject {
    @Published var isLoading = false
    @Published var happenings: [Happening] = []
    private var decoder = getDecoder()

    func getEvents() {
        isLoading = true

        AF.request("\(BACKEND_URL)/happenings", headers: SingletonState.shared.authHeaders()).responseDecodable(of: [Happening].self, decoder: decoder) {
            response in

            debugPrint(response)

            self.isLoading = false

            if let data = response.value {
                self.happenings = data
                debugPrint(9)
            }
        }
    }
}

private func getDecoder() -> JSONDecoder {
    let formatter = DateFormatter()
    formatter.calendar = Calendar(identifier: .iso8601)
    formatter.locale = Locale(identifier: "en_US_POSIX")
    formatter.timeZone = TimeZone(secondsFromGMT: 0)

    let decoder = JSONDecoder()

    decoder.dateDecodingStrategy = .custom {
        (decoder) -> Date in
            let container = try decoder.singleValueContainer()
            let dateStr = try container.decode(String.self)

            formatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSXXXXX"
            if let date = formatter.date(from: dateStr) {
                return date
            }
            formatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ssXXXXX"
            if let date = formatter.date(from: dateStr) {
                return date
            }
            throw DateError.invalidDate
    }

    return decoder
}

extension EventsViewModel {
    static var example : EventsViewModel {
        let viewModel = EventsViewModel()
        viewModel.happenings = exampleHappenings()
        return viewModel
    }
}

private func exampleHappenings() -> [Happening] {
    let malt = User(id: 5, nick: "Malt", avatarUrl: randomProfileImage())
    let horv = User(id: 6,
                    nick: "Alkohorv",
                    avatarUrl: randomProfileImage(gender: .lego))
    let oneDay = TimeInterval(12*60*60)
    let codeParty = Happening(happeningId: "theCoding",
                              admin: malt,
                              name: "Do the code",
                              description: "Code code, drink beer",
                              time: Date(timeIntervalSinceNow: TimeInterval(5*oneDay)),
                              location: nil,
                              attendees: [malt, horv],
                              awaiting: [],
                              cancelled: false)

    let beerParty = Happening(happeningId: "beerOClock",
                              admin: horv,
                              name: "Drink the beer",
                              description: "Bring your own beer. Drink beer",
                              time: Date(timeIntervalSinceNow: TimeInterval(6*oneDay)),
                              location: Location(coordinate: .init(lat: 57.708870, lng: 11.974560)),
                              attendees: [malt, horv],
                              awaiting: [],
                              cancelled: false)
    return [codeParty, beerParty]
}

private enum Gender : String {
    case male = "men"
    case female = "women"
    case lego = "lego"
}

private func randomProfileImage(gender: Gender = .male) -> String {
    let max = gender == .lego ? 8 : 100
    let number = Int.random(in: 1..<max)
    return "https://randomuser.me/api/portraits/\(gender.rawValue)/\(number).jpg"
}