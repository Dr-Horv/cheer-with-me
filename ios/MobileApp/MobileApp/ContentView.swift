import SwiftUI

struct ContentView: View {
    @ObservedObject var viewModel: MainViewModel

    var body: some View {
        VStack {
            Text("Hello, \(viewModel.username)")
                .padding()
            Spacer()
            NavigationView()
        }
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView(viewModel: MainViewModel())
    }
}
