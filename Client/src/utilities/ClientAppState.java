package utilities;

import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import connection.GameConnection;
import controls.NetworkControl;
import java.util.Collection;
import java.util.HashMap;
import java.util.TreeSet;

public class ClientAppState extends AbstractAppState implements GameConnection {
    private HashMap<Integer, NetworkControl> managed = new HashMap<>();
    private TreeSet<NetworkControl> creator = new TreeSet<>();
    private TreeSet<NetworkControl> destroyer = new TreeSet<>();
    private TreeSet<NetworkControl> updater = new TreeSet<>();

    @Override
    public void stateAttached(AppStateManager stateManager) {
    }

    @Override
    public void stateDetached(AppStateManager stateManager) {
    }

    @Override
    public void update(float tpf) {
        if (!this.creator.isEmpty()) {
            for (NetworkControl c : this.creator) {
                c.create(false);
                c.updateClient();
                this.updater.remove(c);
                this.managed.put(c.getId(), c);
            }
            this.creator.clear();
        }
        if (!this.destroyer.isEmpty()) {
            for (NetworkControl c : this.destroyer) {
                NetworkControl s = this.managed.get(c.getId());
                s.destroy(false);
                this.managed.remove(c.getId());
            }
            this.destroyer.clear();
        }
        if (!this.updater.isEmpty()) {
            for (NetworkControl c : this.updater) {
                NetworkControl s = this.managed.get(c.getId());
                c.setSpatial(s.getSpatial());
                c.updateClient();
            }
            this.updater.clear();
        }
    }
    
    @Override
    public void create(Collection<NetworkControl> cts) {
        this.creator.addAll(cts);
    }

    @Override
    public void destroy(Collection<NetworkControl> cts) {
        this.destroyer.addAll(cts);
    }

    @Override
    public void update(Collection<NetworkControl> cts) {
        this.updater.addAll(cts);
    }
}
