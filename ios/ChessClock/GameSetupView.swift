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
        ZStack {
            RuledBackground(baseColor: ChessClockColors.paper, lineColor: ChessClockColors.leather.opacity(0.07))
                .ignoresSafeArea()
            InkStainOverlay()
                .ignoresSafeArea()

            VStack(alignment: .leading, spacing: 0) {
                Text("Configuration")
                    .font(ChessClockFonts.instrumentSerif(34))
                    .foregroundColor(ChessClockColors.inkSurface)

                Rectangle()
                    .fill(ChessClockColors.leather.opacity(0.45))
                    .frame(height: 1)
                    .padding(.vertical, 22)

                CheckboxRow(
                    checked: sameTimeForBoth,
                    onToggle: {
                        sameTimeForBoth.toggle()
                        // Si on repasse en "temps identique", le temps des Blancs s'applique aux deux.
                        if sameTimeForBoth {
                            blackTime = whiteTime
                        }
                    }
                )

                Spacer().frame(height: 32)

                if sameTimeForBoth {
                    TimeWheelRow(
                        swatch: nil,
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
                    .frame(maxWidth: .infinity)
                } else {
                    TimeWheelRow(swatch: .white, time: $whiteTime)
                        .frame(maxWidth: .infinity)
                    Spacer().frame(height: 24)
                    TimeWheelRow(swatch: .black, time: $blackTime)
                        .frame(maxWidth: .infinity)
                }

                Spacer()

                StartButton(enabled: canStart) {
                    onStartGame(whiteTime, blackTime)
                }
            }
            .padding(.horizontal, 30)
            .padding(.top, 76)
            .padding(.bottom, 44)
        }
    }
}

private struct CheckboxRow: View {
    let checked: Bool
    let onToggle: () -> Void

    var body: some View {
        HStack(spacing: 14) {
            ZStack {
                if checked {
                    Rectangle().fill(ChessClockColors.inkSurface)
                    Text("✓")
                        .foregroundColor(ChessClockColors.paper)
                        .font(.system(size: 15))
                } else {
                    Rectangle()
                        .stroke(ChessClockColors.leather, lineWidth: 1)
                }
            }
            .frame(width: 22, height: 22)

            Text("Temps identique pour les deux joueurs")
                .font(ChessClockFonts.ebGaramond(18))
                .foregroundColor(ChessClockColors.inkSurface)
        }
        .contentShape(Rectangle())
        .onTapGesture(perform: onToggle)
    }
}

private enum CampSwatch { case white, black }

private struct TimeWheelRow: View {
    let swatch: CampSwatch?
    @Binding var time: GameTime

    var body: some View {
        VStack(alignment: .center, spacing: 12) {
            if let swatch {
                Group {
                    if swatch == .white {
                        Rectangle()
                            .fill(ChessClockColors.ivory)
                            .overlay(Rectangle().stroke(ChessClockColors.leather, lineWidth: 1))
                    } else {
                        Rectangle().fill(ChessClockColors.leather)
                    }
                }
                .frame(width: 30, height: 9)
            }

            HStack(spacing: 8) {
                Picker("Minutes", selection: $time.minutes) {
                    ForEach(0..<60) { value in
                        Text("\(value)").tag(value)
                    }
                }
                .pickerStyle(.wheel)
                .frame(width: 90)

                Text("min")
                    .font(ChessClockFonts.ebGaramond(15))
                    .foregroundColor(ChessClockColors.leather)

                Picker("Secondes", selection: $time.seconds) {
                    ForEach(0..<60) { value in
                        Text("\(value)").tag(value)
                    }
                }
                .pickerStyle(.wheel)
                .frame(width: 90)

                Text("s")
                    .font(ChessClockFonts.ebGaramond(15))
                    .foregroundColor(ChessClockColors.leather)
            }
        }
    }
}

private struct StartButton: View {
    let enabled: Bool
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            Text("DÉMARRER")
                .font(ChessClockFonts.ebGaramond(14, weight: .semiBold))
                .tracking(2)
                .frame(maxWidth: .infinity)
                .padding(.vertical, 22)
                .foregroundColor(enabled ? ChessClockColors.inkNight : ChessClockColors.leather.opacity(0.55))
                .background(
                    Group {
                        if enabled {
                            LinearGradient(
                                colors: [
                                    ChessClockColors.brass.opacity(0.7),
                                    ChessClockColors.ivory.opacity(0.4),
                                    ChessClockColors.brass,
                                ],
                                startPoint: .leading,
                                endPoint: .trailing
                            )
                        } else {
                            Color.clear
                        }
                    }
                )
                .overlay(
                    Group {
                        if !enabled {
                            Rectangle().stroke(ChessClockColors.leather.opacity(0.45), lineWidth: 1)
                        }
                    }
                )
        }
        .disabled(!enabled)
    }
}

#Preview {
    GameSetupView(onStartGame: { _, _ in })
}
