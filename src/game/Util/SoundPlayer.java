package game.Util;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class SoundPlayer {
    public static void play(String filename) {
        AudioInputStream ais = null;
        // קודם מה-classpath
        URL url = SoundPlayer.class.getClassLoader().getResource("sounds/" + filename);
        try {
            if (url != null) {
                ais = AudioSystem.getAudioInputStream(url);
            } else {
                // fallback למערכת הקבצים
                File f = new File("src/game/Resources/sounds/" + filename);
                if (!f.exists()) {
                    System.err.println("Sound not found anywhere: " + filename
                            + "\n  Checked classpath under /sounds/"
                            + "\n  And FS at " + f.getAbsolutePath());
                    return;
                }
                ais = AudioSystem.getAudioInputStream(f);
            }

            try (AudioInputStream stream = ais) {
                Clip clip = AudioSystem.getClip();
                clip.open(stream);
                clip.start();
            }
        } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
            e.printStackTrace();
        }
    }
}
