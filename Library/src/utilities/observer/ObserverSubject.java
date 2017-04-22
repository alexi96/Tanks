package utilities.observer;

import java.util.ArrayList;

public class ObserverSubject<D> {
    private final ArrayList<ObserverListener<D>> listeners = new ArrayList<>();
    
    public void changeState(D data) {
        this.listeners.forEach((l) -> l.changedState(data));
    }

    public boolean addListener(ObserverListener<D> e) {
        return listeners.add(e);
    }

    public boolean removeListener(ObserverListener<D> o) {
        return listeners.remove(o);
    }
}
