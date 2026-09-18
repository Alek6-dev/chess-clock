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
                        onSwipeReset: { state.pause() }
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
                        onSwipeReset: { state.pause() }
                    )
                }
            }

            if !state.isOver {
                PauseButton(onTap: { state.pause() })
                    .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .leading)
            }

            PauseOverlay(
                isPaused: state.isPaused && !state.isOver,
                onResume: { state.resume() },
                onConfigure: onReset
            )
        }
        .statusBar(hidden: true)
        .onChange(of: state.isOver) { isOver in
            if isOver { Haptics.doubleImpact() }
        }
    }
}

/// Modale de pause (issue #6) : déclenchée par swipe ou par le bouton pause, comportement
/// identique dans les deux cas. Transition d'entrée "tirée" depuis la gauche vers la droite.
private struct PauseOverlay: View {
    let isPaused: Bool
    let onResume: () -> Void
    let onConfigure: () -> Void

    @State private var progress: CGFloat = 0

    var body: some View {
        if isPaused {
            ZStack {
                ChessClockColors.inkNight.opacity(0.55 * progress)
                    .ignoresSafeArea()
                    .contentShape(Rectangle())
                    .onTapGesture {} // capte les taps derrière la modale

                VStack(alignment: .leading, spacing: 0) {
                    Text("Pause")
                        .font(ChessClockFonts.instrumentSerif(34))
                        .foregroundColor(ChessClockColors.inkSurface)
                    Rectangle()
                        .fill(ChessClockColors.lineRule)
                        .frame(height: 1)
                        .padding(.vertical, 16)
                    Text("Votre partie est en pause, les deux chronomètres sont arrêtés.")
                        .font(ChessClockFonts.ebGaramond(16))
                        .foregroundColor(ChessClockColors.inkSurface)
                    Spacer().frame(height: 12)
                    Text("Souhaitez-vous reprendre votre partie en cours ou en configurer une nouvelle ?")
                        .font(ChessClockFonts.ebGaramond(16))
                        .foregroundColor(ChessClockColors.inkSurface)
                    Spacer().frame(height: 28)
                    HStack(spacing: 14) {
                        Text("REPRENDRE")
                            .font(ChessClockFonts.ebGaramond(13, weight: .semiBold))
                            .tracking(1.5)
                            .foregroundColor(ChessClockColors.paper)
                            .padding(.horizontal, 22)
                            .padding(.vertical, 16)
                            .background(ChessClockColors.inkSurface)
                            .onTapGesture(perform: onResume)
                        Text("CONFIGURER")
                            .font(ChessClockFonts.ebGaramond(13, weight: .semiBold))
                            .tracking(1.5)
                            .foregroundColor(ChessClockColors.inkSurface)
                            .padding(.horizontal, 22)
                            .padding(.vertical, 16)
                            .overlay(Rectangle().stroke(ChessClockColors.inkSurface, lineWidth: 1))
                            .onTapGesture(perform: onConfigure)
                    }
                }
                .padding(32)
                .background(ChessClockColors.paper)
                .padding(.horizontal, 30)
                .offset(x: (progress - 1) * 420)
            }
            .onAppear {
                progress = 0
                withAnimation(.timingCurve(0.22, 0.61, 0.36, 1, duration: 0.32)) {
                    progress = 1
                }
            }
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
    // Le fond de zone EST la couleur du camp (maquettes officielles) : plus de matière
    // cuir/papier séparée. Le pion de son propre camp s'y fond donc entièrement — seul son
    // contour (ton opposé) le rend visible.
    private var baseColor: Color { isBlack ? ChessClockColors.inkSurface : ChessClockColors.ivory }
    private var lineColor: Color { isBlack ? ChessClockColors.ivory.opacity(0.03) : ChessClockColors.leather.opacity(0.05) }
    private var activeColor: Color { isBlack ? ChessClockColors.ivory : ChessClockColors.inkSurface }
    private var inactiveColor: Color { activeColor.opacity(0.6) }
    private var campColor: Color { isBlack ? ChessClockColors.inkSurface : ChessClockColors.ivory }
    private var campContour: Color { isBlack ? ChessClockColors.ivory : ChessClockColors.inkSurface }
    private var opponentCampColor: Color { isBlack ? ChessClockColors.ivory : ChessClockColors.inkSurface }
    private var opponentCampContour: Color { isBlack ? ChessClockColors.inkSurface : ChessClockColors.ivory }
    private var pawnColor: Color { campColor.opacity(isActive ? 1 : 0.6) }

    var body: some View {
        ZStack(alignment: .bottomTrailing) {
            RuledBackground(baseColor: baseColor, lineColor: lineColor)

            // Pion au-dessus, chrono en dessous — authoré identique pour les deux zones : la
            // rotation de la zone du haut se charge de l'inverser visuellement à l'écran.
            VStack(spacing: 8) {
                PawnIcon(fillColor: pawnColor, contourColor: campContour, height: 40)
                TabularTimeText(text: formatTime(ownSeconds), color: isActive ? activeColor : inactiveColor, fontSize: 72)
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity)

            OpponentBadge(seconds: opponentSeconds, pawnColor: opponentCampColor, pawnContour: opponentCampContour, textColor: activeColor)
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
    let pawnContour: Color
    let textColor: Color

    var body: some View {
        HStack(spacing: 8) {
            PawnIcon(fillColor: pawnColor, contourColor: pawnContour, height: 18)
            TabularTimeText(text: formatTime(seconds), color: textColor, fontSize: 15)
        }
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

    private var isBlack: Bool { player == .black }

    var body: some View {
        ZStack {
            if isLoser {
                ChessClockColors.lacquerRed
            } else {
                RuledBackground(
                    baseColor: isBlack ? ChessClockColors.inkSurface : ChessClockColors.ivory,
                    lineColor: isBlack ? ChessClockColors.ivory.opacity(0.03) : ChessClockColors.leather.opacity(0.05)
                )
            }

            VStack(spacing: 0) {
                if isLoser {
                    // Fond de laque rouge : toujours un pion ivoire, quel que soit le camp battu.
                    PawnIcon(fillColor: ChessClockColors.ivory, contourColor: ChessClockColors.lacquerRed, height: 40)
                    Text("DÉFAITE")
                        .font(ChessClockFonts.instrumentSerif(40))
                        .foregroundColor(ChessClockColors.ivory)
                        .padding(.top, 18)
                    Text("Temps écoulé")
                        .font(ChessClockFonts.ebGaramond(15))
                        .foregroundColor(ChessClockColors.textOnLacquer)
                        .padding(.top, 4)
                } else {
                    let pawnColor = isBlack ? ChessClockColors.inkSurface : ChessClockColors.ivory
                    let pawnContour = isBlack ? ChessClockColors.ivory : ChessClockColors.inkSurface
                    PawnIcon(fillColor: pawnColor, contourColor: pawnContour, height: 40)
                    TabularTimeText(
                        text: formatTime(seconds),
                        color: isBlack ? ChessClockColors.ivory : ChessClockColors.inkSurface,
                        fontSize: 72
                    )
                    .padding(.top, 8)
                    Button(action: onRematch) {
                        Text("REJOUER")
                            .font(ChessClockFonts.ebGaramond(14, weight: .semiBold))
                            .tracking(2)
                            .foregroundColor(isBlack ? ChessClockColors.inkSurface : ChessClockColors.ivory)
                            .padding(.horizontal, 52)
                            .padding(.vertical, 20)
                            .background(isBlack ? ChessClockColors.ivory : ChessClockColors.inkSurface)
                    }
                    .padding(.top, 40)
                }
            }
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .rotationEffect(.degrees(isRotated ? 180 : 0))
    }
}
