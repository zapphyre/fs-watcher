package fs.watcher;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;


public class FsWatcher {

    private Thread watcherThread;
    private final WatchService watcher = FileSystems.getDefault().newWatchService();
    Path fileWatching;

    public FsWatcher() throws IOException {
    }

    void watch(Path dirOrFile, Thread syncWith, WatchEvent.Kind<Path>... events) throws IOException, InterruptedException {

        boolean directory = Files.isDirectory(dirOrFile);

        if (!directory) {
            fileWatching = dirOrFile.getFileName();
            dirOrFile = dirOrFile.getParent();
        }

        boolean exists = Files.exists(dirOrFile);

        WatchKey register = dirOrFile.register(watcher, events);

//        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

        System.out.println("before executor");
//        scheduledExecutorService.execute(() -> {

        watcherThread = new Thread(() -> {
            WatchKey key = null;
            try {
                int i = 0;
//            while (i++ < 3) {
                while (true) {
                    System.out.println("Watching " + fileWatching);
                    key = watcher.take();
//                    syncWith.join();

                    System.out.println("taken");

//                    Thread.sleep(100);
                    List<WatchEvent<?>> watchEvents = key.pollEvents();
                    System.out.println("polling " + watchEvents.size() + " events");
                    watchEvents.forEach(watchEvent -> System.out.println(watchEvent.kind()));

                    if (key != null)
                        key.reset();
//                    List<WatchEvent<?>> watchEvents = register.pollEvents();

                }

            } catch (InterruptedException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            } finally {

            }
//        });
        });

        watcherThread.start();
//        watcherThread.join();


//        scheduledExecutorService.shutdown();
    }
}
