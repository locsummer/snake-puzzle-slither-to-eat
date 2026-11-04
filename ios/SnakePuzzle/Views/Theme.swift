import SwiftUI

public enum GameTheme {
    public static let backgroundTop = Color(red: 0.88, green: 0.94, blue: 0.98)
    public static let backgroundBottom = Color(red: 0.72, green: 0.85, blue: 0.92)

    public static let gridBackground = Color(red: 0.16, green: 0.20, blue: 0.28).opacity(0.12)
    public static let gridLine = Color.white.opacity(0.3)

    // Snake colors
    public static let snakeHead = Color(red: 0.20, green: 0.78, blue: 0.35)
    public static let snakeBody = Color(red: 0.15, green: 0.68, blue: 0.30)
    public static let snakeTail = Color(red: 0.12, green: 0.58, blue: 0.25)
    public static let snakeStripe = Color(red: 0.30, green: 0.88, blue: 0.45)
    public static let snakeEye = Color.white
    public static let snakePupil = Color(red: 0.10, green: 0.10, blue: 0.12)
    public static let snakeTongue = Color(red: 0.95, green: 0.25, blue: 0.35)

    // Environment blocks
    public static let solidGrassTop = Color(red: 0.42, green: 0.78, blue: 0.28)
    public static let solidDirt = Color(red: 0.55, green: 0.40, blue: 0.28)
    public static let solidStone = Color(red: 0.35, green: 0.38, blue: 0.45)

    // Apple
    public static let appleRed = Color(red: 0.92, green: 0.22, blue: 0.20)
    public static let appleLeaf = Color(red: 0.35, green: 0.78, blue: 0.25)
    public static let appleStem = Color(red: 0.40, green: 0.25, blue: 0.15)

    // Portal
    public static let portalOuter = Color(red: 0.55, green: 0.20, blue: 0.95)
    public static let portalInner = Color(red: 0.15, green: 0.05, blue: 0.35)
    public static let portalGlow = Color(red: 0.75, green: 0.45, blue: 1.0)

    // Spike
    public static let spikeBase = Color(red: 0.35, green: 0.38, blue: 0.42)
    public static let spikeTip = Color(red: 0.85, green: 0.20, blue: 0.25)

    // UI & Stars
    public static let starActive = Color(red: 1.0, green: 0.80, blue: 0.10)
    public static let starInactive = Color.gray.opacity(0.3)
    public static let cardBackground = Color.white
    public static let textPrimary = Color(red: 0.12, green: 0.15, blue: 0.22)
    public static let textSecondary = Color(red: 0.45, green: 0.50, blue: 0.60)
}
