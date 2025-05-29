package game.Util;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class SoundPlayer {
    public static void play(String filename) {
        URL url = SoundPlayer.class.getClassLoader().getResource("sounds/" + filename);
        try {
            AudioInputStream ais;
            if (url != null) {
                ais = AudioSystem.getAudioInputStream(url);
            } else {
                File f = new File("src/game/Resources/sounds/" + filename);
                if (!f.exists()) {
                    System.err.println("Sound not found anywhere: " + filename);
                    return;
                }
                ais = AudioSystem.getAudioInputStream(f);
            }
            try (AudioInputStream stream = ais) {
                Clip clip = AudioSystem.getClip();
                clip.open(stream);
                clip.start();
            }
        } catch (UnsupportedAudioFileException e) {
            System.err.println("Audio format not supported: " + filename);
        } catch (LineUnavailableException | IOException e) {
            e.printStackTrace();
        }
    }

}
