package fs.watcher;

import fs.watcher.pipeline.EventClosure;
import fs.watcher.pojo.Change;

import java.nio.file.*;


public class FsWatcher {

    EventClosure watch(Path dirOrFile) {
        return events -> callback -> {
            WatchService watcher = FileSystems.getDefault().newWatchService();
            Path watchingDir = Files.isDirectory(dirOrFile) ?
                    dirOrFile : dirOrFile.getParent();

            WatchKey register = watchingDir.register(watcher, events);

            Thread watcherThread = new Thread(() -> {
                while (true) {
                    WatchKey key = null;

                    try {
                        key = watcher.take();
                        Thread.sleep(21);

                        key.pollEvents().stream()
                                .filter(q -> q.context() instanceof Path)
                                .map(q -> new Change(q.kind(), watchingDir.resolve((Path) q.context())))
                                .filter(q -> Files.isDirectory(dirOrFile) ?
                                       q.path().getParent().equals(dirOrFile) : dirOrFile.equals(q.path())
                                )
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

            Runtime.getRuntime().addShutdownHook(new Thread(watcherThread::interrupt));

            return watcherThread::interrupt;
        };

    }
}
