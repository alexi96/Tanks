package utilities.synchronization;

import java.util.TreeSet;

public class SyncManager {

    private int last;
    protected TreeSet<Synchronizer> existent = new TreeSet<>();
    protected TreeSet<Synchronizer> created = new TreeSet<>();
    protected TreeSet<Integer> destroyed = new TreeSet<>();
    protected TreeSet<SyncEntry> updated = new TreeSet<>();

    public void create(Synchronizer c) {
        c.setId(last);
        ++last;
        this.created.add(c);
        this.existent.add(c);
    }

    public void destroy(Synchronizer c) {
        this.destroyed.add(c.getId());
        this.existent.remove(c);
    }

    public void update(Synchronizer c, String name) {
        this.updated.add(new SyncEntry(c, name));
    }
}
