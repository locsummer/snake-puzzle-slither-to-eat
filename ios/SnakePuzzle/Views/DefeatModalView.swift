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

            VStack(spacing: 16) {
                Text("💥 OOPS!")
                    .font(.system(size: 24, weight: .black, design: .rounded))
                    .foregroundColor(Color(red: 0.90, green: 0.25, blue: 0.25))

                Text(reason)
                    .font(.system(size: 15, weight: .medium, design: .rounded))
                    .foregroundColor(GameTheme.textSecondary)
                    .multilineTextAlignment(.center)

                HStack(spacing: 10) {
                    if canUndo {
                        Button(action: onUndo) {
                            HStack {
                                Image(systemName: "arrow.uturn.backward.circle.fill")
                                Text("Undo Move")
                            }
                            .font(.system(size: 14, weight: .bold, design: .rounded))
                            .foregroundColor(.white)
                            .padding(.horizontal, 14)
                            .padding(.vertical, 12)
                            .background(Color(red: 0.20, green: 0.60, blue: 0.86))
                            .cornerRadius(12)
                        }
                    }

                    Button(action: onRestart) {
                        Text("Try Again")
                            .font(.system(size: 14, weight: .bold, design: .rounded))
                            .foregroundColor(GameTheme.textPrimary)
                            .padding(.horizontal, 14)
                            .padding(.vertical, 12)
                            .background(Color(white: 0.94))
                            .cornerRadius(12)
                    }

                    Button(action: onSelectLevel) {
                        Text("Levels")
                            .font(.system(size: 14, weight: .bold, design: .rounded))
                            .foregroundColor(GameTheme.textSecondary)
                            .padding(.horizontal, 14)
                            .padding(.vertical, 12)
                            .background(Color(white: 0.94))
                            .cornerRadius(12)
                    }
                }
            }
            .padding(24)
            .background(Color.white)
            .cornerRadius(22)
            .frame(maxWidth: 400)
            .shadow(radius: 16)
        }
    }
}
