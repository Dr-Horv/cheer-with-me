import SwiftUI
import Alamofire
import MapKit


struct SingleEventMapView: View {
    let coordinate: CLLocationCoordinate2D

    
    func markers() -> [EventMarker] {
        return [
            EventMarker(coordinate: coordinate)
        ]
    }
    
    @State private var region = MKCoordinateRegion(center: CLLocationCoordinate2D(), span: MKCoordinateSpan(latitudeDelta: 0.1, longitudeDelta: 0.1))
    
    var body: some View {
        Map(coordinateRegion: $region, interactionModes: [], annotationItems: self.markers()) { item in
            MapMarker(coordinate: item.coordinate, tint: .blue)
        }
        .onAppear {
            region.center = coordinate
        }
    }
}

struct EventMarker: Identifiable {
    let id = UUID()
    let coordinate: CLLocationCoordinate2D
}


struct SingleEventView: View {
    let event: Happening
    
    func coord() -> CLLocationCoordinate2D? {
        if let c = event.location?.coordinate {
            return CLLocationCoordinate2D(latitude: c.lat, longitude: c.lng)
        }
        return nil
    }
    
    var body: some View {
        VStack {
            Text(event.description)
            Text(event.time, format: .dateTime)
            Text(event.admin.nick)
            if let coord = self.coord() {
                SingleEventMapView(coordinate: coord)
                    .frame(width: 100.0, height: 100.0, alignment: .center)
            }
        }.navigationTitle(event.name)
    }
}

enum DateError: String, Error {
    case invalidDate
}


struct EventsView: View {
    @State var isLoading = false
    @State var happenings: [Happening] = []
    
    var body: some View {
        NavigationView {
            VStack {
                if isLoading {
                    ProgressView()
                }
                List(happenings) { h in
                    NavigationLink(destination: SingleEventView(event: h)) {
                        Text(h.name)
                    }
                }
            }.navigationTitle("Events")
                .onAppear {
                    isLoading = true

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
                    
                    AF.request("\(BACKEND_URL)/happenings", headers: SingletonState.shared.authHeaders()).responseDecodable(of: [Happening].self, decoder: decoder) {
                        response in
                        
                        debugPrint(response)
                        
                        isLoading = false
                        
                        if let data = response.value {
                            happenings = data
                            debugPrint(9)
                        }
                    }
                }
        }
    }
}

struct EventsView_Previews: PreviewProvider {
    static var previews: some View {
        EventsView()
    }
}
