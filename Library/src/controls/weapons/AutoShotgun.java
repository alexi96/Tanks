package controls.weapons;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import controllers.GameController;
import controls.entityes.RobotControl;
import synchronization.SyncManager;

public class AutoShotgun extends WeaponControl {

    private final static Node MODEL = RobotControl.getModel();
    protected Vector3f location = new Vector3f();
    protected Quaternion rotation = new Quaternion();
    protected transient Vector3f weaponDefaultLocation;
    protected transient boolean aiming;
    protected float aimState;

    public AutoShotgun() {
        super(2, 1, 50);
    }

    @Override
    public void secondaryFire(boolean f) {
        this.aiming = f;
    }

    @Override
    public void create() {
        Spatial s = AutoShotgun.MODEL.getChild("TestWeapon").clone();

        this.setSpatial(s);
    }

    @Override
    public void setSpatial(Spatial spatial) {
        if (spatial != null) {
            this.weaponDefaultLocation = spatial.getLocalTranslation().clone();
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

        if (this.aiming) {
            if (this.aimState > 0) {
                this.aimState -= tpf;
                if (this.aimState < 0) {
                    this.aimState = 0;
                }
            }
        } else {
            if (this.aimState < 1) {
                this.aimState += tpf;
                if (this.aimState > 1) {
                    this.aimState = 1;
                }
            }
        }

        Vector3f loc = new Vector3f(0.15f, 0, 0.1f);
        loc.multLocal(this.aimState);
        loc.addLocal(this.weaponDefaultLocation);
        loc.addLocal(0, 0, -0.03f * this.state / this.fireRate);
        this.location.set(loc);

        float ang = this.state / this.fireRate;
        if (ang <= 0.5f) {
            ang *= 2;
            ang = FastMath.QUARTER_PI * ang;
        } else {
            ang = 1 - ang;
            ang *= 2;
            ang = FastMath.QUARTER_PI * ang;
        }

        this.rotation.set(new Quaternion().fromAngleAxis(-ang, Vector3f.UNIT_X));
    }
}
