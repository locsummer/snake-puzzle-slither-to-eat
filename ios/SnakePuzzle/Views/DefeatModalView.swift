import SwiftUI

public struct DefeatModalView: View {
    public let reason: String
    public let canUndo: Bool
    public let onUndo: () -> Void
    public let onRestart: () -> Void
    public let onSelectLevel: () -> Void

    public var body: some View {
        ZStack {
            Color.black.opacity(0.45)
                .ignoresSafeArea()

            VStack(spacing: 20) {
                // Header
                Text("💥 OOPS!")
                    .font(.system(size: 26, weight: .black, design: .rounded))
                    .foregroundColor(Color(red: 0.90, green: 0.25, blue: 0.25))

                Text(reason)
                    .font(.system(size: 16, weight: .medium, design: .rounded))
                    .foregroundColor(GameTheme.textSecondary)
                    .multilineTextAlignment(.center)
                    .padding(.horizontal, 8)

                // Actions
                VStack(spacing: 12) {
                    if canUndo {
                        Button(action: onUndo) {
                            HStack {
                                Image(systemName: "arrow.uturn.backward.circle.fill")
                                Text("Undo Move")
                            }
                            .font(.system(size: 18, weight: .bold, design: .rounded))
                            .foregroundColor(.white)
                            .frame(maxWidth: .infinity)
                            .padding(.vertical, 14)
                            .background(
                                LinearGradient(
                                    colors: [Color(red: 0.25, green: 0.60, blue: 0.95), Color(red: 0.15, green: 0.45, blue: 0.85)],
                                    startPoint: .top,
                                    endPoint: .bottom
                                )
                            )
                            .cornerRadius(14)
                            .shadow(color: Color.blue.opacity(0.35), radius: 6, x: 0, y: 3)
                        }
                    }

                    Button(action: onRestart) {
                        HStack {
                            Image(systemName: "arrow.clockwise")
                            Text("Try Again")
                        }
                        .font(.system(size: 16, weight: .bold, design: .rounded))
                        .foregroundColor(GameTheme.textPrimary)
                        .frame(maxWidth: .infinity)
                        .padding(.vertical, 12)
                        .background(Color(white: 0.94))
                        .cornerRadius(12)
                    }

                    Button(action: onSelectLevel) {
                        Text("Choose Level")
                            .font(.system(size: 14, weight: .semibold, design: .rounded))
                            .foregroundColor(GameTheme.textSecondary)
                    }
                    .padding(.top, 4)
                }
                .padding(.top, 6)
            }
            .padding(28)
            .background(
                RoundedRectangle(cornerRadius: 24)
                    .fill(Color.white)
                    .shadow(color: Color.black.opacity(0.2), radius: 20, x: 0, y: 10)
            )
            .padding(.horizontal, 32)
        }
        .transition(.opacity.combined(with: .scale(scale: 0.9)))
    }
}
