package fs.watcher.pipeline;

@FunctionalInterface
public interface WatchDestroyer {

    void halt();
}
