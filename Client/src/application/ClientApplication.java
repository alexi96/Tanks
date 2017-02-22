package application;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.system.AppSettings;
import controllers.GameController;
import java.util.logging.Level;
import java.util.logging.Logger;
import rpc.HiRpc;
import utilities.ClientAppState;
import utilities.LoadingManager;

public class ClientApplication extends SimpleApplication {

    @Override
    public void simpleInitApp() {
        assetManager.registerLocator("../Library/assets", FileLocator.class);

        LoadingManager loader = new LoadingManager(this.assetManager);
        GameController.getInstance().initialise(this, loader, null, null);
        try {
            ClientAppState state = new ClientAppState();
            HiRpc.connectReverse("localhost", 4321, state);
            super.stateManager.attach(state);
        } catch (Exception ex) {
            Logger.getLogger(ClientApplication.class.getName()).log(Level.SEVERE, null, ex);
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
        app.start();
    }
}
