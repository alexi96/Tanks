package synchronization;

import java.util.TreeSet;

public class SyncManager {

    private int last;
    protected final TreeSet<Synchronizer> managed = new TreeSet<>();
    protected final TreeSet<Synchronizer> created = new TreeSet<>();
    protected final TreeSet<Integer> destroyed = new TreeSet<>();
    protected final TreeSet<SyncEntry> updated = new TreeSet<>();

    public void create(Synchronizer c) {
        c.setId(last);
        ++last;
        this.created.add(c);
        this.managed.add(c);
    }

    public void destroy(Synchronizer c) {
        this.destroyed.add(c.getId());
        this.managed.remove(c);
    }

    public void update(Synchronizer c, String name) {
        this.updated.add(new SyncEntry(c, name));
    }
    
    public void update(Synchronizer c) {
        this.updated.add(new SyncEntry(c, "synchronize"));
    }
    
    public void update() {
    }
}
