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
    @EnvironmentObject var viewModel: EventsViewModel
    let event: Happening
    @State var isPresented: Bool = false

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
            
            NavigationLink(destination:
                List {
                    ForEach(event.attendees) { friend in
                        FriendItem(friend)
                    }
                }
            ) {
                Text("\(event.attendees.count) attending")
            }
            
            NavigationLink(destination:
                List {
                    ForEach(event.awaiting) { friend in
                        FriendItem(friend)
                    }
                }
            ) {
                Text("\(event.awaiting.count) invited")
            }
        }.navigationTitle(event.name)
        .toolbar {
               ToolbarItem(placement: .primaryAction) {
                   Button("Invite friends") {
                       self.isPresented = true
                   }
               }
           }
        .sheet(isPresented: $isPresented, onDismiss: { self.isPresented = false }) {
            SelectFriendsView(onSubmit: {
                friends in
                
                Task {
                    await viewModel.inviteToEvent(happening: event, users: friends)
                }
                
                
                
            })
        }
    }
}


struct SelectFriendsView: View {
    let onSubmit: (_ friends: [User]) -> ()
    
    @EnvironmentObject var viewModel: FriendsViewModel
    @State private var selection: Set<User> = []
    
    private func toggleSelection(selectable: User) {
          if let existingIndex = selection.firstIndex(where: { $0.id == selectable.id }) {
              selection.remove(at: existingIndex)
          } else {
              selection.insert(selectable)
          }
      }
    
    var body: some View {
        VStack{
            List {
                ForEach(viewModel.friends) { friend in
                       Button(action: { toggleSelection(selectable: friend) }) {
                           HStack {
                               FriendItem(friend)

                               Spacer()

                               if selection.contains { $0.id == friend.id } {
                                   Image(systemName: "checkmark").foregroundColor(.accentColor)
                               }
                           }
                       }
                   }
               }.listStyle(GroupedListStyle())
        }
        Button("Submit") {
            
            let users = Array(selection)
            
            self.onSubmit(users)
        }
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
            
        }.environmentObject(viewModel)
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
