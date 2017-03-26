package controls.entityes;

import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.VehicleControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import controllers.GameController;
import controls.weapons.WeaponControl;
import java.util.List;
import synchronization.SyncManager;
import synchronization.Synchronizer;

public class DroneControl extends PlayerControl {

    private final static Node MODEL = (Node) GameController.getInstance().getLoader().loadModel("Models/Drone.j3o");
    private transient VehicleControl character;
    private transient Node eye;
    private transient WeaponControl selectedWeapon;
    private Vector3f location = new Vector3f();
    private Quaternion rotation = new Quaternion();
    private Quaternion eyeRot = new Quaternion();
    private transient Spatial[] spinners;
    private float aimState;

    public DroneControl() {
        super.resetHealth(60);
    }

    @Override
    public void create() {
        super.create();

        GameController gc = GameController.getInstance();

        boolean server = gc.getSynchronizer() != null;
        if (server) {
            this.character = new VehicleControl();
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

        if (server) {
            this.character.setPhysicsLocation(Vector3f.UNIT_Y.mult(10));
        }
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
                CollisionShape hullShape = CollisionShapeFactory.createDynamicMeshShape(spatial);
                hullShape.setScale(Vector3f.UNIT_Y.mult(5));
                this.character.setCollisionShape(hullShape);
                this.character.setMass(10);
                spatial.addControl(this.character);
                GameController.getInstance().getPhysics().add(this.character);
                this.character.setGravity(Vector3f.ZERO);
            } else {
                Node base = (Node) n.getChild("Base");
                this.spinners = new Spatial[4];
                int ind = 0;
                List<Spatial> chs = base.getChildren();
                for (Spatial ch : chs) {
                    if (ch.getName().contains("Spinner")) {
                        this.spinners[ind] = ch;
                        ++ind;
                    }
                }
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
        this.eyeRot.set(o.eyeRot);
        this.aimState = o.aimState;

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
        Vector3f v = c.getDirection().mult(-3);
        v.multLocal(this.aimState);
        v.addLocal(this.eye.getWorldTranslation());
        c.setLocation(v);
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
    public void moveTo(Vector3f loc) {
        this.character.setPhysicsLocation(loc);
    }

    @Override
    public void restrictCamra(Camera camera) {
        final float min = FastMath.DEG_TO_RAD * 45;
        final float max = -FastMath.DEG_TO_RAD * 10;

        float[] angs = camera.getRotation().toAngles(null);
        if (angs[0] > min && angs[0] < FastMath.PI) {
            angs[0] = min;
            camera.setRotation(new Quaternion(angs));
        } else if (angs[0] < max && angs[0] > -FastMath.PI) {
            angs[0] = max;
            camera.setRotation(new Quaternion(angs));
        }
    }

    private void updateFirstPerson(float tpf) {
        tpf *= 3;
        if (this.secondaryFire) {
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
        SyncManager manager = GameController.getInstance().getSynchronizer();
        if (manager == null) {
            Quaternion q = new Quaternion();
            q.fromAngleAxis(tpf * 15, Vector3f.UNIT_Y);

            for (Spatial s : this.spinners) {
                s.setLocalRotation(s.getLocalRotation().mult(q));
            }

            return;
        }

        Quaternion rot = new Quaternion();
        rot.lookAt(look, Vector3f.UNIT_Y);

        float[] t = rot.toAngles(null);
        t[0] = 0;
        t[2] = 0;
        this.rotation.set(new Quaternion(t));
        this.location.set(super.spatial.getLocalTranslation());

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

        walkDir.normalizeLocal();
        walkDir.multLocal(10);

        this.character.setPhysicsLocation(this.character.getPhysicsLocation().add(walkDir.mult(tpf)));

        walkDir.set(0, 0, 0);
        if (super.space) {
            walkDir.addLocal(Vector3f.UNIT_Y);
        } else if (super.ctrl) {
            walkDir.subtractLocal(Vector3f.UNIT_Y);
        }

        this.character.setPhysicsLocation(this.character.getPhysicsLocation().add(walkDir.mult(tpf * 2)));

        this.character.setPhysicsRotation(this.rotation);

        this.updateFirstPerson(tpf);

        manager.update(this);
    }

    public static Node getModel() {
        return MODEL;
    }
}
