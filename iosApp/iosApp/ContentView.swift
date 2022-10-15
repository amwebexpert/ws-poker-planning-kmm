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
    class ViewModel: ObservableObject, WsTextMessageListener {
        @Published public var apiTextResponse = "Loading..."
        @Published public var incomingMessage = ""
        
        let wsService = WebSocketService()
        
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

            connectToWebSocket()
        }
        
        // TODO: implements startCommunication/stopCommunication like in MainViewModel.kt
        // and bind these methods the the iOS view lifecycle onPause/onResume equivalents
        
        func connectToWebSocket() {
            DispatchQueue.main.async {
                self.wsService.connect(
                    hostname: "ws-poker-planning.herokuapp.com",
                    roomUUID: "e78caaee-a1a2-4298-860d-81d7752226ae",
                    listener: self
                ) {error in
                    DispatchQueue.main.async {
                        print(error?.localizedDescription ?? "error while connecting")
                    }
                }
            }
        }

        // WsTextMessageListener impl methods
        func onConnectSuccess() {
            print("iOS: onConnectSuccess.")
        }
        func onConnectFailed() {
            print("iOS: onConnectFailed.")
        }
        func onClose() {
            print("iOS: onClose. Reconnecting...")
            connectToWebSocket()
        }
        func onMessage(text: String) {
            self.incomingMessage = text
            self.objectWillChange.send() //https://marcpalmer.net/me-why-is-my-swiftui-view-not-updating-when-the-model-changes/
            print("iOS: received WebSocket message: \(text)")
        }
    }
}
