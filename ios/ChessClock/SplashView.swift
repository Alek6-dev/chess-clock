import SwiftUI

/// Écran d'accueil (maquette officielle) : losange, wordmark, invite à toucher l'écran, orbe
/// qui pulse pour signaler que l'écran entier est tapable.
struct SplashView: View {
    var onContinue: () -> Void

    var body: some View {
        ZStack {
            RuledBackground(baseColor: ChessClockColors.inkNight, lineColor: ChessClockColors.lineRule.opacity(0.25))
                .ignoresSafeArea()

            VStack {
                Spacer()
                Rectangle()
                    .fill(ChessClockColors.paper)
                    .frame(width: 40, height: 40)
                    .rotationEffect(.degrees(45))
                Spacer().frame(height: 24)
                Text("Chess Clock")
                    .font(ChessClockFonts.instrumentSerif(48))
                    .foregroundColor(ChessClockColors.ivory)
                Spacer()
            }

            VStack {
                Spacer()
                Text("Toucher l'écran pour commencer")
                    .font(ChessClockFonts.ebGaramond(15))
                    .foregroundColor(ChessClockColors.textMuted)
                Spacer().frame(height: 20)
                PulsingOrb()
                Spacer().frame(height: 64)
            }
        }
        .contentShape(Rectangle())
        .onTapGesture(perform: onContinue)
    }
}

private struct PulsingOrb: View {
    @State private var progress: CGFloat = 0

    var body: some View {
        ZStack {
            Circle()
                .fill(ChessClockColors.ivory)
                .frame(width: 56, height: 56)
                .scaleEffect(1 + progress * 0.9)
                .opacity(0.5 * (1 - progress))
            Circle()
                .fill(
                    RadialGradient(
                        colors: [ChessClockColors.ivory, ChessClockColors.brass],
                        center: .center,
                        startRadius: 0,
                        endRadius: 28
                    )
                )
                .frame(width: 56, height: 56)
        }
        .onAppear {
            withAnimation(.easeOut(duration: 1.4).repeatForever(autoreverses: false)) {
                progress = 1
            }
        }
    }
}
