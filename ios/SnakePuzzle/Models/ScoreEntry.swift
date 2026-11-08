import Foundation

public struct ScoreEntry: Codable, Identifiable {
    public var id: String { "\(score)-\(levelReached)-\(dateStr)" }
    public let score: Int
    public let levelReached: Int
    public let dateStr: String

    public init(score: Int, levelReached: Int, dateStr: String) {
        self.score = score
        self.levelReached = levelReached
        self.dateStr = dateStr
    }
}
