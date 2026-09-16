import SwiftUI

/// Affiche un temps "m:ss" sans aucun sursaut : Instrument Serif n'a pas de chiffres à
/// largeur fixe (le "1" fait moins de la moitié de la largeur du "0"), donc un Text() simple
/// change de largeur — et donc de position une fois centré — à chaque tick de seconde.
/// Chaque chiffre est ici posé dans un emplacement de largeur fixe (celle du "0", le plus
/// large).
struct TabularTimeText: View {
    let text: String
    let color: Color
    let fontSize: CGFloat

    private var font: Font { ChessClockFonts.instrumentSerif(fontSize) }

    private var digitWidth: CGFloat {
        (0...9).map { widthOf(String($0)) }.max() ?? 0
    }

    private func widthOf(_ string: String) -> CGFloat {
        let uiFont = UIFont(name: "InstrumentSerif-Regular", size: fontSize) ?? UIFont.systemFont(ofSize: fontSize)
        return (string as NSString).size(withAttributes: [.font: uiFont]).width
    }

    var body: some View {
        HStack(spacing: 0) {
            ForEach(Array(text.enumerated()), id: \.offset) { _, char in
                if char.isNumber {
                    Text(String(char))
                        .font(font)
                        .foregroundColor(color)
                        .frame(width: digitWidth)
                } else {
                    Text(String(char))
                        .font(font)
                        .foregroundColor(color)
                }
            }
        }
    }
}
