package utilities;

import synchronization.SyncManager;
import com.jme3.app.Application;
import com.jme3.app.state.AppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.renderer.RenderManager;
import connection.GameConnection;
import java.util.ArrayList;
import rpc.ConnectionHandler;

public class ServerAppState extends SyncManager implements ConnectionHandler, AppState {

    protected boolean initialized = false;
    private boolean enabled = true;
    private ArrayList<GameConnection> clients = new ArrayList<>();

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        initialized = true;
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void stateAttached(AppStateManager stateManager) {
    }

    @Override
    public void stateDetached(AppStateManager stateManager) {
    }

    @Override
    public void update(float tpf) {
        this.update();
    }

    @Override
    public void render(RenderManager rm) {
    }

    @Override
    public void postRender() {
    }

    @Override
    public void cleanup() {
        initialized = false;
    }

    @Override
    public void update() {
        ArrayList<GameConnection> lost = new ArrayList<>();
        if (!this.created.isEmpty()) {
            for (GameConnection c : this.clients) {
                try {
                    c.create(this.created);
                } catch (Exception e) {
                    lost.add(c);
                    System.out.println("Lost: ");
                }
            }
            this.created.clear();
        }
        this.clients.removeAll(lost);
        lost.clear();

        if (!this.destroyed.isEmpty()) {
            for (GameConnection c : this.clients) {
                try {
                    c.destroy(this.destroyed);
                } catch (Exception e) {
                    lost.add(c);
                    System.out.println("Lost: ");
                }
            }
            this.destroyed.clear();
        }
        this.clients.removeAll(lost);
        lost.clear();

        if (!this.updated.isEmpty()) {
            for (GameConnection c : this.clients) {
                try {
                    c.update(this.updated);
                } catch (Exception e) {
                    lost.add(c);
                    System.out.println("Lost: ");
                }
            }
            this.updated.clear();
        }
        this.clients.removeAll(lost);
        lost.clear();
    }

    @Override
    public void connected(Object proc) throws Exception {
        System.out.println("Connected");
        GameConnection con = (GameConnection) proc;
        con.create(this.managed);
        this.clients.add(con);
    }
}
