import SwiftUI

/// Encoche du bouton pause — seule courbe de toute l'app. Mesurée directement sur la maquette
/// officielle (pixel par pixel) : une amande tangente au bord gauche, large de ~52 pt pour
/// ~84 pt de haut (nettement plus trapue qu'un premier essai à vue, pas un fuseau étroit).
struct AmandeShape: Shape {
    func path(in rect: CGRect) -> Path {
        let w = rect.width
        let h = rect.height
        var path = Path()
        path.move(to: CGPoint(x: rect.minX, y: rect.minY))
        path.addCurve(
            to: CGPoint(x: rect.minX + w, y: rect.minY + h * 0.5),
            control1: CGPoint(x: rect.minX + w * 0.85, y: rect.minY + h * 0.08),
            control2: CGPoint(x: rect.minX + w, y: rect.minY + h * 0.30)
        )
        path.addCurve(
            to: CGPoint(x: rect.minX, y: rect.minY + h),
            control1: CGPoint(x: rect.minX + w, y: rect.minY + h * 0.70),
            control2: CGPoint(x: rect.minX + w * 0.85, y: rect.minY + h * 0.92)
        )
        path.closeSubpath()
        return path
    }
}
