import SwiftUI

/// Deux familles de la planche : Instrument Serif (chronos, titres) et EB Garamond
/// (interface, libellés). Fichiers statiques embarqués dans Fonts/, licence SIL OFL,
/// déclarés dans Info.plist (UIAppFonts).
enum ChessClockFonts {
    static func instrumentSerif(_ size: CGFloat) -> Font {
        .custom("InstrumentSerif-Regular", size: size)
    }

    static func ebGaramond(_ size: CGFloat, weight: EBGaramondWeight = .regular) -> Font {
        .custom(weight.postScriptName, size: size)
    }

    enum EBGaramondWeight {
        case regular
        case medium
        case semiBold
        case italic

        var postScriptName: String {
            switch self {
            case .regular: return "EBGaramond-Regular"
            case .medium: return "EBGaramond-Medium"
            case .semiBold: return "EBGaramond-SemiBold"
            case .italic: return "EBGaramond-Italic"
            }
        }
    }
}
