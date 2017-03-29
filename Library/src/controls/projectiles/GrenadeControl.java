package controls.projectiles;

import com.jme3.app.SimpleApplication;
import com.jme3.audio.AudioData;
import com.jme3.audio.AudioNode;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import controllers.GameController;
import controls.entityes.PlayerControl;

public class GrenadeControl extends ProjectileControl {

    private static final AudioNode FIRE = new AudioNode(GameController.getInstance().getApplication().getAssetManager(), "Sounds/GrenadeFire.wav", AudioData.DataType.Buffer);

    public GrenadeControl() {
    }

    public GrenadeControl(Vector3f direction, float speed, float size, float damage, PlayerControl source, float range) {
        super(direction, speed, size, damage, source, range);
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
    public void update(float tpf) {
        //Scrii aici
    }
}
