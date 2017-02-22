package connection;

import java.util.Collection;
import utilities.synchronization.SyncEntry;
import utilities.synchronization.Synchronizer;

public interface GameConnection {

    void create(Collection<Synchronizer> cts);

    void destroy(Collection<Synchronizer> cts);

    void update(Collection<SyncEntry> cts);
}
