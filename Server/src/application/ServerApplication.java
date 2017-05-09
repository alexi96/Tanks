package application;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.math.Vector3f;
import com.jme3.system.JmeContext;
import connection.ControlsConnection;
import connection.GameConnection;
import controllers.GameController;
import controls.TestBall;
import controls.maps.HillMap;
import controls.maps.Map;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import rpc.HiRpc;
import synchronization.SyncManager;
import utilities.ControlsAppState;
import utilities.LoadingManager;

public class ServerApplication extends SimpleApplication {

    public static void main(String[] args) {
        ServerApplication s = new ServerApplication();
        s.start(JmeContext.Type.Headless);

        s.enqueue(() -> {
        SyncManager m = GameController.getInstance().getSynchronizer();

            TestBall tb = new TestBall();
            m.create(tb);
            tb.move(Vector3f.UNIT_Y.mult(17));
        });
    }

    @Override
    public void simpleInitApp() {
        BulletAppState bulletState = new BulletAppState();
        LoadingManager loader = new LoadingManager(this.assetManager);
        super.stateManager.attach(bulletState);

        ControlsAppState s = new ControlsAppState();

        try {
            HiRpc.start(s, GameConnection.PORT, s, new Class[]{ControlsConnection.class, GameConnection.class});
        } catch (IOException ex) {
            Logger.getLogger(ServerApplication.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }

        GameController.getInstance().initialise(this, super.settings, loader, bulletState.getPhysicsSpace(), s);

        Map t = new HillMap();
        s.create(t);

        super.stateManager.attach(s);

        GameController.getInstance().getDeathSubject().addListener((p) -> System.out.println(p.getName() + " died!"));
    }
}
