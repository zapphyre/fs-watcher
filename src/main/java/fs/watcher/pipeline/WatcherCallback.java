package fs.watcher.pipeline;

import fs.watcher.pojo.Change;

@FunctionalInterface
public interface WatcherCallback {

    void changed(Change change);
}
