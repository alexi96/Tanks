package controls.entityes;

import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import controllers.GameController;
import synchronization.SyncManager;

public class RobotControl extends PlayerControl {

    private final static float HEIGTH = 1.7f;
    private final static Node MODEL = (Node) GameController.getInstance().getLoader().loadModel("Models/Robot.j3o");
    private transient BetterCharacterControl character;
    private transient Node eye;
    private transient Spatial body;
    private transient Spatial head;
    private transient Spatial weapon1;
    private transient Spatial weapon2;
    private Vector3f location = new Vector3f();
    private Quaternion rotation = new Quaternion();
    private Quaternion eyeRot = new Quaternion();
    private Vector3f weaponLoc1 = new Vector3f();
    private Vector3f weaponLoc2 = new Vector3f();
    private float duckState;
    private transient Vector3f weaponDefaultLocation;
    private transient float headDefaultHeigth;

    @Override
    public void create() {
        boolean server = GameController.getInstance().getSynchronizer() != null;
        if (server) {
            this.character = new BetterCharacterControl(0.25f, 1.7f, 100);
        }

        Node n = (Node) MODEL.clone();
        n.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);

        GameController.getInstance().getApplication().getRootNode().attachChild(n);

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
            this.weapon1 = n.getChild("MachineGun");
            this.weapon2 = n.getChild("TestWeapon");
            this.eye.detachAllChildren();
            this.eye.attachChild(this.weapon1);
            this.eye.attachChild(this.weapon2);
            this.weaponDefaultLocation = this.weapon1.getLocalTranslation().clone();
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

        this.weapon1.setLocalTranslation(this.weaponLoc1);
        this.weapon2.setLocalTranslation(this.weaponLoc2);

        this.updatePhysics();
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

        Vector3f loc = new Vector3f(-0.15f, 0, 0.1f);
        this.weaponLoc1.set(this.weaponDefaultLocation.add(loc));
        this.weaponLoc2.set(this.weaponDefaultLocation.add(loc.setX(-loc.getX())));

        this.eye.setLocalRotation(this.eyeRot);

        if (super.secondaryFire) {
            this.weaponLoc1.set(this.weaponDefaultLocation);
        }

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

        walkDir.multLocal(3);
        this.character.setWalkDirection(walkDir);
        this.location = super.spatial.getWorldTranslation().clone();
        manager.update(RobotControl.this);

        this.updateDuck(tpf);
    }
}
