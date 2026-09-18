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
                    .frame(maxWidth: .infinity, alignment: .center)

                Rectangle()
                    .fill(ChessClockColors.leather.opacity(0.45))
                    .frame(height: 1)
                    .padding(.top, 8)
                    .padding(.bottom, 44)

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

/// Roue native (le scroll/snap de Picker(.wheel) est déjà fiable) avec un encart plein
/// derrière la valeur centrale. Contrairement à un NumberPicker Android, chaque ligne est ici
/// un Text que l'on fournit nous-même : on peut donc colorer la valeur sélectionnée
/// différemment de ses voisines sans widget custom.
///
/// Picker(.wheel) n'a pas de mode "boucle infinie" natif : le cycle 0..59 est donc répété de
/// nombreuses fois (`virtualCount`), avec un point de départ calé au milieu de la plage
/// virtuelle, pour donner une sensation de défilement infini dans les deux sens (59 -> 0 et
/// 0 -> 59) sans butée.
private struct HighlightedWheel: View {
    @Binding var selection: Int
    let boxColor: Color
    let selectedTextColor: Color

    private let span = 60
    private let virtualCount = 60 * 2000

    @State private var virtualSelection: Int

    init(selection: Binding<Int>, boxColor: Color, selectedTextColor: Color) {
        self._selection = selection
        self.boxColor = boxColor
        self.selectedTextColor = selectedTextColor
        let span = 60
        let virtualCount = span * 2000
        let middleCycleStart = (virtualCount / 2 / span) * span
        self._virtualSelection = State(initialValue: middleCycleStart + selection.wrappedValue)
    }

    var body: some View {
        ZStack {
            Rectangle()
                .fill(boxColor)
                .frame(width: 64, height: 52)
            Picker("", selection: $virtualSelection) {
                ForEach(0..<virtualCount, id: \.self) { index in
                    Text(String(format: "%02d", index % span))
                        .font(ChessClockFonts.instrumentSerif(index == virtualSelection ? 34 : 24))
                        .foregroundColor(index == virtualSelection ? selectedTextColor : ChessClockColors.inkSurface)
                        .tag(index)
                }
            }
            .pickerStyle(.wheel)
            .frame(width: 90)
            .onChange(of: virtualSelection) { newIndex in
                let actual = newIndex % span
                if actual != selection {
                    selection = actual
                }
            }
            .onChange(of: selection) { newValue in
                let currentActual = virtualSelection % span
                if currentActual != newValue {
                    var delta = newValue - currentActual
                    if delta > span / 2 { delta -= span }
                    if delta < -span / 2 { delta += span }
                    virtualSelection += delta
                }
            }
        }
    }
}

private struct TimeWheelRow: View {
    let swatch: CampSwatch?
    @Binding var time: GameTime

    // Par défaut (mode "temps identique", aucun camp) : encart encre + texte ivoire.
    // Par joueur : encart et texte dans la couleur du camp de cette roue.
    private var boxColor: Color { swatch == .white ? ChessClockColors.ivory : ChessClockColors.inkSurface }
    private var selectedTextColor: Color { swatch == .white ? ChessClockColors.inkSurface : ChessClockColors.ivory }

    var body: some View {
        HStack(alignment: .center, spacing: 14) {
            if let swatch {
                let pawnVariant: PawnVariant = swatch == .white ? .whiteContour : .blackContour
                PawnIcon(variant: pawnVariant, height: 60)
            } else {
                ComboPawnIcon(height: 60)
            }

            HighlightedWheel(selection: $time.minutes, boxColor: boxColor, selectedTextColor: selectedTextColor)
            Text(":")
                .font(ChessClockFonts.instrumentSerif(34))
                .foregroundColor(ChessClockColors.inkSurface)
            HighlightedWheel(selection: $time.seconds, boxColor: boxColor, selectedTextColor: selectedTextColor)
        }
        .frame(maxWidth: .infinity, alignment: .center)
    }
}

/// Icône par défaut (mode "temps identique") : les deux pions se chevauchent, un par camp.
private struct ComboPawnIcon: View {
    let height: CGFloat

    var body: some View {
        ZStack {
            PawnIcon(variant: .blackContour, height: height)
                .offset(x: height * 0.22)
            PawnIcon(variant: .whiteContour, height: height)
                .offset(x: -height * 0.22)
        }
        .frame(width: height * 0.9, height: height)
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
