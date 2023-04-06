import Foundation


class FriendsSearchViewModel: ObservableObject {
    @Published var isLoading: Bool = false
    @Published var query: String = ""
    
    @Published var results: [User] = []
    
    
    var parentViewModel: FriendsViewModel
    
    init(parentViewModel: FriendsViewModel) {
        self.parentViewModel = parentViewModel
    }

    func searchFriends() async {
        
        guard let headers = self.parentViewModel.authHeaders else {
            return
        }
        
        
        do {
        
            let query = query.trimmingCharacters(in: .whitespacesAndNewlines)
            
            let request = try URLRequest(url: "\(BACKEND_URL)/users/search?nick=\(query)", method: .get, headers: headers)
            
            let (data, _) = try await URLSession.shared.data(for: request)
            let response = try JSONDecoder().decode([User].self, from: data)
            
            
            DispatchQueue.main.async {
                self.results = response
            }
            
        } catch {
            print("Error searchFriends: \(error)")
        }
    }
}

extension FriendsSearchViewModel {
    static var example : FriendsSearchViewModel {
        let viewModel = FriendsSearchViewModel(parentViewModel: .example)
        return viewModel
    }
}
