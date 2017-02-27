package controls.entityes;

import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import controllers.GameController;

public class RobotControl extends PlayerControl {

    private final static Node MODEL = (Node) GameController.getInstance().getLoader().loadModel("Models/Robot.j3o");
    private BetterCharacterControl character = new BetterCharacterControl(0.25f, 1.7f, 100) {
        @Override
        public void update(float tpf) {
            super.update(tpf);
            Camera c = GameController.getInstance().getApplication().getCamera();

            Vector3f loc = new Vector3f(c.getDirection());
            loc.setY(0);
            loc.normalizeLocal();
            loc.multLocal(-0.225f);
            Vector3f sl = super.spatial.getWorldTranslation().clone();
            loc.addLocal(sl.add(Vector3f.UNIT_Y.mult(1.7f)));

            c.setLocation(loc);
        }
    };
    private Spatial head;
    private Spatial weapon1;
    private Spatial weapon2;
    private Vector3f weaponLoc;

    @Override
    public void create() {
        Node n = (Node) MODEL.clone();

        GameController.getInstance().getApplication().getRootNode().attachChild(n);

        n.addControl(this);
    }

    @Override
    public void setSpatial(Spatial spatial) {
        boolean server = GameController.getInstance().getSynchronizer() != null;

        if (spatial != null) {
            Node n = (Node) spatial;
            this.head = n.getChild("Head");
            this.weapon1 = n.getChild("Weapon1");
            this.weapon2 = n.getChild("Weapon2");

            if (server) {
                spatial.addControl(this.character);
                GameController.getInstance().getPhysics().add(this.character);
                this.weaponLoc = this.weapon1.getLocalTranslation().clone();
            }
        } else if (server) {
            super.spatial.removeControl(this.character);
            GameController.getInstance().getPhysics().remove(this.character);
        }
        super.setSpatial(spatial);
    }

    @Override
    public void synchronize() {
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
    public void update(float tpf) {
        Camera c = GameController.getInstance().getApplication().getCamera();

        float[] t = c.getRotation().toAngles(null);
        t[0] = 0;
        t[2] = 0;
        this.head.setLocalRotation(new Quaternion(t));
        t = c.getRotation().toAngles(null);
        t[1] = 0;
        t[2] = 0;
        this.weapon1.setLocalRotation(new Quaternion(t));
        this.weapon2.setLocalRotation(new Quaternion(t));

        Vector3f loc = new Vector3f(0.15f, -0.1f, 0.1f);
        this.weapon2.setLocalTranslation(this.weaponLoc.add(loc));
        loc.setX(-loc.getX());
        this.weapon1.setLocalTranslation(this.weaponLoc.add(loc));

        Vector3f walkDir = new Vector3f();
        Vector3f forward = c.getDirection();
        forward.setY(0);
        forward.normalizeLocal();
        Vector3f leftDir = c.getLeft();
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
    }
}
