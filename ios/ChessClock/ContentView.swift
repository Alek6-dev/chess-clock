import SwiftUI

struct ContentView: View {
    @State private var confirmationMessage: String?

    var body: some View {
        GameSetupView { whiteTime, blackTime in
            // Stub temporaire : le vrai lancement de partie (écran des
            // chronos) arrive avec l'issue #2.
            confirmationMessage =
                "Blancs \(whiteTime.minutes):\(whiteTime.seconds) — " +
                "Noirs \(blackTime.minutes):\(blackTime.seconds)"
        }
        .alert(
            "Partie configurée",
            isPresented: Binding(
                get: { confirmationMessage != nil },
                set: { isPresented in
                    if !isPresented {
                        confirmationMessage = nil
                    }
                }
            )
        ) {
            Button("OK", role: .cancel) {}
        } message: {
            Text(confirmationMessage ?? "")
        }
    }
}

#Preview {
    ContentView()
}
