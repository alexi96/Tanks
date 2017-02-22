package controllers;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.PhysicsSpace;
import utilities.LoadingManager;
import utilities.Server;

public final class GameController {

    private static final GameController instance = new GameController();
    private SimpleApplication application;
    private PhysicsSpace physics;
    private LoadingManager loader;
    
    private Server server = new Server();

    public SimpleApplication getApplication() {
        return application;
    }

    public PhysicsSpace getPhysics() {
        return physics;
    }

    public LoadingManager getLoader() {
        return loader;
    }

    public Server getServer() {
        return server;
    }

    private GameController() {
    }

    public void initialise(SimpleApplication application, LoadingManager loader, PhysicsSpace physics, Server server) {
        this.application = application;
        this.loader = loader;
        this.physics = physics;
        this.server = server;
    }

    public static GameController getInstance() {
        return instance;
    }
}
