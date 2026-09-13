import SwiftUI

struct GameSetupView: View {
    @State private var sameTimeForBoth = true
    @State private var whiteTime = GameTime.defaultTime
    @State private var blackTime = GameTime.defaultTime

    var onStartGame: (GameTime, GameTime) -> Void

    private var canStart: Bool {
        whiteTime.isValid && blackTime.isValid
    }

    var body: some View {
        VStack(spacing: 24) {
            Text("Choisis le temps de ta partie")
                .font(.title2)
                .multilineTextAlignment(.center)

            Toggle(
                "Temps identique pour les deux joueurs",
                isOn: Binding(
                    get: { sameTimeForBoth },
                    set: { newValue in
                        sameTimeForBoth = newValue
                        // Si on repasse en "temps identique", le temps des Blancs s'applique aux deux.
                        if newValue {
                            blackTime = whiteTime
                        }
                    }
                )
            )
            .padding(.horizontal)

            if sameTimeForBoth {
                TimeWheelRow(
                    label: nil,
                    time: Binding(
                        get: { whiteTime },
                        set: { newTime in
                            whiteTime = newTime
                            if sameTimeForBoth {
                                blackTime = newTime
                            }
                        }
                    )
                )
            } else {
                TimeWheelRow(label: "Blancs", time: $whiteTime)
                TimeWheelRow(label: "Noirs", time: $blackTime)
            }

            Button("Démarrer") {
                onStartGame(whiteTime, blackTime)
            }
            .disabled(!canStart)
            .buttonStyle(.borderedProminent)
        }
        .padding()
    }
}

private struct TimeWheelRow: View {
    let label: String?
    @Binding var time: GameTime

    var body: some View {
        VStack {
            if let label {
                Text(label).font(.headline)
            }
            HStack {
                Picker("Minutes", selection: $time.minutes) {
                    ForEach(0..<60) { value in
                        Text("\(value) min").tag(value)
                    }
                }
                .pickerStyle(.wheel)
                .frame(width: 100)

                Picker("Secondes", selection: $time.seconds) {
                    ForEach(0..<60) { value in
                        Text("\(value) s").tag(value)
                    }
                }
                .pickerStyle(.wheel)
                .frame(width: 100)
            }
        }
    }
}

#Preview {
    GameSetupView(onStartGame: { _, _ in })
}
