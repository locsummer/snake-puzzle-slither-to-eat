import SwiftUI

public struct VirtualDPadView: View {
    public let onDirection: (Direction) -> Void

    public init(onDirection: @escaping (Direction) -> Void) {
        self.onDirection = onDirection
    }

    public var body: some View {
        let btnSize: CGFloat = 64

        VStack(spacing: 8) {
            // Up Button
            DPadButton(icon: "chevron.up", size: btnSize) {
                onDirection(.up)
            }

            HStack(spacing: 50) {
                // Left Button
                DPadButton(icon: "chevron.left", size: btnSize) {
                    onDirection(.left)
                }

                // Center visual spacer
                Circle()
                    .fill(Color.white.opacity(0.35))
                    .frame(width: 24, height: 24)

                // Right Button
                DPadButton(icon: "chevron.right", size: btnSize) {
                    onDirection(.right)
                }
            }

            // Down Button
            DPadButton(icon: "chevron.down", size: btnSize) {
                onDirection(.down)
            }
        }
    }
}

private struct DPadButton: View {
    let icon: String
    let size: CGFloat
    let action: () -> Void

    @State private var isPressed: Bool = false

    var body: some View {
        Button(action: {
            action()
        }) {
            ZStack {
                Circle()
                    .fill(
                        LinearGradient(
                            colors: [Color.white, Color(white: 0.92)],
                            startPoint: .top,
                            endPoint: .bottom
                        )
                    )
                    .shadow(color: Color.black.opacity(0.12), radius: 6, x: 0, y: 3)

                Image(systemName: icon)
                    .font(.system(size: size * 0.42, weight: .bold))
                    .foregroundColor(GameTheme.textPrimary)
            }
            .frame(width: size, height: size)
            .scaleEffect(isPressed ? 0.92 : 1.0)
        }
        .buttonStyle(PlainButtonStyle())
        .simultaneousGesture(
            DragGesture(minimumDistance: 0)
                .onChanged { _ in isPressed = true }
                .onEnded { _ in isPressed = false }
        )
    }
}
