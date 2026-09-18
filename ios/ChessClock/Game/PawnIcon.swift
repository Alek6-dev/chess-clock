import SwiftUI

/// Silhouette du pion — tracé calqué sur les maquettes officielles (tête ronde, collerette,
/// taille cintrée, base évasée à bord plat), boîte de référence 100 x 140.
private struct PawnShape: Shape {
    func path(in rect: CGRect) -> Path {
        let u = rect.height / 140
        var path = Path()
        path.move(to: CGPoint(x: 50 * u, y: 0))
        path.addCurve(to: CGPoint(x: 74 * u, y: 20 * u), control1: CGPoint(x: 66 * u, y: 0), control2: CGPoint(x: 74 * u, y: 10 * u))
        path.addCurve(to: CGPoint(x: 70 * u, y: 36 * u), control1: CGPoint(x: 74 * u, y: 28 * u), control2: CGPoint(x: 70 * u, y: 34 * u))
        path.addCurve(to: CGPoint(x: 83 * u, y: 54 * u), control1: CGPoint(x: 78 * u, y: 40 * u), control2: CGPoint(x: 83 * u, y: 44 * u))
        path.addLine(to: CGPoint(x: 83 * u, y: 73 * u))
        path.addCurve(to: CGPoint(x: 63 * u, y: 77 * u), control1: CGPoint(x: 83 * u, y: 76 * u), control2: CGPoint(x: 76 * u, y: 76 * u))
        path.addCurve(to: CGPoint(x: 80 * u, y: 116 * u), control1: CGPoint(x: 68 * u, y: 85 * u), control2: CGPoint(x: 74 * u, y: 95 * u))
        path.addCurve(to: CGPoint(x: 91 * u, y: 131 * u), control1: CGPoint(x: 85 * u, y: 122 * u), control2: CGPoint(x: 91 * u, y: 126 * u))
        path.addLine(to: CGPoint(x: 91 * u, y: 140 * u))
        path.addLine(to: CGPoint(x: 9 * u, y: 140 * u))
        path.addLine(to: CGPoint(x: 9 * u, y: 131 * u))
        path.addCurve(to: CGPoint(x: 20 * u, y: 116 * u), control1: CGPoint(x: 9 * u, y: 126 * u), control2: CGPoint(x: 15 * u, y: 122 * u))
        path.addCurve(to: CGPoint(x: 37 * u, y: 77 * u), control1: CGPoint(x: 26 * u, y: 95 * u), control2: CGPoint(x: 32 * u, y: 85 * u))
        path.addCurve(to: CGPoint(x: 17 * u, y: 73 * u), control1: CGPoint(x: 24 * u, y: 76 * u), control2: CGPoint(x: 17 * u, y: 76 * u))
        path.addLine(to: CGPoint(x: 17 * u, y: 54 * u))
        path.addCurve(to: CGPoint(x: 30 * u, y: 36 * u), control1: CGPoint(x: 17 * u, y: 44 * u), control2: CGPoint(x: 22 * u, y: 40 * u))
        path.addCurve(to: CGPoint(x: 26 * u, y: 20 * u), control1: CGPoint(x: 26 * u, y: 34 * u), control2: CGPoint(x: 26 * u, y: 28 * u))
        path.addCurve(to: CGPoint(x: 50 * u, y: 0), control1: CGPoint(x: 26 * u, y: 10 * u), control2: CGPoint(x: 34 * u, y: 0))
        path.closeSubpath()
        return path
    }
}

/// Pion d'échecs vectoriel — exception assumée au principe "aucune pièce dessinée" de la
/// planche, à la demande explicite du produit. [contourColor] : toujours le ton opposé au
/// fond du pion — sur son propre camp, le fond se fond avec le fond de sa zone, seul ce
/// contour le rend visible.
struct PawnIcon: View {
    let fillColor: Color
    let contourColor: Color
    let height: CGFloat

    private var width: CGFloat { height * (100 / 140) }

    var body: some View {
        ZStack {
            PawnShape().fill(fillColor)
            PawnShape().stroke(contourColor, lineWidth: height * (2 / 140))
        }
        .frame(width: width, height: height)
    }
}
