package fs.watcher.pipeline;

import java.nio.file.WatchEvent;

@FunctionalInterface
public interface PathWatcher {

    EventsOnPath forEvents(WatchEvent.Kind<?> ...kinds);
}
