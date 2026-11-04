import Foundation
import AVFoundation
import UIKit

public final class SoundManager: ObservableObject {
    public static let shared = SoundManager()

    @Published public var isSoundEnabled: Bool {
        didSet {
            UserDefaults.standard.set(isSoundEnabled, forKey: "SnakePuzzle_SoundEnabled")
        }
    }

    private var audioEngine: AVAudioEngine?
    private var playerNode: AVAudioPlayerNode?
    private var sampleRate: Double = 44100.0

    private init() {
        let stored = UserDefaults.standard.object(forKey: "SnakePuzzle_SoundEnabled") as? Bool
        self.isSoundEnabled = stored ?? true
        setupAudioEngine()
    }

    private func setupAudioEngine() {
        let engine = AVAudioEngine()
        let player = AVAudioPlayerNode()
        engine.attach(player)

        let format = AVAudioFormat(standardFormatWithSampleRate: sampleRate, channels: 1)!
        engine.connect(player, to: engine.mainMixerNode, format: format)

        do {
            try engine.start()
            self.audioEngine = engine
            self.playerNode = player
            player.play()
        } catch {
            print("SoundManager: could not start audio engine: \(error)")
        }
    }

    private func playTone(frequencies: [(freq: Double, duration: Double)], waveType: String = "sine") {
        guard isSoundEnabled, let player = playerNode, let engine = audioEngine, engine.isRunning else { return }

        var totalFrames = 0
        for item in frequencies {
            totalFrames += Int(sampleRate * item.duration)
        }

        guard totalFrames > 0,
              let format = AVAudioFormat(standardFormatWithSampleRate: sampleRate, channels: 1),
              let buffer = AVAudioPCMBuffer(pcmFormat: format, frameCapacity: AVAudioFrameCount(totalFrames)) else {
            return
        }

        buffer.frameLength = AVAudioFrameCount(totalFrames)
        guard let channels = buffer.floatChannelData else { return }
        let channel = channels[0]

        var currentFrame = 0
        for (freq, dur) in frequencies {
            let framesForTone = Int(sampleRate * dur)
            for i in 0..<framesForTone {
                let t = Double(i) / sampleRate
                let envelope: Float = Float(max(0, 1.0 - (Double(i) / Double(framesForTone)))) // decay
                var val: Float = 0
                if waveType == "sine" {
                    val = Float(sin(2.0 * .pi * freq * t))
                } else if waveType == "square" {
                    val = sin(2.0 * .pi * freq * t) >= 0 ? 0.3 : -0.3
                }
                channel[currentFrame] = val * envelope * 0.4
                currentFrame += 1
            }
        }

        player.scheduleBuffer(buffer, at: nil, options: [], completionHandler: nil)
    }

    public func playMove() {
        let generator = UIImpactFeedbackGenerator(style: .light)
        generator.prepare()
        generator.impactOccurred()
        playTone(frequencies: [(freq: 320, duration: 0.05)], waveType: "sine")
    }

    public func playEat() {
        let generator = UIImpactFeedbackGenerator(style: .medium)
        generator.prepare()
        generator.impactOccurred()
        playTone(frequencies: [(freq: 523.25, duration: 0.06), (freq: 659.25, duration: 0.06), (freq: 783.99, duration: 0.12)], waveType: "sine")
    }

    public func playFall() {
        let generator = UIImpactFeedbackGenerator(style: .heavy)
        generator.prepare()
        generator.impactOccurred()
        playTone(frequencies: [(freq: 260, duration: 0.06), (freq: 180, duration: 0.08)], waveType: "square")
    }

    public func playSpike() {
        let generator = UINotificationFeedbackGenerator()
        generator.prepare()
        generator.notificationOccurred(.error)
        playTone(frequencies: [(freq: 140, duration: 0.15), (freq: 90, duration: 0.2)], waveType: "square")
    }

    public func playWin() {
        let generator = UINotificationFeedbackGenerator()
        generator.prepare()
        generator.notificationOccurred(.success)
        playTone(frequencies: [
            (freq: 523.25, duration: 0.1),
            (freq: 659.25, duration: 0.1),
            (freq: 783.99, duration: 0.1),
            (freq: 1046.50, duration: 0.25)
        ], waveType: "sine")
    }

    public func playClick() {
        let generator = UIImpactFeedbackGenerator(style: .light)
        generator.prepare()
        generator.impactOccurred()
    }
}
