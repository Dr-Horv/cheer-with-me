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
            Text("This is map!")
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

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView(viewModel: MainViewModel())
    }
}
