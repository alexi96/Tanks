package controllers;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.PhysicsSpace;
import utilities.LoadingManager;
import synchronization.SyncManager;

public final class GameController {

    private static final GameController instance = new GameController();
    private SimpleApplication application;
    private PhysicsSpace physics;
    private LoadingManager loader;
    private SyncManager synchronizer = new SyncManager();
    private boolean bestVisualStyles = true;

    private GameController() {
    }

    public SimpleApplication getApplication() {
        return application;
    }

    public PhysicsSpace getPhysics() {
        return physics;
    }

    public LoadingManager getLoader() {
        return loader;
    }

    public SyncManager getSynchronizer() {
        return synchronizer;
    }

    public boolean isBestVisualStyles() {
        return bestVisualStyles;
    }

    public void setBestVisualStyles(boolean bestVisualStyles) {
        this.bestVisualStyles = bestVisualStyles;
    }

    public void initialise(SimpleApplication application, LoadingManager loader, PhysicsSpace physics, SyncManager synchronizer) {
        this.application = application;
        this.loader = loader;
        this.physics = physics;
        this.synchronizer = synchronizer;
    }

    public static GameController getInstance() {
        return instance;
    }
}
