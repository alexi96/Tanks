package controls.weapons;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import controllers.GameController;
import controls.entityes.TankControl;
import controls.projectiles.MissleControl;
import synchronization.SyncManager;

public class MissleLauncher extends WeaponControl {

    private final static Node MODEL = TankControl.getModel();

    public MissleLauncher() {
        super(400, 5, 20);
    }

    @Override
    public void create() {
        Spatial s = MissleLauncher.MODEL.getChild("MissleLauncher").clone();

        s.addControl(this);
    }

    @Override
    public boolean fire() {
        if (!super.fire()) {
            return false;
        }
        Quaternion rot = super.spatial.getParent().getWorldRotation();
        rot.multLocal(new Quaternion().fromAngleAxis(-FastMath.DEG_TO_RAD * 30, Vector3f.UNIT_X));
        Vector3f dir = rot.getRotationColumn(2);
        MissleControl rc = new MissleControl(dir, 60, super.damage, super.holder, 350);
        
        rc.setLocation(super.barrel.getWorldTranslation().clone());
        GameController.getInstance().getSynchronizer().create(rc);

        return true;
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
