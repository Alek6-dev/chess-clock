import SwiftUI

/// Écran de jeu — issues #2 (démarrer), #3 (passer la main), #4 (affichage temps réel),
/// #5 (fin de partie) et #6 (reset direct par swipe ou bouton pause, sans confirmation).
struct GameView: View {
    @StateObject private var state: GameClockState
    var onReset: () -> Void

    init(whiteTime: GameTime, blackTime: GameTime, onReset: @escaping () -> Void) {
        _state = StateObject(wrappedValue: GameClockState(whiteTime: whiteTime, blackTime: blackTime))
        self.onReset = onReset
    }

    var body: some View {
        ZStack {
            ChessClockColors.inkNight.ignoresSafeArea()

            VStack(spacing: 0) {
                if state.isOver {
                    GameOverHalf(
                        player: .black,
                        isLoser: state.timedOutPlayer == .black,
                        seconds: state.seconds(for: .black),
                        isRotated: true,
                        onRematch: onReset
                    )
                } else {
                    PlayerZone(
                        player: .black,
                        ownSeconds: state.blackSeconds,
                        opponentSeconds: state.whiteSeconds,
                        isActive: state.activePlayer == .black,
                        isRotated: true,
                        onTap: { state.pass(.black); Haptics.light() },
                        onSwipeReset: onReset
                    )
                }

                if state.isOver {
                    GameOverHalf(
                        player: .white,
                        isLoser: state.timedOutPlayer == .white,
                        seconds: state.seconds(for: .white),
                        isRotated: false,
                        onRematch: onReset
                    )
                } else {
                    PlayerZone(
                        player: .white,
                        ownSeconds: state.whiteSeconds,
                        opponentSeconds: state.blackSeconds,
                        isActive: state.activePlayer == .white,
                        isRotated: false,
                        onTap: { state.pass(.white); Haptics.light() },
                        onSwipeReset: onReset
                    )
                }
            }

            if !state.isOver {
                PauseButton(onTap: onReset)
                    .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .leading)
            }
        }
        .statusBar(hidden: true)
        .onChange(of: state.isOver) { isOver in
            if isOver { Haptics.doubleImpact() }
        }
    }
}

private func formatTime(_ totalSeconds: Int) -> String {
    let minutes = totalSeconds / 60
    let seconds = totalSeconds % 60
    return String(format: "%d:%02d", minutes, seconds)
}

private struct PlayerZone: View {
    let player: Player
    let ownSeconds: Int
    let opponentSeconds: Int
    let isActive: Bool
    let isRotated: Bool
    let onTap: () -> Void
    let onSwipeReset: () -> Void

    private var isBlack: Bool { player == .black }
    private var baseColor: Color { isBlack ? ChessClockColors.leather : ChessClockColors.paper }
    private var lineColor: Color { isBlack ? ChessClockColors.inkNight.opacity(0.16) : ChessClockColors.leather.opacity(0.07) }
    private var activeColor: Color { isBlack ? ChessClockColors.ivory : ChessClockColors.inkSurface }
    private var inactiveColor: Color { activeColor.opacity(0.6) }
    // Couleur du pion = identité du camp (toujours la même), indépendante de la matière de
    // la zone où il se trouve — contrairement à la couleur du chiffre, choisie pour la
    // lisibilité sur sa propre zone.
    private var campColor: Color { isBlack ? ChessClockColors.inkSurface : ChessClockColors.ivory }
    private var opponentCampColor: Color { isBlack ? ChessClockColors.ivory : ChessClockColors.inkSurface }
    private var pawnColor: Color { campColor.opacity(isActive ? 1 : 0.6) }
    private var badgeBorderColor: Color { isBlack ? ChessClockColors.ivory.opacity(0.28) : ChessClockColors.leather.opacity(0.45) }
    private var badgeTextColor: Color { isBlack ? ChessClockColors.ivory.opacity(0.8) : ChessClockColors.leather }

