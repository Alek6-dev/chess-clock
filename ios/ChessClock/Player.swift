enum Player: Equatable {
    case white
    case black

    var opponent: Player { self == .white ? .black : .white }
}
