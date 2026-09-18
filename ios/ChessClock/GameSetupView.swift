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
            RuledBackground(baseColor: ChessClockColors.paper, lineColor: ChessClockColors.lineRule.opacity(0.09))
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

/// Roue native (scroll/snap déjà fiables) avec un encart ivoire plein derrière la valeur
/// centrale, pour matcher la maquette sans réécrire tout le mécanisme de défilement.
private struct HighlightedWheel: View {
    @Binding var selection: Int

    var body: some View {
        ZStack {
            Rectangle()
                .fill(ChessClockColors.ivory)
                .frame(width: 64, height: 52)
            Picker("", selection: $selection) {
                ForEach(0..<60) { value in
                    Text("\(value)").tag(value)
                }
            }
            .pickerStyle(.wheel)
            .frame(width: 90)
        }
    }
}

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

            HStack(spacing: 10) {
                HighlightedWheel(selection: $time.minutes)
                Text(":")
                    .font(ChessClockFonts.instrumentSerif(34))
                    .foregroundColor(ChessClockColors.inkSurface)
                HighlightedWheel(selection: $time.seconds)
            }
        }
    }
}

private struct StartButton: View {
    let enabled: Bool
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            Text(enabled ? "COMMENCER" : "TEMPS NON VALIDE")
                .font(ChessClockFonts.ebGaramond(14, weight: .semiBold))
                .tracking(2)
                .frame(maxWidth: .infinity)
                .padding(.vertical, 22)
                .foregroundColor(enabled ? ChessClockColors.paper : ChessClockColors.inkSurface)
                .background(enabled ? ChessClockColors.inkSurface : Color.clear)
                .overlay(
                    Group {
                        if !enabled {
                            Rectangle().stroke(ChessClockColors.inkSurface, lineWidth: 1)
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
