package controls.weapons;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import controllers.GameController;
import controls.entityes.RobotControl;
import controls.projectiles.BulletControl;
import controls.projectiles.ShotgunShell;
import java.util.Random;
import synchronization.SyncManager;

public class AutoShotgun extends WeaponControl {

    private static final Random RAND = new Random();
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
    public boolean fire() {
        if (!super.fire()) {
            return false;
        }
        Vector3f dir = super.spatial.getWorldRotation().getRotationColumn(2);
        Quaternion dirQ = new Quaternion();
        dirQ.lookAt(dir, Vector3f.UNIT_Y);
        float change = 2f * FastMath.DEG_TO_RAD;

        float ax = AutoShotgun.RAND.nextInt(101);
        float ay = AutoShotgun.RAND.nextInt(101);
        ax *= change;
        ay *= change;
        ax /= 100f;
        ay /= 100f;

        Quaternion t = dirQ.clone();
        t.multLocal(new Quaternion().fromAngleAxis(ax, Vector3f.UNIT_Y));
        t.multLocal(new Quaternion().fromAngleAxis(ay, Vector3f.UNIT_X));

        ShotgunShell bc = new ShotgunShell.NoisyShotgunShell(t.getRotationColumn(2), 75, super.damage, super.holder, 300);
        bc.setLocation(super.barrel.getWorldTranslation().clone());
        GameController.getInstance().getSynchronizer().create(bc);

        final int shellNum = 9;
        for (int i = 0; i < shellNum; i++) {
            ax = AutoShotgun.RAND.nextInt(101);
            ay = AutoShotgun.RAND.nextInt(101);
            ax *= change;
            ay *= change;
            ax /= 100f;
            ay /= 100f;

            t = dirQ.clone();
            t.multLocal(new Quaternion().fromAngleAxis(ax, Vector3f.UNIT_Y));
            t.multLocal(new Quaternion().fromAngleAxis(ay, Vector3f.UNIT_X));

            bc = new ShotgunShell(t.getRotationColumn(2), 75, super.damage, super.holder, 300);
            bc.setLocation(super.barrel.getWorldTranslation().clone());
            GameController.getInstance().getSynchronizer().create(bc);
        }

        return true;
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
        super.spatial.setLocalTranslation(this.location);

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
