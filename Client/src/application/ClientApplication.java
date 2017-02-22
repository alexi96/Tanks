package application;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.FileLocator;
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

    public static void main(String[] args) {
        ClientApplication app = new ClientApplication();
        app.start();
    }
}
