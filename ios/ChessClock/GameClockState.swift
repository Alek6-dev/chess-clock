import Foundation
import Combine

/// État d'une partie en cours (issues #2, #3, #4, #5). Les Blancs sont toujours actifs
/// au démarrage (règle des échecs). Un seul chrono décompte à la fois ; à zéro, la partie
/// s'arrête définitivement.
final class GameClockState: ObservableObject {
    @Published private(set) var whiteSeconds: Int
    @Published private(set) var blackSeconds: Int
    @Published private(set) var activePlayer: Player = .white
    @Published private(set) var isOver = false
    @Published private(set) var isPaused = false

    private var timer: Timer?

    init(whiteTime: GameTime, blackTime: GameTime) {
        whiteSeconds = whiteTime.totalSeconds
        blackSeconds = blackTime.totalSeconds
        scheduleTimer()
    }

    deinit {
        timer?.invalidate()
    }

    /// Le joueur dont le temps est écoulé, une fois la partie terminée.
    var timedOutPlayer: Player? { isOver ? activePlayer : nil }

    func seconds(for player: Player) -> Int {
        player == .white ? whiteSeconds : blackSeconds
    }

    /// Tap sur la zone active : passe la main.
    func pass(_ tappedPlayer: Player) {
        guard !isOver, !isPaused, tappedPlayer == activePlayer else { return }
        activePlayer = activePlayer.opponent
        scheduleTimer()
    }

    /// Pause (issue #6) : coupe le tick, aucun temps n'est consommé pendant ce temps.
    func pause() {
        guard !isOver, !isPaused else { return }
        isPaused = true
        timer?.invalidate()
    }

    /// Reprend exactement où la partie avait été mise en pause.
    func resume() {
        guard !isOver, isPaused else { return }
        isPaused = false
        scheduleTimer()
    }

    private func scheduleTimer() {
        timer?.invalidate()
        guard !isOver, !isPaused else { return }
        timer = Timer.scheduledTimer(withTimeInterval: 1, repeats: true) { [weak self] _ in
            self?.tick()
        }
    }

    private func tick() {
        guard !isOver else { return }
        if activePlayer == .white {
            whiteSeconds = max(0, whiteSeconds - 1)
            if whiteSeconds == 0 { isOver = true; timer?.invalidate() }
        } else {
            blackSeconds = max(0, blackSeconds - 1)
            if blackSeconds == 0 { isOver = true; timer?.invalidate() }
        }
    }
}
