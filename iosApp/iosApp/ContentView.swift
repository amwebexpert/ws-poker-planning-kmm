import SwiftUI
import shared

struct ContentView: View {
    @ObservedObject private(set) var viewModel: ViewModel
	let greet = Greeting().greeting()
    @State private var username: String = ""

	var body: some View {
        VStack  (alignment: .center, spacing: 16, content: {
            TextField("Username", text: $username).textFieldStyle(RoundedBorderTextFieldStyle())
            if !username.isEmpty { // <1>
                Text("\(greet). Welcome \(username)!")
            } else {
                Text(greet)
            }

            // async UI element(s)
            Text(viewModel.text)
        }).padding(16)
    }
}

extension ContentView {
    class ViewModel: ObservableObject {
        @Published var text = "Loading..."
        init() {
            Greeting().greetingRemote { greeting, error in
                DispatchQueue.main.async {
                    if let greeting = greeting {
                        self.text = greeting
                    } else {
                        self.text = error?.localizedDescription ?? "error"
                    }
                }
            }
        }
    }
}
