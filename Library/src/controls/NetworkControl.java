package controls;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class NetworkControl extends GameControl implements Serializable, Comparable<NetworkControl>, Cloneable {

    protected int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public abstract void create(boolean server);

    public void destroy(boolean server) {
        super.spatial.removeFromParent();
    }

    public void updateClient() {
    }

    public void update() {
    }

    @Override
    public int compareTo(NetworkControl o) {
        return this.id - o.id;
    }

    @Override
    public NetworkControl clone() {
        try {
            return (NetworkControl) super.clone();
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(NetworkControl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
