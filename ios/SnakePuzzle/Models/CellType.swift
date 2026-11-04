import Foundation

public enum CellType: Character, Codable {
    case empty = "."
    case solid = "S"
    case apple = "A"
    case portal = "O"
    case spike = "X"
}
