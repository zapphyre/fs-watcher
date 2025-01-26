package fs.watcher.pipeline;

import fs.watcher.pojo.Change;

import java.nio.file.Path;
import java.nio.file.WatchEvent;

@FunctionalInterface
public interface WatcherCallback {

    void changed(Change change);
}
