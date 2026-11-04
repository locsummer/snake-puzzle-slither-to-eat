import SwiftUI

public struct GameView: View {
    @StateObject private var repository = LevelRepository.shared
    @StateObject private var sound = SoundManager.shared

    @State private var currentLevelIndex: Int = 0
    @State private var engine: SnakePuzzleEngine?
    @State private var showLevelSelect: Bool = false
    @State private var swipeOffset: CGSize = .zero

    public init() {}

    public var body: some View {
        ZStack {
            // Background gradient
            LinearGradient(
                colors: [GameTheme.backgroundTop, GameTheme.backgroundBottom],
                startPoint: .top,
                endPoint: .bottom
            )
            .ignoresSafeArea()

            VStack(spacing: 12) {
                // Top control bar
                topBar

                // Level info header
                if let lvl = currentLevel {
                    VStack(spacing: 2) {
                        Text("Level \(lvl.id): \(lvl.name)")
                            .font(.system(size: 20, weight: .black, design: .rounded))
                            .foregroundColor(GameTheme.textPrimary)

                        if let eng = engine {
                            let remaining = eng.currentState.remainingApples.count
                            let total = lvl.applePositions.count
                            HStack(spacing: 6) {
                                Image(systemName: "circle.circle.fill")
                                    .foregroundColor(remaining == 0 ? .green : .red)
                                Text(remaining == 0 ? "Portal Open! Reach the Vortex!" : "Apples Left: \(remaining)/\(total)")
                                    .font(.system(size: 13, weight: .bold, design: .rounded))
                                    .foregroundColor(remaining == 0 ? Color(red: 0.1, green: 0.6, blue: 0.2) : GameTheme.textSecondary)
                            }
                        }
                    }
                    .padding(.top, 4)
                }

                Spacer(minLength: 8)

                // Game Board with swipe gestures
                if let eng = engine, let lvl = currentLevel {
                    GameBoardRenderer(
                        level: lvl,
                        state: eng.currentState,
                        portalOpen: eng.currentState.remainingApples.isEmpty
                    )
                    .frame(maxWidth: .infinity, maxHeight: 380)
                    .padding(.horizontal, 16)
                    .contentShape(Rectangle())
                    .gesture(
                        DragGesture(minimumDistance: 15)
                            .onEnded { value in
                                handleSwipe(translation: value.translation)
                            }
                    )
                }

                Spacer(minLength: 8)

                // Virtual D-Pad for buttons
                VirtualDPadView { direction in
                    handleMove(direction)
                }
                .padding(.bottom, 16)
            }
            .padding(.top, 8)

            // Modals
            if let eng = engine, let lvl = currentLevel {
                switch eng.currentState.status {
                case .won:
                    VictoryModalView(
                        levelId: lvl.id,
                        levelName: lvl.name,
                        moves: eng.currentState.movesCount,
                        stars: calculateStars(moves: eng.currentState.movesCount, apples: lvl.applePositions.count),
                        hasNextLevel: currentLevelIndex + 1 < repository.levels.count,
                        onNextLevel: {
                            loadLevel(at: currentLevelIndex + 1)
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
                        canUndo: eng.canUndo,
                        onUndo: {
                            _ = eng.undo()
                        },
                        onRestart: {
                            eng.reset()
                        },
                        onSelectLevel: {
                            showLevelSelect = true
                        }
                    )

                case .playing:
                    EmptyView()
                }
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
                engine = SnakePuzzleEngine(level: firstLevel)
            }
        }
    }

    private var currentLevel: Level? {
        guard currentLevelIndex >= 0 && currentLevelIndex < repository.levels.count else { return nil }
        return repository.levels[currentLevelIndex]
    }

    private var topBar: some View {
        HStack {
            // Level Select button
            Button(action: {
                sound.playClick()
                showLevelSelect = true
            }) {
                HStack(spacing: 4) {
                    Image(systemName: "square.grid.2x2.fill")
                    Text("Levels")
                }
                .font(.system(size: 14, weight: .bold, design: .rounded))
                .foregroundColor(GameTheme.textPrimary)
                .padding(.horizontal, 12)
                .padding(.vertical, 8)
                .background(Color.white)
                .cornerRadius(12)
                .shadow(color: Color.black.opacity(0.06), radius: 4, x: 0, y: 2)
            }

            Spacer()

            // Moves pill
            if let eng = engine {
                HStack(spacing: 4) {
                    Image(systemName: "figure.walk")
                    Text("Moves: \(eng.currentState.movesCount)")
                }
                .font(.system(size: 14, weight: .black, design: .rounded))
                .foregroundColor(GameTheme.textPrimary)
                .padding(.horizontal, 12)
                .padding(.vertical, 8)
                .background(Color.white)
                .cornerRadius(12)
                .shadow(color: Color.black.opacity(0.06), radius: 4, x: 0, y: 2)
            }

            Spacer()

            // Undo Button
            Button(action: {
                if let eng = engine, eng.canUndo {
                    sound.playClick()
                    _ = eng.undo()
                }
            }) {
                Image(systemName: "arrow.uturn.backward")
                    .font(.system(size: 16, weight: .bold))
                    .foregroundColor((engine?.canUndo ?? false) ? GameTheme.textPrimary : Color.gray.opacity(0.4))
                    .frame(width: 36, height: 36)
                    .background(Color.white)
                    .clipShape(Circle())
                    .shadow(color: Color.black.opacity(0.06), radius: 4, x: 0, y: 2)
            }
            .disabled(!(engine?.canUndo ?? false))

            // Restart Button
            Button(action: {
                sound.playClick()
                engine?.reset()
            }) {
                Image(systemName: "arrow.clockwise")
                    .font(.system(size: 16, weight: .bold))
                    .foregroundColor(GameTheme.textPrimary)
                    .frame(width: 36, height: 36)
                    .background(Color.white)
                    .clipShape(Circle())
                    .shadow(color: Color.black.opacity(0.06), radius: 4, x: 0, y: 2)
            }

            // Sound Toggle
            Button(action: {
                sound.isSoundEnabled.toggle()
            }) {
                Image(systemName: sound.isSoundEnabled ? "speaker.wave.2.fill" : "speaker.slash.fill")
                    .font(.system(size: 16, weight: .bold))
                    .foregroundColor(sound.isSoundEnabled ? GameTheme.textPrimary : Color.red.opacity(0.7))
                    .frame(width: 36, height: 36)
                    .background(Color.white)
                    .clipShape(Circle())
                    .shadow(color: Color.black.opacity(0.06), radius: 4, x: 0, y: 2)
            }
        }
        .padding(.horizontal, 16)
    }

    private func handleSwipe(translation: CGSize) {
        let horizontal = abs(translation.width) > abs(translation.height)
        if horizontal {
            if translation.width > 20 {
                handleMove(.right)
            } else if translation.width < -20 {
                handleMove(.left)
            }
        } else {
            if translation.height > 20 {
                handleMove(.down)
            } else if translation.height < -20 {
                handleMove(.up)
            }
        }
    }

    private func handleMove(_ direction: Direction) {
        guard let eng = engine, let lvl = currentLevel else { return }
        let prevApplesCount = eng.currentState.remainingApples.count
        let prevHead = eng.currentState.head

        let moved = eng.move(in: direction)
        if moved {
            let nextState = eng.currentState
            switch nextState.status {
            case .won:
                sound.playWin()
                repository.recordWin(
                    levelId: lvl.id,
                    moves: nextState.movesCount,
                    targetApples: lvl.applePositions.count
                )
            case .lost:
                sound.playSpike()
            case .playing:
                if nextState.remainingApples.count < prevApplesCount {
                    sound.playEat()
                } else if nextState.head.y > prevHead.y + 1 {
                    sound.playFall()
                } else {
                    sound.playMove()
                }
            }
        }
    }

    private func loadLevel(at index: Int) {
        guard index >= 0 && index < repository.levels.count else { return }
        currentLevelIndex = index
        let lvl = repository.levels[index]
        engine = SnakePuzzleEngine(level: lvl)
    }

    private func calculateStars(moves: Int, apples: Int) -> Int {
        if moves <= apples + 10 { return 3 }
        if moves <= apples + 18 { return 2 }
        return 1
    }
}
