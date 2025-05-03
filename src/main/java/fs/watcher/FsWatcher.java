package fs.watcher;

import fs.watcher.pipeline.PathWatcher;
import fs.watcher.pojo.Change;

import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.function.Predicate;

public final class FsWatcher {
    private static WatchService watcher;
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(FsWatcher::teardown));
    }

    public static synchronized PathWatcher watch(Path dirOrFile) {
        return events -> callback -> {
            Path watchingDir = Files.isDirectory(dirOrFile) ?
                    dirOrFile : dirOrFile.getParent();

            executor.submit(() -> {
                try {
                    watcher = FileSystems.getDefault().newWatchService();

                    WatchKey register = watchingDir.register(watcher, events);
                } catch (IOException e) {
                    System.out.println("Failed to register watcher for path " + dirOrFile);
                    throw new RuntimeException(e);
                }

                while (!executor.isTerminated()) {
                    WatchKey key = null;

                    try {
                        key = watcher.take();

                        key.pollEvents().stream()
                                .filter(contextIsPath)
                                .map(changeOverWatched(watchingDir))
                                .filter(isChangeEventRelevant(dirOrFile))
                                .forEach(callback::changed);

                        key.reset();
                    } catch (InterruptedException e) {
                        System.out.println("Watcher thread interrupted");
                        e.printStackTrace();
                    }
                }
            });

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
            if (watcher != null)
                watcher.close();
        } catch (IOException e) {
            System.out.println("error while closing watcher");
            e.printStackTrace();
        }

        if (executor != null) {
            executor.shutdownNow();
        }
    }
}