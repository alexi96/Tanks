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
import synchronization.Synchronizer;

public class RobotControl extends PlayerControl {

    private final static Node MODEL = (Node) GameController.getInstance().getLoader().loadModel("Models/Robot.j3o");
    private transient BetterCharacterControl character = new BetterCharacterControl(0.25f, 1.7f, 100) {
        @Override
        public void update(float tpf) {
            super.update(tpf);
        }
    };
    private transient Spatial head;
    private transient Spatial weapon1;
    private transient Spatial weapon2;
    private Vector3f location;
    private Quaternion rotation;
    private Quaternion weaponRot = new Quaternion();
    private Vector3f weaponLoc = new Vector3f();
    private Quaternion headRot = new Quaternion();
    private Vector3f weaponDefaultLocation;

    @Override
    public void create() {
        Node n = (Node) MODEL.clone();
        n.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);

        GameController.getInstance().getApplication().getRootNode().attachChild(n);

        n.addControl(this);
    }

    @Override
    public void setSpatial(Spatial spatial) {
        boolean server = GameController.getInstance().getSynchronizer() != null;

        if (spatial != null) {
            this.location = spatial.getLocalTranslation();
            this.rotation = spatial.getLocalRotation();

            Node n = (Node) spatial;
            this.head = n.getChild("Head");
            this.weapon1 = n.getChild("Weapon1");
            this.weapon2 = n.getChild("Weapon2");
            this.weaponDefaultLocation = this.weapon1.getLocalTranslation().clone();

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
        Camera c = GameController.getInstance().getApplication().getCamera();

        Vector3f loc = new Vector3f(c.getDirection());
        loc.setY(0);
        loc.normalizeLocal();
        loc.multLocal(-0.225f);
        Vector3f sl = super.spatial.getWorldTranslation().clone();
        loc.addLocal(sl.add(Vector3f.UNIT_Y.mult(1.7f)));

        c.setLocation(loc);
    }

    @Override
    public void prepare(Synchronizer newData) {
        super.prepare(newData);
    }

    @Override
    public void synchronize() {
        Camera c = GameController.getInstance().getApplication().getCamera();

        Vector3f loc = new Vector3f(c.getDirection());
        loc.setY(0);
        loc.normalizeLocal();
        loc.multLocal(-0.225f);
        loc.addLocal(this.location.add(Vector3f.UNIT_Y.mult(1.7f)));

        c.setLocation(loc);

        super.spatial.setLocalTranslation(this.location);
        super.spatial.setLocalRotation(this.rotation);

        this.head.setLocalRotation(this.headRot);
        this.weapon1.setLocalRotation(this.weaponRot.clone());
        this.weapon2.setLocalRotation(this.weaponRot);

        this.weapon1.setLocalTranslation(this.weaponLoc);
        this.weapon2.setLocalTranslation(this.weaponLoc.clone().setX(-this.weaponLoc.getX()));
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
        this.headRot.set(new Quaternion(t));
        t = rot.toAngles(null);
        t[1] = 0;
        t[2] = 0;
        this.weaponRot.set(new Quaternion(t));

        Vector3f loc = new Vector3f(0.15f, -0.1f, 0.1f);
        this.weaponLoc.set(this.weaponDefaultLocation.add(loc));

        this.head.setLocalRotation(this.headRot);
        this.weapon1.setLocalRotation(this.weaponRot);
        this.weapon1.setLocalTranslation(this.weaponLoc);
        this.weapon2.setLocalRotation(this.weaponRot);
        this.weapon2.setLocalTranslation(this.weaponLoc.clone().setX(-this.weaponLoc.getX()));

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

        this.character.setWalkDirection(walkDir);
        manager.update(this);
    }
}
