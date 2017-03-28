package controls.weapons;

import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import controllers.GameController;
import controls.entityes.RobotControl;
import controls.projectiles.BulletControl;
import synchronization.SyncManager;

public class MachineGun extends WeaponControl {

    private final static Node MODEL = RobotControl.getModel();
    protected Vector3f location = new Vector3f();
    protected transient Vector3f weaponDefaultLocation;
    protected transient boolean aiming;
    protected transient float aimState;

    public MachineGun() {
        super(15, 0.075f, 1000);
    }

    @Override
    public void secondaryFire(boolean f) {
        this.aiming = f;
    }

    @Override
    public void create() {
        Spatial s = MachineGun.MODEL.getChild("MachineGun").clone();

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
    }

    @Override
    public boolean fire() {
        if (!super.fire()) {
            return false;
        }
        Vector3f dir = super.spatial.getWorldRotation().getRotationColumn(2);
        BulletControl bc = new BulletControl(dir, 100, super.damage, super.holder, 300);
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
