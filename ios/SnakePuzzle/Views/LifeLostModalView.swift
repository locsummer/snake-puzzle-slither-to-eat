import SwiftUI

public struct LifeLostModalView: View {
    public let reason: String
    public let remainingLives: Int
    public let canUndo: Bool
    public let onUndo: () -> Void
    public let onRetryLevel: () -> Void

    public var body: some View {
        ZStack {
            Color.black.opacity(0.5)
                .ignoresSafeArea()

            VStack(spacing: 14) {
                Text("💔 BẠN BỊ MẤT 1 TIM!")
                    .font(.system(size: 22, weight: .black, design: .rounded))
                    .foregroundColor(Color(red: 0.86, green: 0.15, blue: 0.15))

                Text(reason)
                    .font(.system(size: 14, weight: .medium, design: .rounded))
                    .foregroundColor(GameTheme.textSecondary)
                    .multilineTextAlignment(.center)

                VStack(spacing: 4) {
                    HStack(spacing: 6) {
                        ForEach(1...3, id: \.self) { i in
                            Text(i <= remainingLives ? "❤️" : "🖤")
                                .font(.system(size: 24))
                        }
                    }

                    Text("Còn lại \(remainingLives) / 3 tim. Hãy cẩn thận!")
                        .font(.system(size: 13, weight: .bold, design: .rounded))
                        .foregroundColor(Color(red: 0.73, green: 0.11, blue: 0.11))
                }
                .padding(.horizontal, 16)
                .padding(.vertical, 12)
                .frame(maxWidth: .infinity)
                .background(Color(red: 0.99, green: 0.95, blue: 0.95))
                .cornerRadius(14)

                HStack(spacing: 10) {
                    if canUndo {
                        Button(action: onUndo) {
                            HStack {
                                Image(systemName: "arrow.uturn.backward")
                                Text("Hoàn tác (Undo)")
                            }
                            .font(.system(size: 13, weight: .bold, design: .rounded))
                            .foregroundColor(.white)
                            .frame(maxWidth: .infinity)
                            .padding(.vertical, 11)
                            .background(Color(red: 0.15, green: 0.39, blue: 0.92))
                            .cornerRadius(12)
                        }
                    }

                    Button(action: onRetryLevel) {
                        Text("Tiếp Tục")
                            .font(.system(size: 13, weight: .bold, design: .rounded))
                            .foregroundColor(.white)
                            .frame(maxWidth: .infinity)
                            .padding(.vertical, 11)
                            .background(Color(red: 0.09, green: 0.64, blue: 0.29))
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
