package controls.props;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Spatial;
import controllers.GameController;
import java.util.Random;
import synchronization.SyncManager;

public class BoxControl extends PropControl {

    protected static final Random RAND = new Random();

    public BoxControl() {
        super(200);
    }

    public BoxControl(float health) {
        super(health);
    }

    public Vector3f getLocation() {
        return location;
    }

    public Quaternion getRotation() {
        return rotation;
    }

    @Override
    public void create() {
        Spatial box = PropControl.PROPS.getChild("Box").clone();

        box.setLocalTranslation(this.location);
        box.setLocalRotation(this.rotation);

        SyncManager sm = GameController.getInstance().getSynchronizer();
        if (sm == null) {
            box.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        }

        box.addControl(this);

        GameController.getInstance().getApplication().getRootNode().attachChild(box);
    }

    @Override
    protected void die() {
        SyncManager sm = GameController.getInstance().getSynchronizer();

        if (sm != null) {
            for (int i = 1; i <= 5; i++) {
                PlankControl plank = new PlankControl();
                plank.location = this.location.clone();
                float[] angs = new float[3];
                angs[0] = RAND.nextInt(360) * FastMath.DEG_TO_RAD;
                angs[1] = RAND.nextInt(360) * FastMath.DEG_TO_RAD;
                angs[2] = RAND.nextInt(360) * FastMath.DEG_TO_RAD;

                plank.rotation = new Quaternion(angs);

                sm.create(plank);
                
                RigidBodyControl rbc = plank.getSpatial().getControl(RigidBodyControl.class);
                rbc.applyImpulse(plank.rotation.getRotationColumn(0).mult(50), Vector3f.ZERO);
            }
        }

        super.die();
    }

    @Override
    public void setSpatial(Spatial spatial) {
        GameController gc = GameController.getInstance();

        if (gc.getSynchronizer() != null) {
            if (spatial != null) {
                RigidBodyControl rbc = new RigidBodyControl(200);
                spatial.addControl(rbc);

                gc.getPhysics().add(rbc);
            } else {
                RigidBodyControl rbc = super.spatial.getControl(RigidBodyControl.class);
                super.spatial.removeControl(rbc);

                gc.getPhysics().remove(rbc);
            }
        }

        super.setSpatial(spatial);
    }
}
