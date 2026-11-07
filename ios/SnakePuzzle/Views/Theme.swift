import SwiftUI

public enum GameTheme {
    public static func skyTop(for world: String) -> Color {
        switch world {
        case "Crystal Caverns": return Color(red: 0.18, green: 0.10, blue: 0.28)
        case "Sunken Ruins": return Color(red: 0.12, green: 0.24, blue: 0.25)
        case "Sky Citadel": return Color(red: 0.91, green: 0.96, blue: 0.91)
        case "Magma Core": return Color(red: 0.17, green: 0.04, blue: 0.05)
        default: return Color(red: 0.83, green: 0.94, blue: 0.99)
        }
    }

    public static func skyBottom(for world: String) -> Color {
        switch world {
        case "Crystal Caverns": return Color(red: 0.09, green: 0.05, blue: 0.15)
        case "Sunken Ruins": return Color(red: 0.05, green: 0.13, blue: 0.15)
        case "Sky Citadel": return Color(red: 0.78, green: 0.90, blue: 0.79)
        case "Magma Core": return Color(red: 0.08, green: 0.02, blue: 0.02)
        default: return Color(red: 0.67, green: 0.85, blue: 0.96)
        }
    }

    public static func blockBase(for world: String) -> Color {
        switch world {
        case "Crystal Caverns": return Color(red: 0.23, green: 0.24, blue: 0.32)
        case "Sunken Ruins": return Color(red: 0.29, green: 0.33, blue: 0.41)
        case "Sky Citadel": return Color(red: 0.44, green: 0.50, blue: 0.59)
        case "Magma Core": return Color(red: 0.12, green: 0.12, blue: 0.14)
        default: return Color(red: 0.55, green: 0.35, blue: 0.17)
        }
    }

    public static func blockTrim(for world: String) -> Color {
        switch world {
        case "Crystal Caverns": return Color(red: 0.61, green: 0.32, blue: 0.88)
        case "Sunken Ruins": return Color(red: 0.17, green: 0.71, blue: 0.45)
        case "Sky Citadel": return Color(red: 1.00, green: 0.84, blue: 0.00)
        case "Magma Core": return Color(red: 1.00, green: 0.27, blue: 0.00)
        default: return Color(red: 0.28, green: 0.78, blue: 0.20)
        }
    }

    // Apples & Items
    public static let appleRed = Color(red: 0.93, green: 0.15, blue: 0.14)
    public static let appleLeaf = Color(red: 0.30, green: 0.69, blue: 0.31)
    public static let appleStem = Color(red: 0.36, green: 0.25, blue: 0.22)
    public static let appleGleam = Color(red: 1.00, green: 0.64, blue: 0.64)

    // Portal
    public static let portalOuter = Color(red: 0.61, green: 0.15, blue: 0.69)
    public static let portalGlow = Color(red: 0.88, green: 0.25, blue: 0.98)
    public static let portalInner = Color(red: 0.10, green: 0.02, blue: 0.15)

    // Spikes
    public static let spikeBase = Color(red: 0.38, green: 0.49, blue: 0.55)
    public static let spikePoint = Color(red: 0.90, green: 0.22, blue: 0.21)

    // UI & Stars
    public static let starActive = Color(red: 1.00, green: 0.80, blue: 0.10)
    public static let starInactive = Color.gray.opacity(0.3)
    public static let textPrimary = Color(red: 0.12, green: 0.15, blue: 0.22)
    public static let textSecondary = Color(red: 0.45, green: 0.50, blue: 0.60)
}
