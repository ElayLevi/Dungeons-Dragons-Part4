// src/game/Util/SoundPlayer.java
package game.Util;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;

public class SoundPlayer {
    public static void play(String filename) {
        URL url = SoundPlayer.class.getClassLoader().getResource("sounds/" + filename);
        if (url == null) {
            System.err.println("Sound not found: " + filename);
            return;
        }
        try (AudioInputStream ais = AudioSystem.getAudioInputStream(url)) {
            Clip clip = AudioSystem.getClip();
            clip.open(ais);
            clip.start();
        } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
            e.printStackTrace();
        }
    }
}
