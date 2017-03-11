package synchronization;

import java.util.TreeSet;

public class SyncManager {

    private int last;
    protected final TreeSet<Synchronizer> managed = new TreeSet<>();
    protected final TreeSet<Synchronizer> created = new TreeSet<>();
    protected final TreeSet<Integer> destroyed = new TreeSet<>();
    protected final TreeSet<Synchronizer> updated = new TreeSet<>();

    public void create(Synchronizer c) {
        c.setId(last);
        ++last;
        this.created.add(c);
        this.managed.add(c);
        c.create();
    }

    public void destroy(Synchronizer c) {
        this.destroyed.add(c.getId());
        this.managed.remove(c);
        c.destroy();
    }

    public void update(Synchronizer c) {
        this.updated.add(c);
    }
    
    public void update() {
    }
}
