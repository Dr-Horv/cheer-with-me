import SwiftUI
import MapKit

struct CreateEventView: View {
    @ObservedObject var viewModel: EventsViewModel

    @State var title = ""
    @State var description = ""
    @State var date = Date()
    @State var locationQuery = ""
    let onSuccess: () -> Void

    var body: some View {
        Form {
            TextField("Title", text: $title)
            TextField("Description", text: $description)
            DatePicker("When", selection: $date)

            Section("Where") {
                TextField("Search for location...", text: $locationQuery)
                    .onSubmit {
                        Task {
                            await viewModel.search(for: locationQuery)
                        }
                    }
            }

            Section("Results") {
                if viewModel.isSearching {
                    ProgressView().progressViewStyle(CircularProgressViewStyle())
                } else {
                    ForEach(viewModel.results, id: \.hash) { place in
                        Button(action: {
                            locationQuery = ""
                            viewModel.select(result: place)
                        }) {
                            Text(place.name ?? place.description)
                        }
                    }
                }
            }
            if let c = viewModel.coords {
                SingleEventMapView(coordinate: c).frame(height: 200)
            }
        }

        Button("Save") {
            var location: Location? = nil
            
            if let c = viewModel.coords {
                location = Location(coordinate: Coordinate(lat: c.latitude, lng: c.longitude))
            }

            let input = HappeningInput(name: title, description: description, time: date, location: location, usersToInvite: [])

            Task {
                await viewModel.createEvent(input: input)
                self.onSuccess()
            }
        }
    }
}

struct CreateEventView_Previews: PreviewProvider {
    static var previews: some View {
        CreateEventView(viewModel: .example, onSuccess: { })
    }
}
