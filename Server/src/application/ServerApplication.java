package application;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.system.JmeContext;
import connection.GameConnection;
import controllers.GameController;
import controls.TestBall;
import controls.maps.TestMap;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import rpc.HiRpc;
import utilities.LoadingManager;
import utilities.ServerAppState;

public class ServerApplication extends SimpleApplication {

    public static void main(String[] args) {
        ServerApplication s = new ServerApplication();
        s.start(JmeContext.Type.Headless);
    }

    @Override
    public void simpleInitApp() {
        BulletAppState bulletState = new BulletAppState();
        LoadingManager loader = new LoadingManager(this.assetManager);
        super.stateManager.attach(bulletState);

        ServerAppState s = new ServerAppState();

        try {
            HiRpc.start(null, GameConnection.PORT, s, new Class[]{GameConnection.class});
        } catch (IOException ex) {
            Logger.getLogger(ServerApplication.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        super.stateManager.attach(s);

        GameController.getInstance().initialise(this, loader, bulletState.getPhysicsSpace(), s);

        TestMap t = new TestMap();
        s.create(t);
        
        s.create(new TestBall());
    }
}
