package application;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.system.AppSettings;
import connection.GameConnection;
import controllers.GameController;
import controls.TestBall;
import controls.TestMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import rpc.HiRpc;
import synchronization.SyncManager;
import utilities.ClientAppState;
import utilities.LoadingManager;

public class ClientApplication extends SimpleApplication {

    String ip;

    @Override
    public void simpleInitApp() {
        super.cam.setFrustumPerspective(45, (float) super.settings.getWidth() / super.settings.getHeight(), 0.01f, 1000);

        LoadingManager loader = new LoadingManager(this.assetManager);
        GameController.getInstance().initialise(this, loader, null, null);

        if (this.ip != null) {
            try {
                ClientAppState state = new ClientAppState();
                HiRpc.connectReverse(ip, GameConnection.PORT, state);
                super.stateManager.attach(state);
            } catch (Exception ex) {
                Logger.getLogger(ClientApplication.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            BulletAppState bulletState = new BulletAppState();
            super.stateManager.attach(bulletState);

            GameController.getInstance().initialise(this, loader, bulletState.getPhysicsSpace(), new SyncManager());

            new TestMap().create();
            new TestBall().create();
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        System.exit(0);
    }

    public static void main(String[] args) {
        ClientApplication app = new ClientApplication();
        boolean debug = args.length > 0 && args[0].equalsIgnoreCase("debug");
        if (debug) {
            AppSettings set = new AppSettings(true);
            set.setResolution(800, 800);
            app.setSettings(set);
            app.setShowSettings(false);
        } else {
            app.setDisplayFps(false);
            app.setDisplayStatView(false);
        }
        app.ip = null;
        if (args.length > 0 && !debug) {
            app.ip = args[0];
        }
        app.start();
    }
}
