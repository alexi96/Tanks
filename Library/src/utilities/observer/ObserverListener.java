package utilities.observer;

@FunctionalInterface
public interface ObserverListener<D> {

    void changedState(D data);
}
