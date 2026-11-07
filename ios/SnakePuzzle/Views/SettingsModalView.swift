import SwiftUI

public struct SettingsModalView: View {
    @ObservedObject var sound = SoundManager.shared
    @ObservedObject var repository = LevelRepository.shared
    public let onClose: () -> Void

    public var body: some View {
        ZStack {
            Color.black.opacity(0.4)
                .ignoresSafeArea()

            VStack(spacing: 16) {
                Text("⚙️ Game Settings")
                    .font(.system(size: 20, weight: .black, design: .rounded))
                    .foregroundColor(GameTheme.textPrimary)

                VStack(spacing: 12) {
                    // Music
                    Toggle(isOn: $sound.isMusicEnabled) {
                        VStack(alignment: .leading) {
                            Text("Background Music").font(.system(size: 14, weight: .bold))
                            Text("Ambient Chiptune Melody").font(.system(size: 11)).foregroundColor(.gray)
                        }
                    }

                    // SFX
                    Toggle(isOn: $sound.isSoundEnabled) {
                        VStack(alignment: .leading) {
                            Text("Sound Effects").font(.system(size: 14, weight: .bold))
                            Text("Slither, munch, victory chimes").font(.system(size: 11)).foregroundColor(.gray)
                        }
                    }

                    // Haptics
                    Toggle(isOn: $sound.isHapticsEnabled) {
                        VStack(alignment: .leading) {
                            Text("Haptic Feedback").font(.system(size: 14, weight: .bold))
                            Text("Tactile vibration on moves").font(.system(size: 11)).foregroundColor(.gray)
                        }
                    }

                    // D-Pad Position
                    HStack {
                        VStack(alignment: .leading) {
                            Text("D-Pad Hand").font(.system(size: 14, weight: .bold))
                            Text(repository.isDpadOnLeft ? "Left Hand Side" : "Right Hand Side").font(.system(size: 11)).foregroundColor(.gray)
                        }
                        Spacer()
                        Button(action: {
                            repository.isDpadOnLeft.toggle()
                        }) {
                            Text(repository.isDpadOnLeft ? "Move Right" : "Move Left")
                                .font(.system(size: 12, weight: .bold))
                                .padding(.horizontal, 10)
                                .padding(.vertical, 6)
                                .background(Color(red: 0.20, green: 0.60, blue: 0.86))
                                .foregroundColor(.white)
                                .cornerRadius(8)
                        }
                    }
                }
                .padding(.horizontal, 8)

                Button(action: onClose) {
                    Text("Close")
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
            .frame(maxWidth: 380)
            .shadow(radius: 12)
        }
    }
}
