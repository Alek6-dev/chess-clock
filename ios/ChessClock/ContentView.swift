import SwiftUI

private enum Screen {
    case setup
    case playing(white: GameTime, black: GameTime)
}

struct ContentView: View {
    @State private var screen: Screen = .setup

    var body: some View {
        switch screen {
        case .setup:
            GameSetupView { whiteTime, blackTime in
                screen = .playing(white: whiteTime, black: blackTime)
            }
        case .playing(let whiteTime, let blackTime):
            GameView(whiteTime: whiteTime, blackTime: blackTime) {
                screen = .setup
            }
        }
    }
}

#Preview {
    ContentView()
}
