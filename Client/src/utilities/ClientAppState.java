package utilities;

import com.jme3.app.state.AbstractAppState;
import connection.GameConnection;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import synchronization.SyncEntry;
import synchronization.Synchronizer;

public class ClientAppState extends AbstractAppState implements GameConnection {

    private final HashMap<Integer, Synchronizer> managed = new HashMap<>();
    private final TreeSet<Synchronizer> created = new TreeSet<>();
    private final TreeSet<SyncEntry> updated = new TreeSet<>();
    private final TreeSet<Integer> destroyed = new TreeSet<>();

    @Override
    public void update(float tpf) {
        if (!this.created.isEmpty()) {
            for (Synchronizer c : this.created) {
                c.create();
                this.managed.put(c.getId(), c);
            }
            this.created.clear();
        }
        if (!this.updated.isEmpty()) {
            for (SyncEntry se : this.updated) {
                Synchronizer s = this.managed.get(se.getSynch().getId());
                this.update(se.getSynch(), s, se.getCommand());
            }
            this.updated.clear();
        }
        if (!this.destroyed.isEmpty()) {
            for (Synchronizer s : this.created) {
                Synchronizer t = this.managed.get(s.getId());
                t.destroy();
            }
            this.created.clear();
        }
    }

    private void update(Synchronizer n, Synchronizer o, String name) {
        try {
            Method com = n.getClass().getDeclaredMethod(name);
            com.setAccessible(true);

            o.prepare(n);
            com.invoke(o);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(ClientAppState.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void create(Collection<Synchronizer> cts) {
        this.created.addAll(cts);
    }

    @Override
    public void destroy(Collection<Integer> cts) {
        this.destroyed.addAll(cts);
    }

    @Override
    public void update(Collection<SyncEntry> cts) {
        this.updated.addAll(cts);
    }
}
