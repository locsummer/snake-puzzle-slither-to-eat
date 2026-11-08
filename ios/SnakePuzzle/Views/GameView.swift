import SwiftUI

public struct GameView: View {
    @StateObject private var repository = LevelRepository.shared
    @StateObject private var sound = SoundManager.shared

    @State private var currentLevelIndex: Int = 0
    @State private var currentRunScore: Int = 0
    @State private var currentLives: Int = 3
    @State private var isNewHighScore: Bool = false
    @State private var engine: SnakePuzzleEngine?

    @State private var showLevelSelect: Bool = false
    @State private var showSkinSelect: Bool = false
    @State private var showSettings: Bool = false
    @State private var showScoreboard: Bool = false

    private let timer = Timer.publish(every: 1.0, on: .main, in: .common).autoconnect()

    public init() {}

    private var currentLevel: Level? {
        guard currentLevelIndex >= 0 && currentLevelIndex < repository.levels.count else { return nil }
        return repository.levels[currentLevelIndex]
    }

    public var body: some View {
        ZStack {
            if let eng = engine, let lvl = currentLevel {
                LandscapeGameHUD(
                    level: lvl,
                    state: eng.currentState,
                    canUndo: eng.canUndo,
                    isDpadOnLeft: repository.isDpadOnLeft,
                    onDirection: { dir in handleMove(dir) },
                    onUndo: {
                        sound.playClick()
                        _ = eng.undo()
                    },
                    onRestart: {
                        sound.playClick()
                        eng.reset(keepLives: currentLives)
                    },
                    onPrevLevel: {
                        sound.playClick()
                        if currentLevelIndex > 0 {
                            loadLevel(at: currentLevelIndex - 1)
                        }
                    },
                    onNextLevel: {
                        sound.playClick()
                        if currentLevelIndex + 1 < repository.levels.count {
                            loadLevel(at: currentLevelIndex + 1)
                        }
                    },
                    onOpenScoreboard: {
                        sound.playClick()
                        showScoreboard = true
                    },
                    onOpenLevelSelect: {
                        sound.playClick()
                        showLevelSelect = true
                    },
                    onOpenSkinSelect: {
                        sound.playClick()
                        showSkinSelect = true
                    },
                    onOpenSettings: {
                        sound.playClick()
                        showSettings = true
                    }
                ) {
                    GameBoardRenderer(
                        level: lvl,
                        state: eng.currentState,
                        skin: repository.selectedSkin
                    )
                    .contentShape(Rectangle())
                    .gesture(
                        DragGesture(minimumDistance: 16)
                            .onEnded { value in
                                handleSwipe(translation: value.translation)
                            }
                    )
                }

                // Victory, LifeLost & Defeat modals
                switch eng.currentState.status {
                case .won:
                    let moves = eng.currentState.movesCount
                    let stars: Int = {
                        if moves <= lvl.optimalMoves + 2 { return 3 }
                        if moves <= lvl.optimalMoves + 6 { return 2 }
                        return 1
                    }()
                    VictoryModalView(
                        levelId: lvl.id,
                        levelName: lvl.name,
                        world: lvl.world,
                        moves: moves,
                        optimalMoves: lvl.optimalMoves,
                        stars: stars,
                        levelScore: eng.currentState.levelScore,
                        totalScore: eng.currentState.score,
                        highScore: repository.highScore,
                        elapsedSeconds: eng.currentState.elapsedSeconds,
                        timeBonus: eng.currentState.timeBonus,
                        isNewHighScore: isNewHighScore,
                        hasNextLevel: currentLevelIndex + 1 < repository.levels.count,
                        onNextLevel: {
                            let nextIdx = (currentLevelIndex + 1 < repository.levels.count) ? currentLevelIndex + 1 : 0
                            loadLevel(at: nextIdx, keepScore: eng.currentState.score, keepLives: currentLives)
                        },
                        onReplay: {
                            eng.reset(keepLives: currentLives)
                        },
                        onSelectLevel: {
                            showLevelSelect = true
                        }
                    )

                case .lifeLost(let reason, let remainingLives):
                    LifeLostModalView(
                        reason: reason,
                        remainingLives: remainingLives,
                        canUndo: eng.canUndo,
                        onUndo: {
                            _ = eng.undo()
                        },
                        onRetryLevel: {
                            loadLevel(at: currentLevelIndex, keepScore: currentRunScore, keepLives: remainingLives)
                        }
                    )

                case .gameOver(let reason, let finalScore):
                    DefeatModalView(
                        reason: reason,
                        score: finalScore,
                        highScore: repository.highScore,
                        levelReached: lvl.id,
                        onRestartFromBeginning: {
                            // "khi hết 3 tim sẽ thua lưu lại điểm vào bảng điểm và trở về lv1"
                            loadLevel(at: 0, keepScore: 0, keepLives: 3)
                        },
                        onViewScoreboard: {
                            showScoreboard = true
                        }
                    )

                case .playing:
                    EmptyView()
                }
            }

            // Scoreboard Modal
            if showScoreboard {
                ScoreboardModalView(scoreboard: repository.scoreboard, onClose: { showScoreboard = false })
            }

            // Skin Selector Modal
            if showSkinSelect {
                SkinSelectModalView(onClose: { showSkinSelect = false })
            }

            // Settings Modal
            if showSettings {
                SettingsModalView(onClose: { showSettings = false })
            }
        }
        .onReceive(timer) { _ in
            engine?.tickTimer()
        }
        .sheet(isPresented: $showLevelSelect) {
            LevelSelectView(
                onSelectLevel: { selectedLevel in
                    if let index = repository.levels.firstIndex(where: { $0.id == selectedLevel.id }) {
                        loadLevel(at: index)
                        showLevelSelect = false
                    }
                },
                onClose: {
                    showLevelSelect = false
                }
            )
        }
        .onAppear {
            if let firstLevel = repository.levels.first {
                engine = SnakePuzzleEngine(level: firstLevel, initialScore: currentRunScore, highScore: repository.highScore, initialLives: currentLives)
            }
        }
    }

