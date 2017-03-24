package controls.weapons;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import controllers.GameController;
import controls.entityes.DroneControl;
import synchronization.SyncManager;

public class GrenadeLauncher extends WeaponControl {
      private final static Node MODEL = DroneControl.getModel();
    protected Vector3f location = new Vector3f();
    protected Quaternion rotation = new Quaternion();
    protected transient boolean aiming;
    protected float aimState;

    public GrenadeLauncher() {
        super(100, 1.5f, 50);
    }

    @Override
    public void secondaryFire(boolean f) {
        this.aiming = f;
    }

    @Override
    public void create() {
        Spatial s = GrenadeLauncher.MODEL.getChild("GrenadeLauncher").clone();

        this.setSpatial(s);
    }

    @Override
    public void setSpatial(Spatial spatial) {
        if (spatial != null) {
        } else {
        }
        super.setSpatial(spatial);
    }

    @Override
    public void synchronize() {
        super.spatial.setLocalTranslation(this.location);
        super.spatial.setLocalRotation(this.rotation);
    }

    @Override
    public void update(float tpf) {
        SyncManager sm = GameController.getInstance().getSynchronizer();
        if (sm == null) {
            return;
        }
        
        super.update(tpf);
    }
}
