import Foundation

public final class LevelRepository: ObservableObject {
    public static let shared = LevelRepository()

    @Published public private(set) var levels: [Level] = []
    @Published public private(set) var unlockedLevelMax: Int = 1

    private let unlockedKey = "SnakePuzzle_UnlockedLevelMax"
    private let starsKeyPrefix = "SnakePuzzle_Stars_"
    private let bestMovesKeyPrefix = "SnakePuzzle_BestMoves_"

    public init() {
        self.unlockedLevelMax = max(1, UserDefaults.standard.integer(forKey: unlockedKey))
        self.levels = loadLevels()
    }

    public func loadLevels() -> [Level] {
        // Try to load from bundle first
        if let url = Bundle.main.url(forResource: "levels", withExtension: "json"),
           let data = try? Data(contentsOf: url),
           let decoded = try? JSONDecoder().decode([Level].self, from: data) {
            return decoded
        }
        // Fallback embedded JSON string
        return LevelRepository.fallbackLevels
    }

    public func isLevelUnlocked(_ levelId: Int) -> Bool {
        return levelId <= unlockedLevelMax
    }

    public func stars(for levelId: Int) -> Int {
        return UserDefaults.standard.integer(forKey: "\(starsKeyPrefix)\(levelId)")
    }

    public func bestMoves(for levelId: Int) -> Int {
        return UserDefaults.standard.integer(forKey: "\(bestMovesKeyPrefix)\(levelId)")
    }

    public func recordWin(levelId: Int, moves: Int, targetApples: Int) {
        // Calculate stars based on efficiency
        let currentStars = stars(for: levelId)
        let newStars: Int
        if moves <= targetApples + 10 {
            newStars = 3
        } else if moves <= targetApples + 18 {
            newStars = 2
        } else {
            newStars = 1
        }
        if newStars > currentStars {
            UserDefaults.standard.set(newStars, forKey: "\(starsKeyPrefix)\(levelId)")
        }

        let existingBest = bestMoves(for: levelId)
        if existingBest == 0 || moves < existingBest {
            UserDefaults.standard.set(moves, forKey: "\(bestMovesKeyPrefix)\(levelId)")
        }

        if levelId >= unlockedLevelMax {
            unlockedLevelMax = min(levels.count, levelId + 1)
            UserDefaults.standard.set(unlockedLevelMax, forKey: unlockedKey)
        }
    }

