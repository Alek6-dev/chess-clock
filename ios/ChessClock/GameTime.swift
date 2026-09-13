import Foundation

struct GameTime: Equatable {
    var minutes: Int
    var seconds: Int

    var totalSeconds: Int { minutes * 60 + seconds }
    var isValid: Bool { totalSeconds > 0 }

    static let defaultTime = GameTime(minutes: 5, seconds: 0)
}
