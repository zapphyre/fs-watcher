package fs.watcher.pojo;

import java.nio.file.Path;
import java.nio.file.WatchEvent;

public record Change(WatchEvent.Kind<?> kind, Path path) {
}
