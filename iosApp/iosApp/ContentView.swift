import SwiftUI
import shared

let isHttpApiCallResponseVisible: Bool = false
let platformAndDate = ExampleService().getPlatformAndDate()

struct ContentView: View {
    @ObservedObject private(set) var viewModel: ViewModel
    @State private var username: String = "iOS KMM Demo"
    @State private var vote: String = "8"

    var body: some View {
        VStack  (alignment: .center, spacing: 16, content: {
            Text(platformAndDate)
            
            // Poker planning voting form
            TextField("Username", text: $username).textFieldStyle(RoundedBorderTextFieldStyle())
            TextField("Vote", text: $vote).textFieldStyle(RoundedBorderTextFieldStyle())
            Button(
                action: { viewModel.vote(username: username, value: vote)}) {
                    Text("VOTE")
                }

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
        
        let webSocketService = WebSocketService()
        let pokerPlanningService = PokerPlanningService()
        
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
                self.webSocketService.connect(
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
        
        func vote(username: String, value: String) {
            DispatchQueue.main.async {
                let jsonMessage = self.pokerPlanningService.buildEstimateMessageAsJson(username: username, estimate: value)
                Task {
                    do {
                        try await self.webSocketService.sendMessage(text: jsonMessage)
                    } catch {
                        print("Poker planning vote error: \(error)")
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
