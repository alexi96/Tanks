package controls.weapons;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import controllers.GameController;
import controls.entityes.RobotControl;
import controls.projectiles.RocketControl;
import controls.projectiles.ProjectileControl;
import synchronization.SyncManager;

public class RpgControl extends WeaponControl {

    private final static Node MODEL = RobotControl.getModel();
    protected transient Spatial scope;
    protected Vector3f location = new Vector3f();
    protected transient Vector3f weaponDefaultLocation;
    protected transient boolean aiming;
    protected float aimState;

    public RpgControl() {
        super(100, 5f, 10);
    }

    @Override
    public void secondaryFire(boolean f) {
        this.aiming = f;
    }

    @Override
    public void create() {
        Spatial s = RpgControl.MODEL.getChild("Rpg").clone();

        this.setSpatial(s);
    }

    @Override
    public void setSpatial(Spatial spatial) {
        if (spatial != null) {
            Node n = (Node) spatial;
            this.scope = n.getChild("Aim");
            this.weaponDefaultLocation = spatial.getLocalTranslation().clone();
        }

        super.setSpatial(spatial);
    }

    @Override
    public void synchronize() {
        super.spatial.setLocalTranslation(this.location);

        this.scope.setLocalRotation(new Quaternion().fromAngleAxis(FastMath.HALF_PI * this.aimState, Vector3f.UNIT_Y));

        Quaternion r = new Quaternion();

        float t = super.state / super.fireRate;
        t = 1 - t;
        if (t <= 0.1f) {
            t /= 0.1f;
            r.fromAngleAxis(-FastMath.QUARTER_PI * t / 2, Vector3f.UNIT_X);
        } else if (t > 0.1f && t < 0.9f) {
            r.fromAngleAxis(-FastMath.QUARTER_PI / 2, Vector3f.UNIT_X);
        } else {
            t -= 0.9f;
            t = t / 0.1f;
            t = 1 - t;
            r.fromAngleAxis(-FastMath.QUARTER_PI * t / 2, Vector3f.UNIT_X);
        }

        super.spatial.setLocalRotation(r);
    }

    @Override
    public boolean fire() {
        if (!super.fire()) {
            return false;
        }
        Vector3f dir = super.spatial.getWorldRotation().getRotationColumn(2);
        ProjectileControl bc = new RocketControl(dir, 75, super.damage, super.holder, 100);
        bc.setLocation(super.barrel.getWorldTranslation().clone());
        GameController.getInstance().getSynchronizer().create(bc);

        return true;
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

        super.update(tpf);

        this.updateAim(tpf);

        Vector3f loc = new Vector3f(-0.15f, 0, 0.1f);
        loc.multLocal(this.aimState);
        loc.addLocal(this.weaponDefaultLocation);
        loc.addLocal(0, 0, -0.03f * this.state / this.fireRate);
        this.location.set(loc);
        super.spatial.setLocalTranslation(this.location);
    }
}
