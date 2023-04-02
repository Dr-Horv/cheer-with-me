import SwiftUI
import MapKit
import URLImage

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
        }.onChange(of: coordinate, perform: {
            region.center = $0
        })
    }
}

struct EventMarker: Identifiable {
    let id = UUID()
    let coordinate: CLLocationCoordinate2D
}

struct SingleEventView: View {
    let event: Happening

    var body: some View {
        VStack {
            HStack {
                if let image = event.admin.avatarUrl {
                    AsyncImage(url: URL(string: image)) { image in
                        image
                            .resizable()
                            .scaledToFill()
                    } placeholder: {
                        ProgressView()
                    }
                    .frame(width: 100, height: 100)
                    .clipShape(Circle())
                }
                VStack(alignment: .leading) {
                    Text(event.admin.nick).bold()
                    Text(event.description)
                    Text(event.time, format: .dateTime)
                }
            }
            if let coord = event.location?.coord() {
                SingleEventMapView(coordinate: coord)
            }
        }.navigationTitle(event.name)
    }
}

enum DateError: String, Error {
    case invalidDate
}

struct EventsView: View {
    @ObservedObject var viewModel: EventsViewModel
    @State var isPresented: Bool = false

    var body: some View {
        NavigationView {
            VStack {
                if viewModel.isLoading {
                    ProgressView()
                }
                List {
                    ForEach(viewModel.happenings) { h in
                        NavigationLink(destination: SingleEventView(event: h)) {
                            Text(h.name)
                        }
                    }
                    Button(action: { self.isPresented = true }) {
                        HStack {
                            Image(systemName: "plus")
                            Text("Create event")
                        }.foregroundColor(.accentColor)
                    }
                }
                .refreshable {
                    await viewModel.getEvents()
                }
                .navigationTitle("Events")
                .task {
                    viewModel.isLoading = true
                    await viewModel.getEvents()
                    viewModel.isLoading = false
                }
                .sheet(isPresented: $isPresented, onDismiss: { self.isPresented = false }) {
                    CreateEventView(viewModel: viewModel, onSuccess: {
                        self.isPresented = false
                    })
                }
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
