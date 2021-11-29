import SwiftUI
import Alamofire




struct EventsView: View {
    @State var isLoading = false
    @State var happenings: [Happening] = []
    
    var body: some View {
        VStack {
            if isLoading {
                ProgressView()
            }
            List(happenings) { h in
                Text(h.name)
            }
        }.onAppear {
            isLoading = true
            let headers: HTTPHeaders = [
                "Authorization": "Bearer \(SingletonState.shared.token ?? "")",
                "Accept": "application/json"
            ]
            
            AF.request("http://192.168.1.127:8080/friends", headers: headers).responseDecodable(of: FriendsResponse.self) {
                response in
                
                debugPrint(response)
            }
            
            AF.request("http://192.168.1.127:8080/happenings", headers: headers).responseDecodable(of: [Happening].self) {
                response in
                
                debugPrint(response)
                
                isLoading = false
                
                if let data = response.value {
                    happenings = data
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
