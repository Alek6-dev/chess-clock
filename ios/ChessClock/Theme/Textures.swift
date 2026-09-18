import SwiftUI

/// Réglure horizontale — seule texture des maquettes officielles : filets fins réguliers,
/// espacement et épaisseur identiques sur tous les écrans (0.5 pt / 5 pt), seule la couleur
/// et l'opacité des filets changent d'un écran à l'autre.
struct RuledBackground: View {
    var baseColor: Color
    var lineColor: Color
    var spacing: CGFloat = 5
    var lineThickness: CGFloat = 0.5

    var body: some View {
        Canvas { context, size in
            var y: CGFloat = 0
            while y < size.height {
                var path = Path()
                path.move(to: CGPoint(x: 0, y: y))
                path.addLine(to: CGPoint(x: size.width, y: y))
                context.stroke(path, with: .color(lineColor), lineWidth: lineThickness)
                y += spacing
            }
        }
        .background(baseColor)
    }
}

/// Unique tache d'encre du papier vieilli — jamais plus d'une par surface, en haut à droite.
struct InkStainOverlay: View {
    var color: Color = ChessClockColors.lacquerRed.opacity(0.08)

    var body: some View {
        Canvas { context, size in
            let center = CGPoint(x: size.width * 0.74, y: size.height * 0.12)
            let radius = min(size.width, size.height) * 0.6
            let rect = CGRect(x: center.x - radius, y: center.y - radius, width: radius * 2, height: radius * 2)
            context.fill(
                Path(ellipseIn: rect),
                with: .radialGradient(
                    Gradient(colors: [color, .clear]),
                    center: center,
                    startRadius: 0,
                    endRadius: radius
                )
            )
        }
        .allowsHitTesting(false)
    }
}
