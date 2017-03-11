package utilities;

import com.jme3.app.state.AbstractAppState;
import connection.GameConnection;
import java.util.Collection;
import java.util.HashMap;
import java.util.TreeSet;
import synchronization.Synchronizer;

public class ClientAppState extends AbstractAppState implements GameConnection {

    protected final HashMap<Integer, Synchronizer> managed = new HashMap<>();
    protected final TreeSet<Synchronizer> created = new TreeSet<>();
    protected final TreeSet<Synchronizer> updated = new TreeSet<>();
    protected final TreeSet<Integer> destroyed = new TreeSet<>();

    @Override
    public synchronized void update(float tpf) {
        if (!this.created.isEmpty()) {
            for (Synchronizer c : this.created) {
                if (this.managed.containsKey(c.getId())) {
                    continue;
                }
                this.managed.put(c.getId(), c);
                c.create();
            }
            this.created.clear();
        }
        if (!this.updated.isEmpty()) {
            for (Synchronizer se : this.updated) {
                Synchronizer old = this.managed.get(se.getId());
                this.update(se, old);
            }
            this.updated.clear();
        }
        if (!this.destroyed.isEmpty()) {
            for (Integer s : this.destroyed) {
                Synchronizer t = this.managed.get(s);
                t.destroy();
            }
            this.destroyed.clear();
        }
    }

    protected void update(Synchronizer n, Synchronizer o) {
        o.prepare(n);
        o.synchronize();
    }

    @Override
    public synchronized void create(Collection<Synchronizer> cts) {
        this.created.addAll(cts);
    }

    @Override
    public synchronized void destroy(Collection<Integer> cts) {
        this.destroyed.addAll(cts);
    }

    @Override
    public synchronized void update(Collection<Synchronizer> cts) {
        this.updated.addAll(cts);
    }
}
