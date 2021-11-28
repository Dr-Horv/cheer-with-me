import SwiftUI

struct NavigationView: View {
    var body: some View {
        HStack {
            Group {
                Text("A")
                Text("B")
                Text("C")
                Text("D")
                Text("E")
            }
            .frame(maxWidth: .infinity)
        }
    }
}

struct NavigationView_Previews: PreviewProvider {
    static var previews: some View {
        NavigationView()
    }
}
