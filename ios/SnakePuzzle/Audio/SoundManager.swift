import Foundation
import AVFoundation
import UIKit

public final class SoundManager: ObservableObject {
    public static let shared = SoundManager()

    @Published public var isSoundEnabled: Bool {
        didSet {
            UserDefaults.standard.set(isSoundEnabled, forKey: "SnakePuzzle_SFX")
        }
    }
    @Published public var isMusicEnabled: Bool {
        didSet {
            UserDefaults.standard.set(isMusicEnabled, forKey: "SnakePuzzle_Music")
            if isMusicEnabled {
                startBgm()
            } else {
                stopBgm()
            }
        }
    }
    @Published public var isHapticsEnabled: Bool {
        didSet {
            UserDefaults.standard.set(isHapticsEnabled, forKey: "SnakePuzzle_Haptics")
        }
    }

    private var audioEngine: AVAudioEngine?
    private var sfxPlayer: AVAudioPlayerNode?
    private var bgmPlayer: AVAudioPlayerNode?
    private var sampleRate: Double = 44100.0

    private init() {
        self.isSoundEnabled = UserDefaults.standard.object(forKey: "SnakePuzzle_SFX") as? Bool ?? true
        self.isMusicEnabled = UserDefaults.standard.object(forKey: "SnakePuzzle_Music") as? Bool ?? true
        self.isHapticsEnabled = UserDefaults.standard.object(forKey: "SnakePuzzle_Haptics") as? Bool ?? true
        setupAudio()
    }

    private func setupAudio() {
        let engine = AVAudioEngine()
        let sfx = AVAudioPlayerNode()
        let bgm = AVAudioPlayerNode()

        engine.attach(sfx)
        engine.attach(bgm)

        let format = AVAudioFormat(standardFormatWithSampleRate: sampleRate, channels: 1)!
        engine.connect(sfx, to: engine.mainMixerNode, format: format)
        engine.connect(bgm, to: engine.mainMixerNode, format: format)

        do {
            try engine.start()
            self.audioEngine = engine
            self.sfxPlayer = sfx
            self.bgmPlayer = bgm
            sfx.play()
            bgm.play()

            if isMusicEnabled {
                startBgm()
            }
        } catch {
            print("SoundManager: could not start audio engine: \(error)")
        }
    }

    private func startBgm() {
        guard let bgm = bgmPlayer, let engine = audioEngine, engine.isRunning else { return }

        let notes: [Double] = [
            261.63, 329.63, 392.00, 523.25,
            293.66, 349.23, 440.00, 587.33,
            246.94, 293.66, 392.00, 493.88,
            261.63, 329.63, 392.00, 523.25
        ]
        let noteDuration = 0.28
        let noteSamples = Int(sampleRate * noteDuration)
        let totalSamples = noteSamples * notes.count

        guard let format = AVAudioFormat(standardFormatWithSampleRate: sampleRate, channels: 1),
              let buffer = AVAudioPCMBuffer(pcmFormat: format, frameCapacity: AVAudioFrameCount(totalSamples)) else {
            return
        }
        buffer.frameLength = AVAudioFrameCount(totalSamples)
        guard let channels = buffer.floatChannelData else { return }
        let channel = channels[0]

        var current = 0
        for freq in notes {
            for i in 0..<noteSamples {
                let t = Double(i) / sampleRate
                let env = max(0.0, 1.0 - (Double(i) / Double(noteSamples))) * 0.08
                let wave = sin(2.0 * .pi * freq * t) * 0.7 + sin(4.0 * .pi * freq * t) * 0.3
                channel[current] = Float(wave * env)
                current += 1
            }
        }

        bgm.scheduleBuffer(buffer, at: nil, options: .loops, completionHandler: nil)
    }

    private func stopBgm() {
        bgmPlayer?.stop()
    }

    private func playTone(frequencies: [(freq: Double, duration: Double)], waveType: String = "sine") {
        guard isSoundEnabled, let player = sfxPlayer, let engine = audioEngine, engine.isRunning else { return }

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
                let envelope: Float = Float(max(0, 1.0 - (Double(i) / Double(framesForTone))))
                var val: Float = 0
                if waveType == "sine" {
                    val = Float(sin(2.0 * .pi * freq * t))
                } else if waveType == "square" {
                    val = sin(2.0 * .pi * freq * t) >= 0 ? 0.3 : -0.3
                }
                channel[currentFrame] = val * envelope * 0.35
                currentFrame += 1
            }
        }

        player.scheduleBuffer(buffer, at: nil, options: [], completionHandler: nil)
    }

    public func playMove() {
        if isHapticsEnabled {
            let generator = UIImpactFeedbackGenerator(style: .light)
            generator.prepare()
            generator.impactOccurred()
        }
        playTone(frequencies: [(freq: 340, duration: 0.04)], waveType: "sine")
    }

    public func playEat() {
        if isHapticsEnabled {
            let generator = UIImpactFeedbackGenerator(style: .medium)
            generator.prepare()
            generator.impactOccurred()
        }
        playTone(frequencies: [(freq: 587.33, duration: 0.05), (freq: 783.99, duration: 0.06), (freq: 1046.50, duration: 0.10)], waveType: "sine")
    }

    public func playPortalUnlocked() {
        if isHapticsEnabled {
            let generator = UINotificationFeedbackGenerator()
            generator.prepare()
            generator.notificationOccurred(.success)
        }
        playTone(frequencies: [(freq: 440, duration: 0.07), (freq: 554.37, duration: 0.07), (freq: 659.25, duration: 0.10), (freq: 880, duration: 0.18)], waveType: "sine")
    }

    public func playLockedNotice() {
        if isHapticsEnabled {
            let generator = UIImpactFeedbackGenerator(style: .rigid)
            generator.prepare()
            generator.impactOccurred()
        }
        playTone(frequencies: [(freq: 220, duration: 0.06), (freq: 180, duration: 0.08)], waveType: "square")
    }

    public func playFall() {
        if isHapticsEnabled {
            let generator = UIImpactFeedbackGenerator(style: .heavy)
            generator.prepare()
            generator.impactOccurred()
        }
        playTone(frequencies: [(freq: 260, duration: 0.05), (freq: 180, duration: 0.07)], waveType: "square")
    }

    public func playSpike() {
        if isHapticsEnabled {
            let generator = UINotificationFeedbackGenerator()
            generator.prepare()
            generator.notificationOccurred(.error)
        }
        playTone(frequencies: [(freq: 140, duration: 0.12), (freq: 80, duration: 0.18)], waveType: "square")
    }

    public func playWin() {
        if isHapticsEnabled {
            let generator = UINotificationFeedbackGenerator()
            generator.prepare()
            generator.notificationOccurred(.success)
        }
        playTone(frequencies: [
            (freq: 523.25, duration: 0.09),
            (freq: 659.25, duration: 0.09),
            (freq: 783.99, duration: 0.09),
            (freq: 1046.50, duration: 0.28)
        ], waveType: "sine")
    }

    public func playClick() {
        if isHapticsEnabled {
            let generator = UIImpactFeedbackGenerator(style: .light)
            generator.prepare()
            generator.impactOccurred()
        }
    }
}
