package controls;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Spatial;
import controllers.GameController;
import synchronization.SyncManager;

public class TestBall extends DestroyableControl {

    private Vector3f loc;
    private Quaternion rot;

    @Override
    public void create() {
        GameController gc = GameController.getInstance();
        Spatial s = gc.getLoader().loadModel("Models/Ball.j3o");
        s.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);

        gc.getApplication().getRootNode().attachChild(s);

        s.addControl(this);

        boolean server = GameController.getInstance().getSynchronizer() != null;
        if (!server) {
            return;
        }

        RigidBodyControl rbc = new RigidBodyControl(300);
        s.addControl(rbc);
        gc.getPhysics().add(rbc);
    }

    @Override
    public void setSpatial(Spatial spatial) {
        if (spatial != null) {
            this.loc = spatial.getLocalTranslation();
            this.rot = spatial.getLocalRotation();
        }

        super.setSpatial(spatial);
    }

    @Override
    public void hit(float dmg, Vector3f dir, Vector3f loc) {
        RigidBodyControl rbc = super.spatial.getControl(RigidBodyControl.class);
        rbc.applyImpulse(dir.mult(dmg), loc);
    }

    @Override
    public void synchronize() {
        super.spatial.setLocalTranslation(this.loc);
        super.spatial.setLocalRotation(this.rot);
    }

    @Override
    public void update(float tpf) {
        SyncManager s = GameController.getInstance().getSynchronizer();
        if (s == null) {
            return;
        }
        s.update(this);
    }

    @Override
    public String toString() {
        return this.loc.toString();
    }
}
