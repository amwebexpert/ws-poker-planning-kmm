import SwiftUI
import shared

let isHttpApiCallResponseVisible: Bool = false
let isTextFieldStateDemoVisible: Bool = false
let platformAndDate = ExampleService().getPlatformAndDate()

struct ContentView: View {
    @ObservedObject private(set) var viewModel: ViewModel
    @State private var username: String = ""

	var body: some View {
        VStack  (alignment: .center, spacing: 16, content: {
            if (isTextFieldStateDemoVisible) {
                TextField("Username", text: $username).textFieldStyle(RoundedBorderTextFieldStyle())
                Text("Username: \(username)")
            }
            Text(platformAndDate)

            // async UI element(s)
            Text("Async Poker planning message:")
            Text(viewModel.incomingMessage)

            if (isHttpApiCallResponseVisible) {
                Text("Async api call response:")
                Text(viewModel.apiTextResponse)
            }
        }).padding(16)
    }
}

extension ContentView {
    class ViewModel: ObservableObject {
        @Published var apiTextResponse = "Loading..."
        @Published var incomingMessage = ""
        
        init() {
            ExampleService().apiCallTextResult { apiTextResponse, error in
                DispatchQueue.main.async {
                    if let apiTextResponse = apiTextResponse {
                        self.apiTextResponse = apiTextResponse
                    } else {
                        self.apiTextResponse = error?.localizedDescription ?? "error while calling the api"
                    }
                }
            }
            
            class WsTextMessageListenerImpl: WsTextMessageListener {
                var viewModel: ViewModel
                init(viewModel: ViewModel) {
                    self.viewModel = viewModel
                }

                func onConnectSuccess() {}
                func onClose() {}
                func onConnectFailed() {}
                func onMessage(text: String) {
                    viewModel.incomingMessage = text
                    print("iOS: received text: \(text)")
                }
            }

            WebSocketService().connect(
                hostname: "ws-poker-planning.herokuapp.com",
                roomUUID: "e78caaee-a1a2-4298-860d-81d7752226ae",
                listener: WsTextMessageListenerImpl(viewModel: self)
            ) {error in
                DispatchQueue.main.async {
                    print(error?.localizedDescription ?? "error while connecting")
                }
            }
        }
    }
}
