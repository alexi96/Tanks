package controllers;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.system.AppSettings;
import controls.entityes.PlayerControl;
import utilities.LoadingManager;
import synchronization.SyncManager;
import utilities.observer.ObserverSubject;
import utilities.observer.ScoreObserverSubject;

public final class GameController {

    private static final GameController instance = new GameController();
    private SimpleApplication application;
    private AppSettings settings;
    private PhysicsSpace physics;
    private LoadingManager loader;
    private SyncManager synchronizer = new SyncManager();
    private final ObserverSubject<PlayerControl> deathSubject = new ObserverSubject<>();
    private final ScoreObserverSubject scoreSubject = new ScoreObserverSubject();

    private GameController() {
    }

    public SimpleApplication getApplication() {
        return application;
    }

    public AppSettings getSettings() {
        return settings;
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

    public ObserverSubject<PlayerControl> getDeathSubject() {
        return deathSubject;
    }

    public ScoreObserverSubject getScoreSubject() {
        return scoreSubject;
    }

    
    public void initialise(SimpleApplication application, AppSettings settings, LoadingManager loader, PhysicsSpace physics, SyncManager synchronizer) {
        this.application = application;
        this.loader = loader;
        this.settings = settings;
        this.physics = physics;
        this.synchronizer = synchronizer;
    }

    public void configConnection(SyncManager synchronizer) {
        this.synchronizer = synchronizer;
    }

    public static GameController getInstance() {
        return instance;
    }
}
