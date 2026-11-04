import SwiftUI

@main
public struct SnakePuzzleApp: App {
    public init() {}

    public var body: some Scene {
        WindowGroup {
            GameView()
                .preferredColorScheme(.light)
        }
    }
}
