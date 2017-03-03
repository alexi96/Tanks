package synchronization;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class Synchronizer implements Comparable<Synchronizer>, Serializable {

    protected int id;

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

    /**
     * Changes the nontransient fields of this instance with the nontransient
     * fields in the newData.
     *
     * @param newData the object with changes
     */
    public void prepare(Synchronizer newData) {
        try {
            Class tc = super.getClass();
            Class nc = newData.getClass();
            if (!tc.equals(nc)) {
                return;
            }
            Field[] fs = tc.getDeclaredFields();
            for (Field of : fs) {
                if (Modifier.isStatic(of.getModifiers())) {
                    continue;
                }
                if (Modifier.isTransient(of.getModifiers())) {
                    continue;
                }
                if (Modifier.isFinal(of.getModifiers())) {
                    continue;
                }
                of.setAccessible(true);
                Object o = of.get(newData);
                of.set(this, o);
            }
        } catch (SecurityException | IllegalArgumentException | IllegalAccessException ex) {
            Logger.getLogger(Synchronizer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public int compareTo(Synchronizer o) {
        return this.id - o.id;
    }
}
