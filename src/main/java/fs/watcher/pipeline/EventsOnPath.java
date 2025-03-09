package fs.watcher.pipeline;

import java.io.IOException;

@FunctionalInterface
public interface EventsOnPath {

    WatchDestroyer onChange(WatcherCallback callback) throws IOException;
}
