package controls.props;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import controllers.GameController;
import controls.DestroyableControl;
import synchronization.SyncManager;
import synchronization.Synchronizer;

public abstract class PropControl extends DestroyableControl {
    protected static final Node PROPS = (Node) GameController.getInstance().getApplication().getAssetManager().loadModel("Models/Props.j3o");

    protected Vector3f location = new Vector3f();
    protected Quaternion rotation = new Quaternion();
    protected transient Vector3f lastLoc = new Vector3f();
    protected transient Quaternion lastRot = new Quaternion();

    public PropControl() {
    }

    public PropControl(float health) {
        super(health);
    }

    @Override
    public void prepare(Synchronizer newData) {
        PropControl o = (PropControl) newData;
        this.location.set(o.location);
        this.rotation.set(o.rotation);
    }

    @Override
    public void synchronize() {
        super.spatial.setLocalTranslation(this.location);
        super.spatial.setLocalRotation(this.rotation);
    }

    @Override
    public boolean hit(float dmg, Vector3f dir, Vector3f loc) {
        RigidBodyControl rbc = super.spatial.getControl(RigidBodyControl.class);
        if (rbc != null) {
            rbc.applyImpulse(dir.mult(dmg), loc);
        }
        return super.hit(dmg, dir, loc);
    }
    
    @Override
    public void setSpatial(Spatial spatial) {
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
