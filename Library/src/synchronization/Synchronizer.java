package synchronization;

import java.io.Serializable;

public abstract class Synchronizer implements Comparable<Synchronizer>, Serializable {

    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void create() {
    }
    
    public void synchronize() {
    }
    
    public void destroy() {
    }
    
    public void prepare(Synchronizer s) {
    }

    @Override
    public int compareTo(Synchronizer o) {
        return this.id - o.id;
    }
}
