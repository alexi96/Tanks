package controls.projectiles;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioData;
import com.jme3.audio.AudioNode;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import controllers.GameController;
import controls.entityes.PlayerControl;
import synchronization.SyncManager;
import utilities.Explosion;

public class RocketControl extends ExplosibileProjectile {

    private final static AudioNode FIRE = new AudioNode(GameController.getInstance().getApplication().getAssetManager(), "Sounds/RPGFire.wav", AudioData.DataType.Buffer);
    protected final static AudioNode EXPLOSION = new AudioNode(GameController.getInstance().getApplication().getAssetManager(), "Sounds/Explosion.wav", AudioData.DataType.Buffer);

    static {
        FIRE.setVolume(3);
        EXPLOSION.setVolume(5);
    }

    public RocketControl() {
    }

    public RocketControl(Vector3f direction, float speed, float damage, PlayerControl source, float range) {
        super(5f, direction, speed, 0.25f, damage, source, range);
    }

    @Override
    public void create() {
        SimpleApplication app = GameController.getInstance().getApplication();

        Spatial s = ProjectileControl.MODEL.getChild("Rocket").clone();
        s.scale(5);
        Quaternion rot = new Quaternion();
        rot.lookAt(super.direction, Vector3f.UNIT_Y);
        s.setLocalRotation(rot);
        s.setLocalTranslation(super.location);
        s.addControl(this);
        app.getRootNode().attachChild(s);

        if (GameController.getInstance().getSynchronizer() != null) {
            return;
        }

        AudioNode an = RocketControl.FIRE.clone();
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

    public static AudioNode getExplosion() {
        return EXPLOSION;
    }
}