    private func handleSwipe(translation: CGSize) {
        let horizontal = abs(translation.width) > abs(translation.height)
        if horizontal {
            if translation.width > 20 { handleMove(.right) }
            else if translation.width < -20 { handleMove(.left) }
        } else {
            if translation.height > 20 { handleMove(.down) }
            else if translation.height < -20 { handleMove(.up) }
        }
    }

    private func handleMove(_ direction: Direction) {
        guard let eng = engine, let lvl = currentLevel else { return }
        let result = eng.move(direction)
        switch result {
        case .levelWon(_, let totalScore, _):
            sound.playWin()
            currentRunScore = totalScore
            isNewHighScore = repository.recordScore(totalScore)
            repository.recordWin(
                levelId = lvl.id,
                moves: eng.currentState.movesCount,
                targetApples: lvl.applePositions.count,
                optimalMoves: lvl.optimalMoves
            )
        case .lifeLost(let remaining, _):
            sound.playSpike()
            currentLives = remaining
        case .gameOver(let finalScore, _):
            sound.playSpike()
            currentLives = 0
            repository.addScoreToBoard(score: finalScore, levelReached: lvl.id)
        case .appleEaten(_, let portalJustOpened, _):
            sound.playEat()
            if portalJustOpened {
                sound.playPortalUnlocked()
            }
        case .portalLockedNotice:
            sound.playLockedNotice()
        case .moved:
            sound.playMove()
        case .blocked:
            break
        }
    }

    private func loadLevel(at index: Int, keepScore: Int = 0, keepLives: Int = 3) {
        guard index >= 0 && index < repository.levels.count else { return }
        currentLevelIndex = index
        currentRunScore = keepScore
        currentLives = keepLives
        let lvl = repository.levels[index]
        engine = SnakePuzzleEngine(level: lvl, initialScore: keepScore, highScore: repository.highScore, initialLives: keepLives)
    }
}
