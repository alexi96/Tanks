package controls;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.light.AmbientLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import controllers.GameController;
import utilities.synchronization.SyncManager;

public class TestBall extends SyncGameControl {

    private Transform tr = new Transform();

    @Override
    public void create() {
        GameController gc = GameController.getInstance();
        Spatial s = gc.getLoader().loadModel("Models/Ball.j3o");
        gc.getApplication().getRootNode().attachChild(s);

        s.addControl(this);
        s.setLocalTranslation(Vector3f.UNIT_Y.mult(5));

        boolean server = GameController.getInstance().getSynchronizer() != null;
        if (!server) {
            AmbientLight ambient = new AmbientLight();
            ambient.setColor(ColorRGBA.White);
            s.addLight(ambient);
            return;
        }

        RigidBodyControl rbc = new RigidBodyControl(10);
        s.addControl(rbc);
        gc.getPhysics().add(rbc);
    }

    public void synchronizeTransform() {
        super.spatial.setLocalTransform(this.tr);
    }

    @Override
    public void update(float tpf) {
        SyncManager s = GameController.getInstance().getSynchronizer();
        if (s == null) {
            return;
        }
        RigidBodyControl rbc = super.spatial.getControl(RigidBodyControl.class);
        rbc.applyCentralForce(Vector3f.UNIT_XYZ.mult(tpf));
        this.tr = super.spatial.getLocalTransform().clone();
        s.update(this, "synchronizeTransform");
    }

    @Override
    public String toString() {
        return this.tr.toString();
    }
}
