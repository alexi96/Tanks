package controls.weapons;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import controllers.GameController;
import controls.entityes.TankControl;
import controls.projectiles.BulletControl;
import synchronization.SyncManager;

public class MinigunControl extends WeaponControl {

    private final static Node MODEL = TankControl.getModel();
    protected transient boolean aiming;
    protected float aimState;
    protected transient Spatial spinner;
    protected float spin;

    public MinigunControl() {
        super(10, 0.1f, 1000);
    }

    @Override
    public void secondaryFire(boolean f) {
        this.aiming = f;
    }

    @Override
    public void setSpatial(Spatial spatial) {
        if (spatial != null) {
            Node n = (Node) spatial;
            this.spinner = n.getChild("Spinner");
        }
        super.setSpatial(spatial);
    }

    @Override
    public void create() {
        Spatial s = MinigunControl.MODEL.getChild("Minigun").clone();

        s.addControl(this);
    }

    @Override
    public void synchronize() {
        Quaternion rot = new Quaternion();
        float t = 1 - this.aimState;
        rot.fromAngleAxis(FastMath.HALF_PI * t, Vector3f.UNIT_Z);
        super.spatial.setLocalRotation(rot);

        this.spinner.setLocalRotation(new Quaternion().fromAngleAxis(this.spin, Vector3f.UNIT_Z));
    }

    @Override
    public boolean fire() {
        if (!super.fire()) {
            return false;
        }
        Vector3f dir = super.spatial.getParent().getWorldRotation().getRotationColumn(2);
        BulletControl bc = new BulletControl(dir, 100, super.damage, super.holder, 150);
        bc.setLocation(super.barrel.getWorldTranslation().clone());
        GameController.getInstance().getSynchronizer().create(bc);

        return true;
    }

    @Override
    public void update(float tpf) {
        SyncManager sm = GameController.getInstance().getSynchronizer();
        if (sm == null) {
            return;
        }

        super.update(tpf);

        if (this.aiming) {
            if (this.aimState < 1) {
                this.aimState += tpf;
                if (this.aimState > 1) {
                    this.aimState = 1;
                }
            }
        } else {
            if (this.aimState > 0) {
                this.aimState -= tpf;
                if (this.aimState < 0) {
                    this.aimState = 0;
                }
            }
        }

        Quaternion rot = new Quaternion();
        rot.fromAngleAxis(FastMath.HALF_PI * this.aimState, Vector3f.UNIT_Z);
        super.spatial.setLocalRotation(rot);

        if (super.fireing) {
            this.spin += tpf * 10;
        }
    }
}
