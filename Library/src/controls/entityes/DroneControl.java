package controls.entityes;

import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import controllers.GameController;
import controls.weapons.WeaponControl;
import synchronization.SyncManager;
import synchronization.Synchronizer;

public class DroneControl extends PlayerControl {

    private final static Node MODEL = (Node) GameController.getInstance().getLoader().loadModel("Models/Drone.j3o");
    private transient BetterCharacterControl character;
    private transient Node eye;
    private transient WeaponControl selectedWeapon;
    private Vector3f location = new Vector3f();
    private Quaternion rotation = new Quaternion();
    private Quaternion eyeRot = new Quaternion();

    @Override
    public void create() {
        super.create();

        GameController gc = GameController.getInstance();

        boolean server = gc.getSynchronizer() != null;
        if (server) {
            this.character = new BetterCharacterControl(0.5f, 0.5f, 10);
        }

        Node n = (Node) MODEL.clone();
        n.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);

        gc.getApplication().getRootNode().attachChild(n);

        this.primary.setHolder(this);
        this.secondary.setHolder(this);
        this.primary.create();
        this.secondary.create();
        this.selectedWeapon = primary;

        n.addControl(this);
        
        this.character.warp(Vector3f.UNIT_Y.mult(10));
    }

    @Override
    public void setSpatial(Spatial spatial) {
        boolean server = GameController.getInstance().getSynchronizer() != null;

        if (spatial != null) {
            Node n = (Node) spatial;
            this.eye = (Node) n.getChild("Eye");
            this.eye.detachAllChildren();
            this.eye.attachChild(this.primary.getSpatial());
            this.eye.attachChild(this.secondary.getSpatial());

            if (server) {
                spatial.addControl(this.character);
                GameController.getInstance().getPhysics().add(this.character);
            }
        } else if (server) {
            super.spatial.removeControl(this.character);
            GameController.getInstance().getPhysics().remove(this.character);
        }
        super.setSpatial(spatial);
    }

    @Override
    public void prepare(Synchronizer newData) {
        DroneControl o = (DroneControl) newData;
        this.location.set(o.location);
        this.rotation.set(o.rotation);

        this.primary.prepare(o.primary);
        this.secondary.prepare(o.secondary);
    }

    @Override
    public void synchronize() {
        super.spatial.setLocalTranslation(this.location);
        super.spatial.setLocalRotation(this.rotation);

        this.eye.setLocalRotation(this.eyeRot);

        this.primary.synchronize();
        this.secondary.synchronize();

        if (super.id != PlayerControl.serverId) {
            return;
        }

        Camera c = GameController.getInstance().getApplication().getCamera();
        c.setLocation(this.eye.getWorldTranslation());
    }

    private void updateWeapons(float tpf) {
        if (super.swap) {
            super.swap = false;
            this.selectedWeapon.secondaryFire(false);
            this.selectedWeapon.fire(false);
            if (this.primary == this.selectedWeapon) {
                this.selectedWeapon = this.secondary;
            } else {
                this.selectedWeapon = this.primary;
            }
        }

        this.primary.update(tpf);
        this.secondary.update(tpf);

        this.selectedWeapon.fire(super.fire);
        this.selectedWeapon.secondaryFire(super.secondaryFire);
    }

    @Override
    public void update(float tpf) {
        SyncManager manager = GameController.getInstance().getSynchronizer();
        if (manager == null) {
            return;
        }
        
        Quaternion rot = new Quaternion();
        rot.lookAt(look, Vector3f.UNIT_Y);

        float[] t = rot.toAngles(null);
        t[0] = 0;
        t[2] = 0;
        this.rotation.set(new Quaternion(t));
        this.location.set(super.spatial.getLocalTranslation());

        this.character.setViewDirection(super.look);

        t = rot.toAngles(null);
        t[1] = 0;
        t[2] = 0;
        this.eyeRot.set(new Quaternion(t));

        this.updateWeapons(tpf);

        this.eye.setLocalRotation(this.eyeRot);

        Vector3f walkDir = new Vector3f();
        Vector3f forward = super.look.clone();
        forward.setY(0);
        forward.normalizeLocal();
        Vector3f leftDir = rot.getRotationColumn(0);
        leftDir.setY(0);
        leftDir.normalizeLocal();

        if (super.up) {
            walkDir.addLocal(forward);
        } else if (super.down) {
            walkDir.addLocal(forward.negate());
        }
        if (super.left) {
            walkDir.addLocal(leftDir);
        } else if (super.right) {
            walkDir.addLocal(leftDir.negate());
        }

        walkDir.multLocal(6);
        
        this.character.setWalkDirection(walkDir);
        this.location = super.spatial.getWorldTranslation().clone();


        manager.update(this);
    }

    public static Node getModel() {
        return MODEL;
    }
}
