import MapKit
import SwiftUI

struct ContentView: View {
    @ObservedObject var viewModel: MainViewModel

    var body: some View {
        TabView {
            Text("Cheers 🍻")
                .tabItem {
                    Image(systemName: "cup.and.saucer.fill")
                    Text("Cheers")
                }
            CheersMap()
                .tabItem {
                    Image(systemName: "map.fill")
                    Text("Map")
                }
            Text("List of all the events!")
                .tabItem {
                    Image(systemName: "calendar")
                    Text("Events")
                }
            FriendsView()
                .tabItem {
                    Image(systemName: "person.2.fill")
                    Text("Friends")
                }
            Text("You are \(viewModel.username)!")
                .tabItem {
                    Image(systemName: "person.crop.circle")
                    Text(viewModel.username)
                }
        }
    }
}

private struct CheersMap: View {
    @State private var region = MKCoordinateRegion(center: CLLocationCoordinate2D(latitude: 57.708870, longitude: 11.974560), span: MKCoordinateSpan(latitudeDelta: 0.1, longitudeDelta: 0.1))

    var body: some View {
        Map(coordinateRegion: $region)
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView(viewModel: MainViewModel())
    }
}
