package application;

import com.jme3.app.SimpleApplication;
import com.jme3.system.AppSettings;
import connection.GameConnection;
import controllers.GameController;
import java.util.logging.Level;
import java.util.logging.Logger;
import rpc.HiRpc;
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
                HiRpc.connectReverse("localhost", GameConnection.PORT, state);
                super.stateManager.attach(state);
            } catch (Exception ex) {
                Logger.getLogger(ClientApplication.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        System.exit(0);
    }

    public static void main(String[] args) {
        ClientApplication app = new ClientApplication();
        if (args.length > 0 && args[0].equalsIgnoreCase("debug")) {
            AppSettings set = new AppSettings(true);
            set.setResolution(800, 800);
            app.setSettings(set);
            app.setShowSettings(false);
        } else {
            app.setDisplayFps(false);
            app.setDisplayStatView(false);
        }
        app.ip = null;
        if (args.length > 0) {
            app.ip = args[0];
        }
        app.start();
    }
}
