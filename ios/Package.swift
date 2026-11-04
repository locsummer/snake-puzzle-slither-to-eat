// swift-tools-version:5.9
import PackageDescription

let package = Package(
    name: "SnakePuzzle",
    platforms: [
        .iOS(.v15),
        .macOS(.v12)
    ],
    products: [
        .executable(
            name: "SnakePuzzle",
            targets: ["SnakePuzzle"]
        )
    ],
    targets: [
        .executableTarget(
            name: "SnakePuzzle",
            path: "SnakePuzzle",
            resources: [
                .process("Resources")
            ]
        )
    ]
)
