import SwiftUI

/// Silhouette du pion — tracé de référence dans une boîte 100 x 140.
private struct PawnShape: Shape {
    func path(in rect: CGRect) -> Path {
        let u = rect.height / 140
        var path = Path()
        path.move(to: CGPoint(x: 50 * u, y: 4 * u))
        path.addCurve(
            to: CGPoint(x: 66 * u, y: 20 * u),
            control1: CGPoint(x: 59 * u, y: 4 * u),
            control2: CGPoint(x: 66 * u, y: 11 * u)
        )
        path.addCurve(
            to: CGPoint(x: 59.5 * u, y: 34 * u),
            control1: CGPoint(x: 66 * u, y: 26 * u),
            control2: CGPoint(x: 63.5 * u, y: 31 * u)
        )
        path.addLine(to: CGPoint(x: 62 * u, y: 40 * u))
        path.addCurve(
            to: CGPoint(x: 76 * u, y: 76 * u),
            control1: CGPoint(x: 68 * u, y: 46 * u),
            control2: CGPoint(x: 74 * u, y: 60 * u)
        )
        path.addCurve(
            to: CGPoint(x: 78 * u, y: 88 * u),
            control1: CGPoint(x: 76.8 * u, y: 82 * u),
            control2: CGPoint(x: 78 * u, y: 86 * u)
        )
        path.addLine(to: CGPoint(x: 78 * u, y: 94 * u))
        path.addCurve(
            to: CGPoint(x: 71.5 * u, y: 100 * u),
            control1: CGPoint(x: 78 * u, y: 97.5 * u),
            control2: CGPoint(x: 75 * u, y: 100 * u)
        )
        path.addLine(to: CGPoint(x: 28.5 * u, y: 100 * u))
        path.addCurve(
            to: CGPoint(x: 22 * u, y: 94 * u),
            control1: CGPoint(x: 25 * u, y: 100 * u),
            control2: CGPoint(x: 22 * u, y: 97.5 * u)
        )
        path.addLine(to: CGPoint(x: 22 * u, y: 88 * u))
        path.addCurve(
            to: CGPoint(x: 24 * u, y: 76 * u),
            control1: CGPoint(x: 22 * u, y: 86 * u),
            control2: CGPoint(x: 23.2 * u, y: 82 * u)
        )
        path.addCurve(
            to: CGPoint(x: 38 * u, y: 40 * u),
            control1: CGPoint(x: 26 * u, y: 60 * u),
            control2: CGPoint(x: 32 * u, y: 46 * u)
        )
        path.addLine(to: CGPoint(x: 40.5 * u, y: 34 * u))
        path.addCurve(
            to: CGPoint(x: 34 * u, y: 20 * u),
            control1: CGPoint(x: 36.5 * u, y: 31 * u),
            control2: CGPoint(x: 34 * u, y: 26 * u)
        )
        path.addCurve(
            to: CGPoint(x: 50 * u, y: 4 * u),
            control1: CGPoint(x: 34 * u, y: 11 * u),
            control2: CGPoint(x: 41 * u, y: 4 * u)
        )
        path.closeSubpath()
        return path
    }
}

/// Pion d'échecs vectoriel — exception assumée au principe "aucune pièce dessinée" de la
/// planche, à la demande explicite du produit pour identifier visuellement les chronos par
/// camp. Dessiné à la main (pas d'asset bitmap, pas d'icône stock).
struct PawnIcon: View {
    let fillColor: Color
    let height: CGFloat

    private var width: CGFloat { height * (100 / 140) }

    var body: some View {
        let u = height / 140
        ZStack(alignment: .topLeading) {
            PawnShape().fill(fillColor)
            PawnShape().stroke(ChessClockColors.lineRule, lineWidth: 2 * u)

            RoundedRectangle(cornerRadius: 2 * u)
                .fill(fillColor)
                .overlay(RoundedRectangle(cornerRadius: 2 * u).stroke(ChessClockColors.lineRule, lineWidth: 2 * u))
                .frame(width: 72 * u, height: 15 * u)
                .offset(x: 14 * u, y: 100 * u)
        }
        .frame(width: width, height: height)
    }
}
