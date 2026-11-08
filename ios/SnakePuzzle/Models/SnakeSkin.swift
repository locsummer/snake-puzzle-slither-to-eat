import SwiftUI

public enum SnakeSkin: String, CaseIterable, Identifiable {
    case emerald = "emerald"
    case golden = "golden"
    case cyber = "cyber"
    case candy = "candy"
    case shadow = "shadow"

    public var id: String { rawValue }

    public var displayName: String {
        switch self {
        case .emerald: return "Apple Worm (Classic)"
        case .golden: return "Golden Dragon"
        case .cyber: return "Cyber Neon"
        case .candy: return "Strawberry Candy"
        case .shadow: return "Shadow Phantom"
        }
    }

    public var headColor: Color {
        switch self {
        case .emerald: return Color(red: 0.09, green: 0.64, blue: 0.14) // #16A324
        case .golden: return Color(red: 1.00, green: 0.84, blue: 0.00)
        case .cyber: return Color(red: 0.00, green: 0.96, blue: 1.00)
        case .candy: return Color(red: 1.00, green: 0.41, blue: 0.71)
        case .shadow: return Color(red: 0.20, green: 0.28, blue: 0.37)
        }
    }

    public var bodyColor: Color {
        switch self {
        case .emerald: return Color(red: 0.08, green: 0.60, blue: 0.13) // #149921
        case .golden: return Color(red: 0.95, green: 0.61, blue: 0.07)
        case .cyber: return Color(red: 0.00, green: 0.77, blue: 0.80)
        case .candy: return Color(red: 1.00, green: 0.08, blue: 0.58)
        case .shadow: return Color(red: 0.17, green: 0.24, blue: 0.31)
        }
    }

    public var tailColor: Color {
        switch self {
        case .emerald: return Color(red: 0.06, green: 0.48, blue: 0.10)
        case .golden: return Color(red: 0.84, green: 0.54, blue: 0.06)
        case .cyber: return Color(red: 0.00, green: 0.60, blue: 0.80)
        case .candy: return Color(red: 0.78, green: 0.08, blue: 0.52)
        case .shadow: return Color(red: 0.10, green: 0.15, blue: 0.18)
        }
    }

    public var throatColor: Color {
        switch self {
        case .emerald: return Color(red: 0.57, green: 0.83, blue: 0.12) // Lime green #92D41E
        case .golden: return Color(red: 1.00, green: 0.95, blue: 0.46)
        case .cyber: return Color(red: 0.00, green: 0.96, blue: 1.00)
        case .candy: return Color(red: 1.00, green: 0.71, blue: 0.76)
        case .shadow: return Color(red: 0.61, green: 0.35, blue: 0.71)
        }
    }

    public var lipsColor: Color {
        switch self {
        case .emerald: return Color(red: 0.70, green: 0.08, blue: 0.55) // Magenta #B3148B
        case .golden: return Color(red: 0.75, green: 0.22, blue: 0.17)
        case .cyber: return Color(red: 0.88, green: 0.00, blue: 1.00)
        case .candy: return Color(red: 0.53, green: 0.05, blue: 0.31)
        case .shadow: return Color(red: 0.56, green: 0.27, blue: 0.68)
        }
    }

    public var accentColor: Color {
        switch self {
        case .emerald: return Color(red: 0.22, green: 0.78, blue: 0.28)
        case .golden: return Color(red: 1.00, green: 0.95, blue: 0.46)
        case .cyber: return Color(red: 0.88, green: 0.00, blue: 1.00)
        case .candy: return Color(red: 1.00, green: 0.71, blue: 0.76)
        case .shadow: return Color(red: 0.61, green: 0.35, blue: 0.71)
        }
    }

    public var cheekColor: Color {
        switch self {
        case .emerald: return Color.pink.opacity(0.40)
        case .golden: return Color.orange.opacity(0.4)
        case .cyber: return Color.purple.opacity(0.45)
        case .candy: return Color.red.opacity(0.4)
        case .shadow: return Color.purple.opacity(0.35)
        }
    }

    public var eyeColor: Color {
        switch self {
        case .shadow: return Color.red
        default: return Color.white
        }
    }

    public var pupilColor: Color {
        switch self {
        case .golden: return Color(red: 0.47, green: 0.16, blue: 0.12)
        case .cyber: return Color(red: 0.11, green: 0.00, blue: 0.20)
        case .candy: return Color(red: 0.29, green: 0.00, blue: 0.14)
        default: return Color(red: 0.07, green: 0.07, blue: 0.07)
        }
    }
}
