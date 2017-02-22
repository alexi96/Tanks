package application;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.bullet.BulletAppState;
import com.jme3.system.JmeContext;
import connection.GameConnection;
import controllers.GameController;
import controls.TestBall;
import controls.TestMap;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import rpc.HiRpc;
import utilities.LoadingManager;
import utilities.Server;

public class ServerApplication extends SimpleApplication {

    public static void main(String[] args) {
        ServerApplication s = new ServerApplication();
        s.start(JmeContext.Type.Headless);
    }

    @Override
    public void simpleUpdate(float tpf) {
        GameController.getInstance().getServer().update();
    }
    
    @Override
    public void simpleInitApp() {
        Server s = new Server();

        try {
            HiRpc.start(null, 4321, s, new Class[]{GameConnection.class});
        } catch (IOException ex) {
            Logger.getLogger(ServerApplication.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }

        assetManager.registerLocator("../Library/assets", FileLocator.class);
        BulletAppState bulletState = new BulletAppState();
        LoadingManager loader = new LoadingManager(this.assetManager);
        super.stateManager.attach(bulletState);
        GameController.getInstance().initialise(this, loader, bulletState.getPhysicsSpace(), s);

        TestMap t = new TestMap();
        s.create(t);
        s.create(new TestBall());
    }
}