    public static let fallbackLevels: [Level] = [
        Level(
            id: 1,
            name: "First Bite",
            width: 8,
            height: 5,
            grid: [
                "........",
                "........",
                ".A....O.",
                "SSSSSSSS",
                "SSSSSSSS"
            ],
            snake: [[3, 2], [4, 2]],
            targetApples: 1
        ),
        Level(
            id: 2,
            name: "Across the Gap",
            width: 9,
            height: 5,
            grid: [
                ".........",
                ".........",
                "..A...O..",
                "SSSS.SSSS",
                "SSSS.SSSS"
            ],
            snake: [[1, 2], [0, 2]],
            targetApples: 1
        ),
        Level(
            id: 3,
            name: "Safe Fall",
            width: 8,
            height: 6,
            grid: [
                "........",
                "SS......",
                "SS..A...",
                "SS......",
                "SS....OS",
                "SSSSSSSS"
            ],
            snake: [[1, 0], [0, 0]],
            targetApples: 1
        ),
        Level(
            id: 4,
            name: "Spike Crossing",
            width: 9,
            height: 6,
            grid: [
                ".........",
                ".........",
                "..A...O..",
                "SSSS.SSSS",
                "SSSSXSSSS",
                "SSSSSSSSSS"
            ],
            snake: [[1, 2], [0, 2]],
            targetApples: 1
        ),
        Level(
            id: 5,
            name: "Double Growth",
            width: 10,
            height: 6,
            grid: [
                "..........",
                "..........",
                ".A..A..O..",
                "SSS..SSSSS",
                "SSSXXSSSSS",
                "SSSSSSSSSS"
            ],
            snake: [[1, 2], [0, 2]],
            targetApples: 2
        ),
        Level(
            id: 6,
            name: "Terraces",
            width: 9,
            height: 6,
            grid: [
                ".......OS",
                ".....SSSS",
                "...A.SSSS",
                ".SSSSSSSS",
                "SSSSSSSSS",
                "SSSSSSSSS"
            ],
            snake: [[1, 3], [0, 3]],
            targetApples: 1
        ),
        Level(
            id: 7,
            name: "The High Portal",
            width: 9,
            height: 7,
            grid: [
                ".......O.",
                "......SS.",
                "....A.SS.",
                "..A.S.SS.",
                "SSS.S.SS.",
                "SSS...SS.",
                "SSSSSSSSS"
            ],
            snake: [[1, 3], [0, 3]],
            targetApples: 2
        ),
        Level(
            id: 8,
            name: "The S-Bend",
            width: 9,
            height: 6,
            grid: [
                "SSSSSSSSS",
                "S....A..S",
                "S.SSSSS.S",
                "S.O.....S",
                "SSSSSSSSS",
                "SSSSSSSSS"
            ],
            snake: [[2, 1], [1, 1]],
            targetApples: 1
        ),
        Level(
            id: 9,
            name: "The Pit",
            width: 9,
            height: 6,
            grid: [
                ".........",
                "SS.....SS",
                "SS..A..SS",
                "SS.....SS",
                "SS...O.SS",
                "SSSSSSSSS"
            ],
            snake: [[1, 0], [0, 0]],
            targetApples: 1
        ),
        Level(
            id: 10,
            name: "Pillar Steps",
            width: 10,
            height: 6,
            grid: [
                "..........",
                "..........",
                ".A..A..O..",
                "SS..S..SSS",
                "SSXXSXXSSS",
                "SSSSSSSSSS"
            ],
            snake: [[0, 2], [0, 3]],
            targetApples: 2
        ),
        Level(
            id: 11,
            name: "The Bridge",
            width: 11,
            height: 6,
            grid: [
                "...........",
                "..A....A...",
                "SSS......OS",
                "SSS.SS...SS",
                "SSSXXXXXXSS",
                "SSSSSSSSSSS"
            ],
            snake: [[1, 2], [0, 2]],
            targetApples: 2
        ),
        Level(
            id: 12,
            name: "Two Level Pit",
            width: 9,
            height: 7,
            grid: [
                ".........",
                "SS.....SS",
                "SS..A..SS",
                "SS.SSS.SS",
                "SS..A..SS",
                "SS...O.SS",
                "SSSSSSSSS"
            ],
            snake: [[1, 0], [0, 0]],
            targetApples: 2
        ),
        Level(
            id: 13,
            name: "Spike Overpass",
            width: 11,
            height: 6,
            grid: [
                "...........",
                "....A...O..",
                "SSSS..SSSSS",
                "SSSS..SSSSS",
                "SSSSXXSSSSS",
                "SSSSSSSSSSS"
            ],
            snake: [[1, 1], [0, 1]],
            targetApples: 1
        ),
        Level(
            id: 14,
            name: "Zig Zag",
            width: 10,
            height: 7,
            grid: [
                "..........",
                ".A...SS..O",
                "SSSS.SS.SS",
                "SSSS....SS",
                "SSSS.A..SS",
                "SSSSSSSSSS",
                "SSSSSSSSSS"
            ],
            snake: [[1, 0], [0, 0]],
            targetApples: 2
        ),
        Level(
            id: 15,
            name: "Grandmaster",
            width: 11,
            height: 8,
            grid: [
                "...........",
                ".A......A..",
                "SSS.SSS.SSS",
                "S...S.S...S",
                "S.O.S.S...S",
                "SSS.S.S.SSS",
                "SSS...S.SSS",
                "SSSSSSSSSSS"
            ],
            snake: [[1, 1], [0, 1]],
            targetApples: 2
        )
    ]
}
