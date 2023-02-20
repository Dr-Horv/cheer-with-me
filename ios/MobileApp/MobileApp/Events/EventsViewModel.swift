import Alamofire
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

struct User: Identifiable, Codable {
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
    private var decoder = getDecoder()

    var authHeaders: HTTPHeaders? {
        guard let token = google.token else {
            return nil
        }

        return HTTPHeaders([
            "Authorization": "Bearer \(token)",
            "Accept": "application/json",
            "Content-Type": "application/json",
        ])
    }

    init(authProvider: AuthProviderProtocol) {
        google = authProvider
    }

    func getEvents() async {
        guard let headers = authHeaders else {
            return
        }
        
        do {
            let request = try URLRequest(url: "\(BACKEND_URL)/happenings", method: .get, headers: headers)
            let (data, _) = try await URLSession.shared.data(for: request)
            
            let response = try getDecoder().decode([Happening].self, from: data)
            
            DispatchQueue.main.async {
                self.happenings = response
            }
            
        } catch {
            print("Error getEvents: \(error)")
        }
    }
    
    func createEvent(input: HappeningInput) async {
        guard let headers = authHeaders else {
            return
        }
        
        do {
            var request = try URLRequest(url: "\(BACKEND_URL)/happenings/createHappening", method: .post, headers: headers)
            
            request.httpBody = try getEncoder().encode(input)
            
            let (data, _) = try await URLSession.shared.data(for: request)
            let response = try getDecoder().decode(Happening.self, from: data)
            
            DispatchQueue.main.async {
                self.happenings.append(response)
            }
            
            
        } catch {
            print("Error createEvent: \(error)")
        }
    }

    @MainActor
    func search(for location: String) async {
        if location.isEmpty {
            return
        }

        let request = MKLocalSearch.Request()
        request.naturalLanguageQuery = location
        let search = MKLocalSearch(request: request)

        if let response = try? await search.start() {
            self.results = response.mapItems
        }
    }
}

private func getEncoder() -> JSONEncoder {
    let formatter = DateFormatter()
    formatter.calendar = Calendar(identifier: .iso8601)
    formatter.locale = Locale(identifier: "en_US_POSIX")
    formatter.timeZone = TimeZone(secondsFromGMT: 0)
    formatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS"
    
    let encoder = JSONEncoder()
    encoder.dateEncodingStrategy = .formatted(formatter)
        
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
