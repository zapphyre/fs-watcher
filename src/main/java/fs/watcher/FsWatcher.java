package fs.watcher;

import fs.watcher.pipeline.PathWatcher;
import fs.watcher.pojo.Change;

import java.io.IOException;
import java.nio.file.*;
import java.util.function.Function;
import java.util.function.Predicate;

public final class FsWatcher {
    private static WatchService watcher;
    private static Thread watcherThread;

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(FsWatcher::teardown));
    }

    public static PathWatcher watch(Path dirOrFile) {
        return events -> callback -> {
            watcher = FileSystems.getDefault().newWatchService();
            Path watchingDir = Files.isDirectory(dirOrFile) ?
                    dirOrFile : dirOrFile.getParent();

            WatchKey register = watchingDir.register(watcher, events);

            watcherThread = new Thread(() -> {
                while (true) {
                    WatchKey key = null;

                    try {
                        key = watcher.take();

                        key.pollEvents().stream()
                                .filter(contextIsPath)
                                .map(changeOverWatched(watchingDir))
                                .filter(isChangeEventRelevant(dirOrFile))
                                .forEach(callback::changed);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        if (key != null)
                            key.reset();
                    }
                }

            });

            watcherThread.start();

            return FsWatcher::teardown;
        };
    }

    static Predicate<WatchEvent<?>> contextIsPath = q -> q.context() instanceof Path;

    static Function<WatchEvent<?>, Change> changeOverWatched(Path dir) {
        return q -> new Change(q.kind(), dir.resolve((Path) q.context()));
    }

    static Predicate<Change> isChangeEventRelevant(Path path) {
        return isWatchingFile(path).or(isWatchingWholeDir(path));
    }

    static Predicate<Change> isWatchingWholeDir(Path dirOrFile) {
        return q -> q.path().getParent().equals(dirOrFile);
    }

    static Predicate<Change> isWatchingFile(Path dirOrFile) {
        return q -> q.path().equals(dirOrFile);
    }

    private static void teardown() {
        System.out.println("winding down FS watcher");
        try {
            if (watcher != null) {
                watcher.take(); //need to take last or it errors
                watcher.close();
            }

            if (watcherThread.isAlive())
                watcherThread.interrupt();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
