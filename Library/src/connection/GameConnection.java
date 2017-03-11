package connection;

import java.util.Collection;
import synchronization.Synchronizer;

public interface GameConnection {
    int PORT = 2048;

    void create(Collection<Synchronizer> cts);

    void destroy(Collection<Integer> cts);

    void update(Collection<Synchronizer> cts);
}
