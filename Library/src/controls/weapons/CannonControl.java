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

public class CannonControl extends WeaponControl {

    private final static Node MODEL = TankControl.getModel();
    protected transient boolean aiming;
    protected float aimState;
    protected transient Spatial front;

    public CannonControl() {
        super(200, 3f, 20);
    }

    @Override
    public void secondaryFire(boolean f) {
        this.aiming = f;
    }

    @Override
    public void create() {
        Spatial s = CannonControl.MODEL.getChild("Cannon").clone();

        s.addControl(this);
    }

    @Override
    public void setSpatial(Spatial spatial) {
        if (spatial != null) {
            Node n = (Node) spatial;
            this.front = n.getChild("CannonFront");
        } else {
        }
        super.setSpatial(spatial);
    }

    @Override
    public void synchronize() {
        Quaternion rot = new Quaternion();
        rot.fromAngleAxis(FastMath.HALF_PI * this.aimState, Vector3f.UNIT_Z);
        super.spatial.setLocalRotation(rot);
        this.front.setLocalTranslation(0, 0, -this.state / this.fireRate * 0.65f);
    }

    @Override
    public boolean fire() {
        if (!super.fire()) {
            return false;
        }
        Vector3f dir = super.spatial.getParent().getWorldRotation().getRotationColumn(2);
        BulletControl bc = new BulletControl(dir, 10, super.damage, super.holder, 100) {

            @Override
            public void create() {
                super.create(); //To change body of generated methods, choose Tools | Templates.
                super.spatial.scale(10);
            }
        };
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
        this.front.setLocalTranslation(0, 0, -this.state / this.fireRate * 0.65f);
    }
}
