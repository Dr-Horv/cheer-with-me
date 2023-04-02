import Foundation
import MapKit

extension CLLocationCoordinate2D: Equatable {
    public static func == (lhs: CLLocationCoordinate2D, rhs: CLLocationCoordinate2D) -> Bool {
        return lhs.longitude == rhs.longitude && lhs.latitude == rhs.latitude
    }
}

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

struct InviteToEventInput: Codable {
    let happeningId: String
    let usersToInvite: [UserId]
}

typealias UserId = Int64

struct HappeningInput: Codable {
    let name: String
    let description: String
    let time: Date
    let location: Location?
    let usersToInvite: [UserId]
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

struct User: Identifiable, Hashable, Codable {
    let id: UserId
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
    @Published var google: AuthProviderProtocol
    @Published var results: [MKMapItem] = []
    @Published var isSearching = false
    @Published var choosenLocation: MKMapItem?
    private var decoder = getDecoder()

    var locationName: String? {
        choosenLocation?.name ?? choosenLocation?.description
    }

    var coords: CLLocationCoordinate2D? {
        return choosenLocation?.placemark.location?.coordinate
    }

    init(authProvider: AuthProviderProtocol) {
        google = authProvider
    }

    @MainActor
    func getEvents() async {
        guard let token = google.token else {
            return
        }

        do {
            let url = URL(string: "\(BACKEND_URL)/happenings")!
            var request = URLRequest(url: url)
            request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
            request.setValue("application/json", forHTTPHeaderField: "Accept")

            let (data, _) = try await URLSession.shared.data(for: request)
            let response = try getDecoder().decode([Happening].self, from: data)

            self.happenings = response

        } catch {
            print("Error getEvents: \(error)")
        }
    }

    @MainActor
    func createEvent(input: HappeningInput) async {
        guard let token = google.token else {
            return
        }

        do {
            let url = URL(string: "\(BACKEND_URL)/happenings/createHappening")!
            var request = URLRequest(url: url)
            request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
            request.setValue("application/json", forHTTPHeaderField: "Accept")
            request.setValue("application/json", forHTTPHeaderField: "Content-Type")
            request.httpMethod = "POST"

            request.httpBody = try getEncoder().encode(input)

            let (data, _) = try await URLSession.shared.data(for: request)
            let response = try getDecoder().decode(Happening.self, from: data)

            self.happenings.append(response)
        } catch {
            print("Error createEvent: \(error)")
        }
    }

    @MainActor
    func search(for location: String) async {
        isSearching = true
        let request = MKLocalSearch.Request()
        request.naturalLanguageQuery = location
        let search = MKLocalSearch(request: request)

        if let response = try? await search.start() {
            self.results = response.mapItems
        }

        isSearching = false
    }
    
    func inviteToEvent(happening: Happening, users: [User]) async {
        guard let token = google.token else {
            return
        }
        
        let input = InviteToEventInput(happeningId: happening.id, usersToInvite: users.map { $0.id } )

        do {
            let url = URL(string: "\(BACKEND_URL)/happenings/inviteUsers")!
            var request = URLRequest(url: url)
            request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
            request.setValue("application/json", forHTTPHeaderField: "Accept")
            request.setValue("application/json", forHTTPHeaderField: "Content-Type")
            request.httpMethod = "PUT"

            request.httpBody = try getEncoder().encode(input)

            let (data, _) = try await URLSession.shared.data(for: request)
            let response = try getDecoder().decode(Happening.self, from: data)

            self.happenings.append(response)
        } catch {
            print("Error createEvent: \(error)")
        }
    }

    func select(result location: MKMapItem) {
        choosenLocation = location
        results = []
    }
}

private func getEncoder() -> JSONEncoder {
    let encoder = JSONEncoder()
    encoder.dateEncodingStrategy = .secondsSince1970
    return encoder
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
        let viewModel = EventsViewModel(authProvider: AuthProviderMock())
        viewModel.happenings = exampleHappenings()
        return viewModel
    }
}

private func exampleHappenings() -> [Happening] {
    let oneDay = TimeInterval(12*60*60)
    let codeParty = Happening(happeningId: "theCoding",
                              admin: .malt,
                              name: "Do the code",
                              description: "Code code, drink beer",
                              time: Date(timeIntervalSinceNow: TimeInterval(5*oneDay)),
                              location: nil,
                              attendees: [.malt, .horv],
                              awaiting: [],
                              cancelled: false)

    let beerParty = Happening(happeningId: "beerOClock",
                              admin: .horv,
                              name: "Drink the beer",
                              description: "Bring your own beer. Drink beer",
                              time: Date(timeIntervalSinceNow: TimeInterval(6*oneDay)),
                              location: Location(coordinate: .init(lat: 57.708870, lng: 11.974560)),
                              attendees: [.malt, .horv],
                              awaiting: [],
                              cancelled: false)
    return [codeParty, beerParty]
}
