import SwiftUI

struct CheerView: View {
    var body: some View {
        VStack {
            Spacer()
            ZStack {
                Circle()
                    .fill(.orange)
                    .frame(width: 200, height: 200)
                    .onTapGesture {
                        print("CLICK 2")
                    }
                Text("🍺")
                    .font(.largeTitle)
                    ._colorMonochrome(.white)
            }
            BeverageGroup()
        }
    }
}

private struct BeverageGroup: View {
    var rows: [GridItem] =
    [.init(.fixed(40), spacing: 40),
     .init(.fixed(40), spacing: 40)]

    var body: some View {
        LazyHGrid(rows: rows, alignment: .bottom, spacing: 80) {
            Circle().fill(Color.gray)
                .frame(width: 46, height: 46)
            Circle().fill(Color.gray)
                .frame(width: 46, height: 46)
            Circle().fill(Color.gray)
                .frame(width: 46, height: 46)
            Circle().fill(Color.gray)
                .frame(width: 46, height: 46)
            Circle().fill(Color.gray)
                .frame(width: 46, height: 46)
            Circle().fill(Color.gray)
                .frame(width: 46, height: 46)
        }
    }
}

struct CheerView_Previews: PreviewProvider {
    static var previews: some View {
        CheerView()
    }
}
