import Foundation

public struct Level: Identifiable, Codable, Equatable {
    public let id: Int
    public let name: String
    public let width: Int
    public let height: Int
    public let grid: [String]
    public let snake: [[Int]]
    public let targetApples: Int

    public var snakePositions: [Position] {
        snake.map { Position(x: $0[0], y: $0[1]) }
    }

    public var applePositions: [Position] {
        var result: [Position] = []
        for y in 0..<height {
            guard y < grid.count else { continue }
            let row = Array(grid[y])
            for x in 0..<width {
                guard x < row.count else { continue }
                if row[x] == "A" {
                    result.append(Position(x: x, y: y))
                }
            }
        }
        return result
    }

    public var portalPosition: Position? {
        for y in 0..<height {
            guard y < grid.count else { continue }
            let row = Array(grid[y])
            for x in 0..<width {
                guard x < row.count else { continue }
                if row[x] == "O" {
                    return Position(x: x, y: y)
                }
            }
        }
        return nil
    }

    public var spikePositions: Set<Position> {
        var result = Set<Position>()
        for y in 0..<height {
            guard y < grid.count else { continue }
            let row = Array(grid[y])
            for x in 0..<width {
                guard x < row.count else { continue }
                if row[x] == "X" {
                    result.insert(Position(x: x, y: y))
                }
            }
        }
        return result
    }

    public var solidPositions: Set<Position> {
        var result = Set<Position>()
        for y in 0..<height {
            guard y < grid.count else { continue }
            let row = Array(grid[y])
            for x in 0..<width {
                guard x < row.count else { continue }
                if row[x] == "S" {
                    result.insert(Position(x: x, y: y))
                }
            }
        }
        return result
    }
}
