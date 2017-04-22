package controls.projectiles;

import com.jme3.app.SimpleApplication;
import com.jme3.audio.AudioData;
import com.jme3.audio.AudioNode;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import controllers.GameController;
import controls.DestroyableControl;
import controls.GameControl;
import controls.entityes.PlayerControl;
import utilities.LoadingManager;

public class ShotgunShell extends BulletControl {

    private static final AudioNode FIRE = new AudioNode(GameController.getInstance().getApplication().getAssetManager(), "Sounds/ShotgunFire.wav", AudioData.DataType.Buffer);

    public ShotgunShell() {
    }

    public ShotgunShell(Vector3f direction, float speed, float damage, PlayerControl source, float range) {
        super(direction, speed, damage, source, range);
    }

    @Override
    public boolean colides(float dist) {
        Ray ray = new Ray(this.location, this.direction);
        ray.setLimit(dist);
        Node root = GameController.getInstance().getApplication().getRootNode();
        CollisionResults res = new CollisionResults();

        int sz = root.collideWith(ray, res);
        for (int i = 0; i < sz; i++) {
            CollisionResult r = res.getCollision(i);
            if (r.getContactPoint().subtract(ray.getOrigin()).lengthSquared() > dist * 2) {
                break;
            }

            Geometry g = r.getGeometry();
            GameControl gc = LoadingManager.findControl(g, GameControl.class);
            if (gc == null) {
                return true;
            }
            if (gc == this) {
                continue;
            }
            if (gc instanceof ShotgunShell) {
                continue;
            }
            if (!(gc instanceof DestroyableControl)) {
                return true;
            }
            if (gc == this.source) {
                continue;
            }
            DestroyableControl dc = (DestroyableControl) gc;
            this.hit(dc, r.getContactNormal().negate(), r.getContactPoint());
            return true;
        }

        return false;
    }

    @Override
    public void create() {
        SimpleApplication app = GameController.getInstance().getApplication();

        Spatial s = ProjectileControl.MODEL.getChild("Shell").clone();
        Quaternion rot = new Quaternion();
        rot.lookAt(super.direction, Vector3f.UNIT_Y);
        s.setLocalRotation(rot);
        s.setLocalTranslation(super.location);
        s.addControl(this);
        app.getRootNode().attachChild(s);
    }

    public static class NoisyShotgunShell extends ShotgunShell {

        public NoisyShotgunShell() {
        }

        public NoisyShotgunShell(Vector3f direction, float speed, float damage, PlayerControl source, float range) {
            super(direction, speed, damage, source, range);
        }

        @Override
        public void create() {
            super.create();

            if (GameController.getInstance().getSynchronizer() != null) {
                return;
            }
            AudioNode an = ShotgunShell.FIRE.clone();
            an.setLocalTranslation(super.location);
            an.playInstance();
        }
    }
}
