package fs.watcher;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;

import static java.nio.file.StandardWatchEventKinds.*;

public class FsWatcherTest {

    @Test
    void testWatcher() throws IOException, InterruptedException {
        FsWatcher fsWatcher = new FsWatcher();
        Path path = Path.of("/", "home", "tepo", "qwer");

        System.out.println("before watcher");

        fsWatcher.watch(path, Thread.currentThread(), ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);

        System.out.println("after watcher");

        while (true) {
            Thread.sleep(3000);

            System.out.println("main");
        }
    }

}
