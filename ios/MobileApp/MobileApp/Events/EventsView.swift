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
    @ObservedObject var viewModel = EventsViewModel()

    var body: some View {
        NavigationView {
            VStack {
                if viewModel.isLoading {
                    ProgressView()
                }
                List(viewModel.happenings) { h in
                    NavigationLink(destination: SingleEventView(event: h)) {
                        Text(h.name)
                    }
                }
            }
            .navigationTitle("Events")
            .onAppear {
                viewModel.getEvents()
            }
        }
    }
}

struct EventsView_Previews: PreviewProvider {
    static var previews: some View {
        EventsView(viewModel: .example)
    }
}

struct SingleEventView_Previews: PreviewProvider {
    static var previews: some View {
        SingleEventView(
            event: EventsViewModel.example.happenings.last!
        )
    }
}
