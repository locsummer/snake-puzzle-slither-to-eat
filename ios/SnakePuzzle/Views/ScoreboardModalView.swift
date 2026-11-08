import SwiftUI

public struct ScoreboardModalView: View {
    public let scoreboard: [ScoreEntry]
    public let onClose: () -> Void

    public var body: some View {
        ZStack {
            Color.black.opacity(0.5)
                .ignoresSafeArea()

            VStack(spacing: 14) {
                HStack {
                    HStack(spacing: 6) {
                        Text("🏆")
                            .font(.system(size: 24))
                        Text("BẢNG ĐIỂM KỶ LỤC")
                            .font(.system(size: 18, weight: .black, design: .rounded))
                            .foregroundColor(Color(red: 0.12, green: 0.23, blue: 0.54))
                    }

                    Spacer()

                    Button(action: onClose) {
                        Image(systemName: "xmark.circle.fill")
                            .font(.system(size: 22))
                            .foregroundColor(.gray)
                    }
                }

                if scoreboard.isEmpty {
                    Spacer()
                    Text("Chưa có kỷ lục nào!")
                        .foregroundColor(.gray)
                    Spacer()
                } else {
                    ScrollView {
                        VStack(spacing: 8) {
                            ForEach(Array(scoreboard.enumerated()), id: \.offset) { index, entry in
                                let medal: String = {
                                    switch index {
                                    case 0: return "🥇"
                                    case 1: return "🥈"
                                    case 2: return "🥉"
                                    default: return "#\(index + 1)"
                                    }
                                }()

                                HStack {
                                    Text(medal)
                                        .font(.system(size: 16, weight: .bold))

                                    VStack(alignment: .leading, spacing: 2) {
                                        Text("\(entry.score) PTS")
                                            .font(.system(size: 14, weight: .black, design: .rounded))
                                            .foregroundColor(Color(red: 0.06, green: 0.09, blue: 0.16))
                                        Text("Màn đạt được: LV \(entry.levelReached)")
                                            .font(.system(size: 11, weight: .medium, design: .rounded))
                                            .foregroundColor(Color.gray)
                                    }

                                    Spacer()

                                    Text(entry.dateStr)
                                        .font(.system(size: 11, weight: .medium, design: .rounded))
                                        .foregroundColor(Color.gray.opacity(0.8))
                                }
                                .padding(.horizontal, 14)
                                .padding(.vertical, 8)
                                .background(index == 0 ? Color(red: 1.0, green: 0.95, blue: 0.78) : Color(white: 0.96))
                                .cornerRadius(12)
                            }
                        }
                    }
                    .frame(maxHeight: 260)
                }

                Button(action: onClose) {
                    Text("Đóng")
                        .font(.system(size: 14, weight: .bold, design: .rounded))
                        .foregroundColor(.white)
                        .frame(maxWidth: .infinity)
                        .padding(.vertical, 10)
                        .background(Color(red: 0.15, green: 0.39, blue: 0.92))
                        .cornerRadius(12)
                }
            }
            .padding(22)
            .background(Color.white)
            .cornerRadius(22)
            .frame(maxWidth: 440)
            .shadow(radius: 16)
        }
    }
}
