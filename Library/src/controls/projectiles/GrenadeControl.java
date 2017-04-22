package controls.projectiles;

import com.jme3.app.SimpleApplication;
import com.jme3.audio.AudioData;
import com.jme3.audio.AudioNode;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import controllers.GameController;
import controls.entityes.PlayerControl;
import synchronization.SyncManager;
import utilities.Explosion;

public class GrenadeControl extends ExplosibileProjectile {

    private static final AudioNode FIRE = new AudioNode(GameController.getInstance().getApplication().getAssetManager(), "Sounds/GrenadeFire.wav", AudioData.DataType.Buffer);

    public GrenadeControl() {
    }

    public GrenadeControl(Vector3f direction, float speed, float damage, PlayerControl source, float range) {
        super(5, direction, speed, 0.1f, damage, source, range);
    }

    @Override
    public void create() {
        SimpleApplication app = GameController.getInstance().getApplication();

        Spatial s = ProjectileControl.MODEL.getChild("Grenade").clone();
        Quaternion rot = new Quaternion();
        rot.lookAt(super.direction, Vector3f.UNIT_Y);
        s.setLocalRotation(rot);
        s.setLocalTranslation(super.location);
        s.addControl(this);
        app.getRootNode().attachChild(s);

        if (GameController.getInstance().getSynchronizer() != null) {
            return;
        }
        AudioNode an = GrenadeControl.FIRE.clone();
        an.setLocalTranslation(super.location);
        an.playInstance();
    }

    @Override
    public void destroy() {
        super.destroy();

        SyncManager manager = GameController.getInstance().getSynchronizer();
        if (manager == null) {
            AudioNode an = RocketControl.EXPLOSION.clone();
            an.setLocalTranslation(super.location);
            an.playInstance();
        } else {
            Explosion ex = new Explosion();
            ex.setLocation(super.location);
            manager.create(ex);
        }
    }

    @Override
    public void update(float tpf) {
        SyncManager manager = GameController.getInstance().getSynchronizer();
        if (manager == null) {
            return;
        }

        this.direction.y -= tpf / 5f;

        float dist = this.speed * tpf;
        if (this.colides(dist + this.size)) {
            manager.destroy(this);
            return;
        }

        this.range -= dist;
        if (this.range <= 0) {
            manager.destroy(this);
            return;
        }

        this.location.addLocal(this.direction.mult(dist));
        super.spatial.setLocalTranslation(this.location);

        manager.update(this);
    }
}
