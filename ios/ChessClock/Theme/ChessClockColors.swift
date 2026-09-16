import SwiftUI

extension Color {
    init(hex: UInt32, opacity: Double = 1) {
        self.init(
            red: Double((hex >> 16) & 0xFF) / 255,
            green: Double((hex >> 8) & 0xFF) / 255,
            blue: Double(hex & 0xFF) / 255,
            opacity: opacity
        )
    }
}

/// Palette de la planche d'identité — axe unique noyer brûlé → ivoire.
/// Aucun noir pur, aucun blanc pur.
enum ChessClockColors {
    static let inkNight = Color(hex: 0x17110D)
    static let inkSurface = Color(hex: 0x2A211A)
    static let lineRule = Color(hex: 0x3E3126)
    static let leather = Color(hex: 0x6B5236)
    static let brass = Color(hex: 0xB08D4F)
    static let paper = Color(hex: 0xE3D5BC)
    static let ivory = Color(hex: 0xF0E6D2)
    static let lacquerRed = Color(hex: 0x8C3B2E)

    static let textMuted = Color(hex: 0x8A7355)
    static let textSoft = Color(hex: 0xC9AE86)
    static let textOnLacquer = Color(hex: 0xF0D9C6)
}