    var body: some View {
        ZStack(alignment: .bottomTrailing) {
            RuledBackground(baseColor: baseColor, lineColor: lineColor)

            HStack(spacing: 14) {
                PawnIcon(fillColor: pawnColor, height: 40)
                TabularTimeText(text: formatTime(ownSeconds), color: isActive ? activeColor : inactiveColor, fontSize: 72)
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity)

            OpponentBadge(
                seconds: opponentSeconds,
                pawnColor: opponentCampColor,
                borderColor: badgeBorderColor,
                textColor: badgeTextColor
            )
            .padding(22)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .rotationEffect(.degrees(isRotated ? 180 : 0))
        .contentShape(Rectangle())
        // Tap sur sa zone = passe la main. Swipe du bouton pause vers l'autre bord (gauche ->
        // droite à l'écran) = reset direct (issue #6). La zone des Noirs étant tournée à
        // 180°, sa translation est dans un repère local déjà inversé : "vers la droite de
        // l'écran" y correspond à une translation négative.
        .gesture(
            DragGesture(minimumDistance: 0)
                .onEnded { value in
                    let swipedTowardOtherEdge = isRotated
                        ? value.translation.width < -80
                        : value.translation.width > 80
                    if swipedTowardOtherEdge {
                        onSwipeReset()
                    } else if abs(value.translation.width) < 24 {
                        onTap()
                    }
                }
        )
    }
}

private struct OpponentBadge: View {
    let seconds: Int
    let pawnColor: Color
    let borderColor: Color
    let textColor: Color

    var body: some View {
        HStack(spacing: 8) {
            PawnIcon(fillColor: pawnColor, height: 18)
            TabularTimeText(text: formatTime(seconds), color: textColor, fontSize: 15)
        }
        .padding(.horizontal, 13)
        .padding(.vertical, 7)
        .overlay(Rectangle().stroke(borderColor, lineWidth: 1))
    }
}

private struct PauseButton: View {
    let onTap: () -> Void

    var body: some View {
        AmandeShape()
            .fill(ChessClockColors.inkSurface)
            .frame(width: 34, height: 210)
            .overlay(
                HStack(spacing: 7) {
                    Rectangle().fill(ChessClockColors.ivory).frame(width: 3.5, height: 14)
                    Rectangle().fill(ChessClockColors.ivory).frame(width: 3.5, height: 14)
                }
            )
            .contentShape(AmandeShape())
            .onTapGesture(perform: onTap)
    }
}

private struct GameOverHalf: View {
    let player: Player
    let isLoser: Bool
    let seconds: Int
    let isRotated: Bool
    let onRematch: () -> Void

    private var campLabel: String { player == .black ? "NOIR" : "BLANC" }

    var body: some View {
        ZStack {
            if isLoser {
                ChessClockColors.lacquerRed
            } else {
                RuledBackground(baseColor: ChessClockColors.paper, lineColor: ChessClockColors.leather.opacity(0.07))
                InkStainOverlay()
            }

            VStack(spacing: 0) {
                Text(formatTime(seconds))
                    .font(ChessClockFonts.instrumentSerif(72))
                    .foregroundColor(isLoser ? ChessClockColors.ivory : ChessClockColors.inkSurface)

                if isLoser {
                    Text("TEMPS ÉCOULÉ — \(campLabel)")
                        .font(ChessClockFonts.ebGaramond(13, weight: .semiBold))
                        .tracking(2)
                        .foregroundColor(ChessClockColors.textOnLacquer)
                        .padding(.top, 18)
                } else {
                    Button(action: onRematch) {
                        Text("REJOUER")
                            .font(ChessClockFonts.ebGaramond(14, weight: .semiBold))
                            .tracking(2)
                            .foregroundColor(ChessClockColors.inkNight)
                            .padding(.horizontal, 52)
                            .padding(.vertical, 20)
                            .background(ChessClockColors.brass)
                    }
                    .padding(.top, 40)
                }
            }
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .rotationEffect(.degrees(isRotated ? 180 : 0))
    }
}
