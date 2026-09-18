import SwiftUI

/// Les 4 pions fournis (svg/Pion-*.svg), importés tels quels via l'Asset Catalog (SwiftUI les
/// affiche en vectoriel avec "Preserve Vector Data").
enum PawnVariant {
    case whiteFull
    case whiteContour
    case blackFull
    case blackContour

    var assetName: String {
        switch self {
        case .whiteFull: return "Pion-blanc-full"
        case .whiteContour: return "Pion-blanc-contour-noir"
        case .blackFull: return "Pion-noir-full"
        case .blackContour: return "Pion-noir-contour-blanc"
        }
    }
}

/// Pion d'échecs — assets fournis par l'utilisateur, pas de tracé recréé à la main.
/// "Contour" pour les cas où le pion se fond avec le fond de sa propre zone, "Full" quand le
/// contraste est déjà suffisant (badge adversaire).
struct PawnIcon: View {
    let variant: PawnVariant
    let height: CGFloat
    var opacity: Double = 1

    var body: some View {
        Image(variant.assetName)
            .resizable()
            .aspectRatio(75.0 / 103.0, contentMode: .fit)
            .frame(height: height)
            .opacity(opacity)
    }
}
