package fs.watcher.pipeline;

import java.io.IOException;

@FunctionalInterface
public interface EvtCallbackClosure {

    WatchDestroyer onChange(WatcherCallback callback) throws IOException;
}
