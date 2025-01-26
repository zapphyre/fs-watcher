package fs.watcher.pipeline;

import java.nio.file.WatchEvent;

@FunctionalInterface
public interface EventClosure {

    EvtCallbackClosure forEvents(WatchEvent.Kind<?> ...kinds);
}
