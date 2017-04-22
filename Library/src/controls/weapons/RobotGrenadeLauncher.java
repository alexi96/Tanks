package controls.weapons;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import controllers.GameController;
import controls.entityes.RobotControl;
import controls.projectiles.GrenadeControl;
import controls.projectiles.ProjectileControl;
import synchronization.SyncManager;

public class RobotGrenadeLauncher extends WeaponControl {

    private final static Node MODEL = RobotControl.getModel();
    protected Vector3f location = new Vector3f();
    protected Quaternion rotation = new Quaternion();
    protected transient Spatial clip;
    protected transient boolean aiming;
    protected float aimState;
    protected transient Vector3f weaponDefaultLocation;

    public RobotGrenadeLauncher() {
        super(100, 1.5f, 50);
    }

    @Override
    public void secondaryFire(boolean f) {
        this.aiming = f;
    }

    @Override
    public boolean fire() {
        if (!super.fire()) {
            return false;
        }
        Vector3f dir = super.spatial.getParent().getWorldRotation().getRotationColumn(2);
        ProjectileControl pc = new GrenadeControl(dir, 20, super.damage, super.holder, 100);
        pc.setLocation(super.barrel.getWorldTranslation().clone());
        GameController.getInstance().getSynchronizer().create(pc);

        return true;
    }

    @Override
    public void create() {
        Spatial s = RobotGrenadeLauncher.MODEL.getChild("GrenadeLauncher").clone();

        this.setSpatial(s);
    }

    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);

        if (spatial != null) {
            Node n = (Node) spatial;
            this.clip = n.getChild("Clip");
            this.weaponDefaultLocation = spatial.getLocalTranslation().clone();
        } else {
            this.clip = null;
        }
    }

    @Override
    public void synchronize() {
        super.spatial.setLocalTranslation(this.location);
        super.spatial.setLocalRotation(this.rotation);

        Quaternion q = new Quaternion();
        q.fromAngleAxis(this.state * 60 / this.fireRate * FastMath.DEG_TO_RAD, Vector3f.UNIT_Z);
        this.clip.setLocalRotation(q);
        
        float ang = this.state / this.fireRate;
        if (ang <= 0.5f) {
            ang *= 2;
            ang = FastMath.QUARTER_PI * ang / 4;
        } else {
            ang = 1 - ang;
            ang *= 2;
            ang = FastMath.QUARTER_PI * ang / 4;
        }

        this.rotation.set(new Quaternion().fromAngleAxis(-ang, Vector3f.UNIT_X));
        
        q = new Quaternion();
        q.fromAngleAxis(-ang, Vector3f.UNIT_X);
        super.spatial.setLocalRotation(q);
    }

    private void updateAim(float tpf) {
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
    }

    @Override
    public void update(float tpf) {
        SyncManager sm = GameController.getInstance().getSynchronizer();
        if (sm == null) {
            return;
        }

        this.updateAim(tpf);

        Vector3f loc = new Vector3f(0.15f, 0, 0.1f);
        loc.multLocal(this.aimState);
        loc.addLocal(this.weaponDefaultLocation);
        loc.addLocal(0, 0, -0.03f * this.state / this.fireRate);
        this.location.set(loc);
        super.spatial.setLocalTranslation(this.location);

        super.update(tpf);
    }
}
