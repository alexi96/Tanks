package controls.entityes;

import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import controllers.GameController;
import synchronization.SyncManager;
import synchronization.Synchronizer;

public class RobotControl extends PlayerControl {

    private final static float MAX_SPRINT = 10;
    private final static float HEIGTH = 1.7f;
    private final static Node MODEL = (Node) GameController.getInstance().getLoader().loadModel("Models/Robot.j3o");
    private transient BetterCharacterControl character;
    private transient Node eye;
    private transient Spatial body;
    private transient Spatial head;
    private Vector3f location = new Vector3f();
    private Quaternion rotation = new Quaternion();
    private Quaternion eyeRot = new Quaternion();
    private float duckState;
    private transient float headDefaultHeigth;
    private transient float sprint = RobotControl.MAX_SPRINT;
    private transient boolean tired;

    public RobotControl() {
        super.resetHealth(100);
    }

    @Override
    public void create() {
        super.create();

        GameController gc = GameController.getInstance();
        boolean server = gc.getSynchronizer() != null;
        if (server) {
            this.character = new BetterCharacterControl(0.25f, 1.7f, 100);
        }

        Node n = (Node) MODEL.clone();
        n.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);

        gc.getApplication().getRootNode().attachChild(n);

        this.primary.setHolder(this);
        this.secondary.setHolder(this);
        this.primary.create();
        this.secondary.create();
        super.selected = primary;

        n.addControl(this);
    }

    @Override
    public void setSpatial(Spatial spatial) {
        boolean server = GameController.getInstance().getSynchronizer() != null;

        if (spatial != null) {
            Node n = (Node) spatial;
            this.eye = (Node) n.getChild("Eye");
            this.body = n.getChild("Body");
            this.head = n.getChild("Head");
            this.eye.detachAllChildren();
            this.eye.attachChild(this.primary.getSpatial());
            this.eye.attachChild(this.secondary.getSpatial());
            this.headDefaultHeigth = this.head.getLocalTranslation().getY();

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
        RobotControl o = (RobotControl) newData;
        super.health = o.health;
        
        this.location.set(o.location);
        this.rotation.set(o.rotation);
        this.eyeRot.set(o.eyeRot);
        this.duckState = o.duckState;

        this.primary.prepare(o.primary);
        this.secondary.prepare(o.secondary);
        if (o.selected == o.primary) {
            this.selected = this.primary;
        } else {
            this.selected = this.secondary;
        }
    }

    private void updatePhysics() {
        if (super.id != PlayerControl.serverId) {
            return;
        }

        Camera c = GameController.getInstance().getApplication().getCamera();
        c.setLocation(this.eye.getWorldTranslation());
    }

    @Override
    public void synchronize() {
        super.spatial.setLocalTranslation(this.location);
        this.body.setLocalScale(1, this.duckState, 1);
        float t = 1 - this.duckState;
        this.head.setLocalTranslation(0, this.headDefaultHeigth - t * RobotControl.HEIGTH / 2, 0);
        super.spatial.setLocalRotation(this.rotation);

        this.eye.setLocalRotation(this.eyeRot);

        this.primary.synchronize();
        this.secondary.synchronize();

        this.updatePhysics();
    }

    @Override
    public void moveTo(Vector3f loc) {
        this.character.warp(loc);
    }

    @Override
    public void restrictCamra(Camera camera) {
        final float min = FastMath.DEG_TO_RAD * 20;
        final float max = -FastMath.DEG_TO_RAD * 45;

        float[] angs = camera.getRotation().toAngles(null);
        if (angs[0] > min && angs[0] < FastMath.PI) {
            angs[0] = min;
            camera.setRotation(new Quaternion(angs));
        } else if (angs[0] < max && angs[0] > -FastMath.PI) {
            angs[0] = max;
            camera.setRotation(new Quaternion(angs));
        }
    }

    private void updateDuck(float tpf) {
        tpf *= 2;
        this.character.setDucked(this.ctrl);

        if (super.ctrl) {
            if (this.duckState <= 0) {
                return;
            }

            this.duckState -= tpf;
            if (this.duckState < 0) {
                this.duckState = 0;
            }
            return;
        }

        if (this.duckState >= 1) {
            return;
        }

        this.duckState += tpf;
        if (this.duckState > 1) {
            this.duckState = 1;
        }
    }

    private void updateWeapons(float tpf) {
        if (super.swap) {
            super.swap = false;
            this.selected.secondaryFire(false);
            this.selected.fire(false);
            if (this.primary == this.selected) {
                this.selected = this.secondary;
            } else {
                this.selected = this.primary;
            }
        }

        this.primary.update(tpf);
        this.secondary.update(tpf);

        this.selected.fire(super.fire);
        this.selected.secondaryFire(super.secondaryFire);
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
        this.location.set(super.spatial.getWorldTranslation());

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

        if (super.space) {
            super.space = false;
            this.character.jump();
        }

        /*if (this.tired) {
            if (super.shift && this.sprint > 4) {
                this.sprint -= tpf;
                walkDir.multLocal(2);
                if (this.sprint <= 0) {
                    this.tired = true;
                }
            } else {
                this.sprint += tpf * 2;
            }
        } else {
            if (super.shift && this.sprint > 0) {
                this.sprint -= tpf;
                walkDir.multLocal(2);
                if (this.sprint <= 0) {
                    this.tired = true;
                }
            } else {
                this.sprint += tpf * 2;
            }
        }*/
        walkDir.multLocal(3);
        this.character.setWalkDirection(walkDir);
        this.location = super.spatial.getWorldTranslation().clone();

        this.body.setLocalScale(1, this.duckState, 1);
        float td = 1 - this.duckState;
        this.head.setLocalTranslation(0, this.headDefaultHeigth - td * RobotControl.HEIGTH / 2, 0);

        manager.update(RobotControl.this);

        this.updateDuck(tpf);
    }

    public static Node getModel() {
        return MODEL;
    }
}
