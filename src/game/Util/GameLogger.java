package game.Util;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;



/**
 * GameLogger is a singleton service that collects log messages from all game threads
 * and writes them, in order, to a log file. Internally it uses a BlockingQueue<String>
 * to store log lines; a dedicated writer thread polls this queue and flushes to disk.
 */
public class GameLogger {
    // Single, shared BlockingQueue for all threads to enqueue their messages.
    private final BlockingQueue<String> queue = new LinkedBlockingQueue<>();
    private final Thread writerThread;
    private volatile boolean running = true;
    private final String logFilePath = "game_events.log";


    private GameLogger() {
        writerThread = new Thread(() -> {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFilePath, true))) {
                while (running || !queue.isEmpty()) {
                    String line = queue.take();
                    writer.write(line);
                    writer.newLine();
                    writer.flush();
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }, "GameLogger-Writer");
        writerThread.start();
    }

    private static class Holder {
        private static final GameLogger INSTANCE = new GameLogger();
    }

    public static GameLogger getInstance() {
        return Holder.INSTANCE;
    }


    /**
     * Enqueue a log message.
     *
     * @param message The content describing the event (e.g. "Player moved ...").
     */
    public void log(String message) {
        if (!running) return;
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String fullLine = "[" + timestamp + "] " + message;
        queue.offer(fullLine);
    }

    /**
     * Signals the logger to stop after it has drained all pending messages.
     */
    public void shutdown() {
        running = false;
        writerThread.interrupt();
    }


}
