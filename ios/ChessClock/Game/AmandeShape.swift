import SwiftUI

/// Encoche du bouton pause — seule courbe de toute l'app (planche § Bouton pause & encoche).
/// Deux arcs symétriques qui se referment en pointe sur le bord gauche, bombement maximal au
/// centre. Tracé de référence (boîte 34 × 210) mis à l'échelle de la taille réelle du composant.
struct AmandeShape: Shape {
    func path(in rect: CGRect) -> Path {
        let w = rect.width
        let h = rect.height
        var path = Path()
        path.move(to: CGPoint(x: rect.minX, y: rect.minY))
        path.addCurve(
            to: CGPoint(x: rect.minX + w, y: rect.minY + h * 105 / 210),
            control1: CGPoint(x: rect.minX + w * 18 / 34, y: rect.minY + h * 62 / 210),
            control2: CGPoint(x: rect.minX + w, y: rect.minY + h * 82 / 210)
        )
        path.addCurve(
            to: CGPoint(x: rect.minX, y: rect.minY + h),
            control1: CGPoint(x: rect.minX + w, y: rect.minY + h * 128 / 210),
            control2: CGPoint(x: rect.minX + w * 18 / 34, y: rect.minY + h * 148 / 210)
        )
        path.closeSubpath()
        return path
    }
}
