package fs.watcher.pipeline;

import java.nio.file.Path;
import java.nio.file.WatchEvent;

public interface FsEventCallback {

    void change(Path path, WatchEvent<?> watchEvent);
}
