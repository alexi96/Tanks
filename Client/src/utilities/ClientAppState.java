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
import utilities.synchronization.SyncEntry;
import utilities.synchronization.Synchronizer;

public class ClientAppState extends AbstractAppState implements GameConnection {

    private HashMap<Integer, Synchronizer> managed = new HashMap<>();
    private TreeSet<Synchronizer> created = new TreeSet<>();
    private TreeSet<SyncEntry> updated = new TreeSet<>();

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
    }

    public void update(Synchronizer n, Synchronizer o, String name) {
        try {
            Method com = n.getClass().getDeclaredMethod(name);
            com.setAccessible(true);
            
            n.prepare(o);
            com.invoke(n);
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
        for (Integer s : cts) {
            this.managed.remove(s);
        }
    }

    @Override
    public void update(Collection<SyncEntry> cts) {
        this.updated.addAll(cts);
    }
}
