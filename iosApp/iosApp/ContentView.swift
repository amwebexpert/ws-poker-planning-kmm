import SwiftUI
import shared

struct ContentView: View {
    @ObservedObject private(set) var viewModel: ViewModel
	let platformAndDate = ExampleService().getPlatformAndDate()
    @State private var username: String = ""

	var body: some View {
        VStack  (alignment: .center, spacing: 16, content: {
            TextField("Username", text: $username).textFieldStyle(RoundedBorderTextFieldStyle())

            if username.isEmpty {
                Text(platformAndDate)
            } else {
                Text("\(platformAndDate). Welcome \(username)!")
            }

            // async UI element(s)
            Text(viewModel.apiTextResponse)
        }).padding(16)
    }
}

extension ContentView {
    class ViewModel: ObservableObject {
        @Published var apiTextResponse = "Loading..."

        init() {
            ExampleService().apiCallTextResult { apiTextResponse, error in
                DispatchQueue.main.async {
                    if let apiTextResponse = apiTextResponse {
                        self.apiTextResponse = apiTextResponse
                    } else {
                        self.apiTextResponse = error?.localizedDescription ?? "error"
                    }
                }
            }
        }
    }
}
