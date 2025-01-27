package fs.watcher;

import fs.watcher.pipeline.WatchDestroyer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;

import static java.nio.file.StandardWatchEventKinds.*;

public class FsWatcherTest {

    @Test
    void testWatcher() throws IOException, InterruptedException {
        Path path = Path.of("/", "home", "tepo");

        System.out.println("before watcher");

        WatchDestroyer watchDestroyer = FsWatcher
                .watch(path)
                .forEvents(ENTRY_CREATE, ENTRY_DELETE)
                .onChange(c -> {
                    System.out.println("changed path: " + c.path() + ", event: " + c.kind());
                });

        System.out.println("after watcher");

        while (true) {
            Thread.sleep(2000);

            System.out.println("main");
        }
    }

}
