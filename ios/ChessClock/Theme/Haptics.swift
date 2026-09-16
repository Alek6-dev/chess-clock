import UIKit

/// Deux retours haptiques normatifs (planche § Mouvement) : léger au passage de main,
/// double à la fin de partie. Rien d'autre dans l'app.
enum Haptics {
    static func light() {
        UIImpactFeedbackGenerator(style: .light).impactOccurred()
    }

    static func doubleImpact() {
        let generator = UIImpactFeedbackGenerator(style: .rigid)
        generator.impactOccurred()
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.09) {
            generator.impactOccurred()
        }
    }
}
