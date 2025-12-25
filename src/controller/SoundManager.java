package controller;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages sound effects for the game.
 * Handles loading and playing sound files.
 * 
 * @author Team Sloth
 */
public class SoundManager {
    private static SoundManager instance;
    private Map<String, Clip> soundClips;
    private boolean soundEnabled = true;
    
    private SoundManager() {
        soundClips = new HashMap<>();
        loadSounds();
    }
    
    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }
    
    /**
     * Loads all sound effects from the sounds folder.
     */
    private void loadSounds() {
        loadSound("bomb", "sounds/bomb.wav");
        loadSound("flag", "sounds/flag.wav");
        loadSound("victory", "sounds/victory.wav");
        loadSound("game-over", "sounds/game-over.wav");
        loadSound("surprise", "sounds/surprise.wav");
        loadSound("correct-answer", "sounds/correct-answer.wav");
    }
    
    /**
     * Loads a sound effect from a file.
     * @param name The name/key for the sound
     * @param filePath Path to the sound file (relative to project root)
     */
    private void loadSound(String name, String filePath) {
        try {
            File soundFile = new File(filePath);
            if (!soundFile.exists()) {
                System.err.println("Sound file not found: " + filePath);
                return;
            }
            
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            
            soundClips.put(name, clip);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Error loading sound: " + name + " - " + e.getMessage());
        }
    }
    
    /**
     * Plays a sound effect.
     * @param name The name/key of the sound to play
     */
    public void playSound(String name) {
        if (!soundEnabled) return;
        
        Clip clip = soundClips.get(name);
        if (clip != null) {
            try {
                // Reset clip to beginning if it's already playing
                if (clip.isRunning()) {
                    clip.stop();
                }
                clip.setFramePosition(0);
                clip.start();
            } catch (Exception e) {
                System.err.println("Error playing sound: " + name + " - " + e.getMessage());
            }
        }
    }
    
    /**
     * Enables or disables sound effects.
     */
    public void setSoundEnabled(boolean enabled) {
        this.soundEnabled = enabled;
    }
    
    /**
     * Checks if sound is enabled.
     */
    public boolean isSoundEnabled() {
        return soundEnabled;
    }
    
    /**
     * Cleans up resources.
     */
    public void cleanup() {
        for (Clip clip : soundClips.values()) {
            if (clip != null && clip.isOpen()) {
                clip.stop();
                clip.close();
            }
        }
        soundClips.clear();
    }
}

