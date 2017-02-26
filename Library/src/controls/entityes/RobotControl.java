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
    private BetterCharacterControl character = new BetterCharacterControl(0.25f, 1.7f, 100);
    private Spatial head;
    private Spatial weapon1;
    private Spatial weapon2;
    private Vector3f weaponLoc;

    @Override
    public void create() {
        Node n = (Node) MODEL.clone();

        GameController.getInstance().getApplication().getRootNode().attachChild(n);

        n.addControl(this);

        this.character.setWalkDirection(Vector3f.UNIT_Z.mult(5));
    }

    @Override
    public void setSpatial(Spatial spatial) {
        if (spatial != null) {
            spatial.addControl(this.character);
            Node n = (Node) spatial;
            this.head = n.getChild("Head");
            this.weapon1 = n.getChild("Weapon1");
            this.weapon2 = n.getChild("Weapon2");
            GameController.getInstance().getPhysics().add(this.character);
            this.weaponLoc = this.weapon1.getLocalTranslation().clone();
        } else {
            super.spatial.removeControl(this.character);
            GameController.getInstance().getPhysics().remove(this.character);
        }
        super.setSpatial(spatial);
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

        Vector3f loc = c.getDirection().clone();
        loc.setY(0);
        loc.normalizeLocal();
        loc.multLocal(-5f);
        Vector3f sl = super.spatial.getWorldTranslation().clone();
        loc.addLocal(sl.add(Vector3f.UNIT_Y.mult(1.7f)));

        Vector3f loc2 = new Vector3f(0.15f, -0.1f, 0.1f);
        this.weapon2.setLocalTranslation(this.weaponLoc.add(loc2));
        loc2.setX(-loc2.getX());
        this.weapon1.setLocalTranslation(this.weaponLoc.add(loc2));

        c.setLocation(loc);
    }
}
