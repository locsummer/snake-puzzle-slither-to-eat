import SwiftUI

public struct GameView: View {
    @StateObject private var repository = LevelRepository.shared
    @StateObject private var sound = SoundManager.shared

    @State private var currentLevelIndex: Int = 0
    @State private var currentRunScore: Int = 0
    @State private var isNewHighScore: Bool = false
    @State private var engine: SnakePuzzleEngine?
    @State private var showLevelSelect: Bool = false
    @State private var showSkinSelect: Bool = false
    @State private var showSettings: Bool = false

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
                        eng.reset()
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

                // Victory & Defeat modals
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
                        isNewHighScore: isNewHighScore,
                        hasNextLevel: currentLevelIndex + 1 < repository.levels.count,
                        onNextLevel: {
                            currentRunScore = eng.currentState.score
                            if currentLevelIndex + 1 < repository.levels.count {
                                loadLevel(at: currentLevelIndex + 1)
                            }
                        },
                        onReplay: {
                            eng.reset()
                        },
                        onSelectLevel: {
                            showLevelSelect = true
                        }
                    )

                case .lost(let reason):
                    DefeatModalView(
                        reason: reason,
                        score: eng.currentState.score,
                        highScore: repository.highScore,
                        canUndo: eng.canUndo,
                        onUndo: {
                            _ = eng.undo()
                        },
                        onRestartFromBeginning: {
                            // "chết thì chơi lại từ đầu" -> Reset to Level 1 and 0 score!
                            currentRunScore = 0
                            loadLevel(at: 0)
                        },
                        onSelectLevel: {
                            showLevelSelect = true
                        }
                    )

                case .playing:
                    EmptyView()
                }
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
                engine = SnakePuzzleEngine(level: firstLevel, initialScore: currentRunScore, highScore: repository.highScore)
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
        case .levelWon(_, let totalScore):
            sound.playWin()
            isNewHighScore = repository.recordScore(totalScore)
            repository.recordWin(
                levelId: lvl.id,
                moves: eng.currentState.movesCount,
                targetApples: lvl.applePositions.count,
                optimalMoves: lvl.optimalMoves
            )
        case .levelLost(_, let finalScore):
            sound.playSpike()
            _ = repository.recordScore(finalScore)
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

    private func loadLevel(at index: Int) {
        guard index >= 0 && index < repository.levels.count else { return }
        currentLevelIndex = index
        let lvl = repository.levels[index]
        engine = SnakePuzzleEngine(level: lvl, initialScore: currentRunScore, highScore: repository.highScore)
    }
}
