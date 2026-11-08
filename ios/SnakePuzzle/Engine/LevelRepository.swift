import Foundation

public final class LevelRepository: ObservableObject {
    public static let shared = LevelRepository()

    @Published public private(set) var levels: [Level] = []
    @Published public private(set) var unlockedLevelMax: Int = 1
    @Published public private(set) var highScore: Int = 0
    @Published public var selectedSkin: SnakeSkin = .emerald {
        didSet {
            UserDefaults.standard.set(selectedSkin.rawValue, forKey: skinKey)
        }
    }
    @Published public var isDpadOnLeft: Bool = true {
        didSet {
            UserDefaults.standard.set(isDpadOnLeft, forKey: dpadKey)
        }
    }

    private let unlockedKey = "SnakePuzzle_UnlockedLevelMax_v2"
    private let starsKeyPrefix = "SnakePuzzle_Stars_v2_"
    private let bestMovesKeyPrefix = "SnakePuzzle_BestMoves_v2_"
    private let skinKey = "SnakePuzzle_Skin"
    private let dpadKey = "SnakePuzzle_DpadLeft"
    private let highScoreKey = "SnakePuzzle_HighScore"

    public init() {
        self.unlockedLevelMax = max(1, UserDefaults.standard.integer(forKey: unlockedKey))
        let savedSkin = UserDefaults.standard.string(forKey: skinKey) ?? "emerald"
        self.selectedSkin = SnakeSkin(rawValue: savedSkin) ?? .emerald
        self.isDpadOnLeft = UserDefaults.standard.object(forKey: dpadKey) as? Bool ?? true
        self.highScore = UserDefaults.standard.integer(forKey: highScoreKey)
        self.levels = loadLevels()
    }

    @discardableResult
    public func recordScore(_ score: Int) -> Bool {
        if score > highScore {
            highScore = score
            UserDefaults.standard.set(score, forKey: highScoreKey)
            return true
        }
        return false
    }

    public func loadLevels() -> [Level] {
        if let url = Bundle.main.url(forResource: "levels", withExtension: "json"),
           let data = try? Data(contentsOf: url),
           let decoded = try? JSONDecoder().decode([Level].self, from: data) {
            return decoded
        }
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

    public func recordWin(levelId: Int, moves: Int, targetApples: Int, optimalMoves: Int) {
        let currentStars = stars(for: levelId)
        let newStars: Int
        if moves <= optimalMoves + 2 {
            newStars = 3
        } else if moves <= optimalMoves + 6 {
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
            world: "Green Meadows",
            width: 8,
            height: 5,
            grid: ["........","........",".A....O.","SSSSSSSS","SSSSSSSS"],
            snake: [[3, 2], [4, 2]],
            targetApples: 1,
            optimalMoves: 8
        ),
        Level(
            id: 2,
            name: "Reaching Out",
            world: "Green Meadows",
            width: 8,
            height: 5,
            grid: ["........","........","..A...O.","SSSSSSSS","SSSSSSSS"],
            snake: [[1, 2], [0, 2]],
            targetApples: 1,
            optimalMoves: 5
        )
    ]
}
