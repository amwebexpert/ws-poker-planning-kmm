import SwiftUI
import shared

struct ContentView: View {
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
        }).padding(16)
    }
}

struct ContentView_Previews: PreviewProvider {
	static var previews: some View {
		ContentView()
	}
}
