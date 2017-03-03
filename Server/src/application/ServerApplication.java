package application;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.jme3.system.JmeContext;
import connection.ControlsConnection;
import connection.GameConnection;
import controllers.GameController;
import controls.TestBall;
import controls.maps.TestMap;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import rpc.HiRpc;
import utilities.ControlsAppState;
import utilities.LoadingManager;

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

        ControlsAppState s = new ControlsAppState();

        try {
            HiRpc.start(s, GameConnection.PORT, s, new Class[]{ControlsConnection.class, GameConnection.class});
        } catch (IOException ex) {
            Logger.getLogger(ServerApplication.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        super.stateManager.attach(s);

        GameController.getInstance().initialise(this, super.settings, loader, bulletState.getPhysicsSpace(), s);

        TestMap t = new TestMap();
        s.create(t);

        TestBall tb = new TestBall();
        s.create(tb);
        Spatial sp = tb.getSpatial();
        sp.getControl(RigidBodyControl.class).setPhysicsLocation(Vector3f.UNIT_Y.mult(3).add(Vector3f.UNIT_X.mult(3)));
    }
}
