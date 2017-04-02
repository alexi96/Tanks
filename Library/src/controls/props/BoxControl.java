package controls.props;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import controllers.GameController;
import controls.DestroyableControl;
import synchronization.SyncManager;
import synchronization.Synchronizer;

public class BoxControl extends DestroyableControl {

    protected static final Node PROPS = (Node) GameController.getInstance().getApplication().getAssetManager().loadModel("Models/Props.j3o");

    protected Vector3f location = new Vector3f();
    protected Quaternion rotation = new Quaternion();
    protected transient Vector3f lastLoc = new Vector3f();
    protected transient Quaternion lastRot = new Quaternion();

    public BoxControl() {
        super(200);
    }

    public Vector3f getLocation() {
        return location;
    }

    public Quaternion getRotation() {
        return rotation;
    }

    @Override
    public void create() {
        Spatial box = BoxControl.PROPS.getChild("Box").clone();

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
    public void prepare(Synchronizer newData) {
        BoxControl o = (BoxControl) newData;
        this.location.set(o.location);
        this.rotation.set(o.rotation);
    }

    @Override
    public void synchronize() {
        super.spatial.setLocalTranslation(this.location);
        super.spatial.setLocalRotation(this.rotation);
    }

    @Override
    public void hit(float dmg, Vector3f dir, Vector3f loc) {
        RigidBodyControl rbc = super.spatial.getControl(RigidBodyControl.class);
        rbc.applyImpulse(dir.mult(dmg), loc);
        super.hit(dmg, dir, loc);
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

        if (spatial != null) {
            this.location = spatial.getLocalTranslation();
            this.rotation = spatial.getLocalRotation();
        } else {
            this.location = null;
            this.rotation = null;
        }

        super.setSpatial(spatial);
    }

    @Override
    public void update(float tpf) {
        GameController gc = GameController.getInstance();
        SyncManager sm = gc.getSynchronizer();
        if (sm == null) {
            return;
        }

        if (this.lastLoc.equals(this.location) && this.lastRot.equals(this.rotation)) {
            return;
        }
        this.lastLoc.set(this.location);
        this.lastRot.set(this.rotation);
        sm.update(this);
    }
}
