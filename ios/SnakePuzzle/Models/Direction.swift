import Foundation

public enum Direction: String, CaseIterable, Codable {
    case up = "U"
    case down = "D"
    case left = "L"
    case right = "R"

    public var delta: Position {
        switch self {
        case .up: return Position(x: 0, y: -1)
        case .down: return Position(x: 0, y: 1)
        case .left: return Position(x: -1, y: 0)
        case .right: return Position(x: 1, y: 0)
        }
    }

    public var opposite: Direction {
        switch self {
        case .up: return .down
        case .down: return .up
        case .left: return .right
        case .right: return .left
        }
    }
}
