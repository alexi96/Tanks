package utilities;

import connection.GameConnection;
import controls.NetworkControl;
import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import rpc.ConnectionHandler;

public class Server implements ConnectionHandler {

    private int last;
    private ArrayList<GameConnection> clients = new ArrayList<>();
    private TreeSet<NetworkControl> existent = new TreeSet<>();
    private TreeSet<NetworkControl> created = new TreeSet<>();
    private TreeSet<NetworkControl> updated = new TreeSet<>();
    private TreeSet<NetworkControl> destroyed = new TreeSet<>();

    public void update(NetworkControl nc) {
        this.updated.add(nc);
    }

    public void create(NetworkControl c) {
        c.setId(last);
        ++last;
        this.created.add(c);
        this.existent.add(c);
        c.create(true);
    }

    public void destroy(NetworkControl c) {
        this.destroyed.add(c);
        this.existent.remove(c);
    }

    public void update() {
        if (!this.created.isEmpty()) {
            for (GameConnection c : this.clients) {
                c.update(this.created);
            }
            this.created.clear();
        }
        if (!this.destroyed.isEmpty()) {
            for (GameConnection c : this.clients) {
                c.destroy(this.destroyed);
            }
            this.destroyed.clear();
        }
        if (!this.updated.isEmpty()) {
            for (GameConnection c : this.clients) {
                c.update(this.updated);
            }
            this.updated.clear();
        }
    }

    @Override
    public void connected(Object proc) throws Exception {
        GameConnection con = (GameConnection) proc;
        con.create(this.existent);
        this.clients.add(con);
    }
}
