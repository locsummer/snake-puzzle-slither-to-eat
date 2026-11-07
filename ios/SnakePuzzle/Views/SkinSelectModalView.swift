import SwiftUI

public struct SkinSelectModalView: View {
    @ObservedObject var repository = LevelRepository.shared
    public let onClose: () -> Void

    public var body: some View {
        ZStack {
            Color.black.opacity(0.4)
                .ignoresSafeArea()

            VStack(spacing: 16) {
                Text("🐍 Select Snake Skin")
                    .font(.system(size: 20, weight: .black, design: .rounded))
                    .foregroundColor(GameTheme.textPrimary)

                ScrollView(.horizontal, showsIndicators: false) {
                    HStack(spacing: 14) {
                        ForEach(SnakeSkin.allCases) { skin in
                            let isSelected = repository.selectedSkin == skin

                            Button(action: {
                                repository.selectedSkin = skin
                            }) {
                                VStack(spacing: 8) {
                                    HStack(spacing: 4) {
                                        Circle().fill(skin.headColor).frame(width: 22, height: 22)
                                        Circle().fill(skin.bodyColor).frame(width: 18, height: 18)
                                        Circle().fill(skin.tailColor).frame(width: 14, height: 14)
                                    }

                                    Text(skin.displayName)
                                        .font(.system(size: 12, weight: .bold))
                                        .foregroundColor(GameTheme.textPrimary)

                                    if isSelected {
                                        Text("Active")
                                            .font(.system(size: 10, weight: .bold))
                                            .foregroundColor(.white)
                                            .padding(.horizontal, 8)
                                            .padding(.vertical, 2)
                                            .background(skin.headColor)
                                            .cornerRadius(6)
                                    } else {
                                        Text("Equip")
                                            .font(.system(size: 10))
                                            .foregroundColor(.gray)
                                    }
                                }
                                .padding(12)
                                .frame(width: 120)
                                .background(isSelected ? skin.headColor.opacity(0.12) : Color(white: 0.96))
                                .cornerRadius(14)
                                .overlay(
                                    RoundedRectangle(cornerRadius: 14)
                                        .stroke(isSelected ? skin.headColor : Color.clear, lineWidth: 2)
                                )
                            }
                            .buttonStyle(PlainButtonStyle())
                        }
                    }
                    .padding(.horizontal, 8)
                }

                Button(action: onClose) {
                    Text("Done")
                        .font(.system(size: 15, weight: .bold))
                        .foregroundColor(.white)
                        .frame(maxWidth: .infinity)
                        .padding(.vertical, 10)
                        .background(Color(red: 0.18, green: 0.80, blue: 0.44))
                        .cornerRadius(10)
                }
            }
            .padding(20)
            .background(Color.white)
            .cornerRadius(20)
            .frame(maxWidth: 460)
            .shadow(radius: 12)
        }
    }
}
