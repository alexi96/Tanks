package controls.projectiles;

import com.jme3.math.Vector3f;
import controllers.GameController;
import controls.entityes.PlayerControl;
import synchronization.SyncManager;

public class MissleControl extends RocketControl {

    public MissleControl() {
    }

    public MissleControl(Vector3f direction, float speed, float damage, PlayerControl source, float range) {
        super(direction, speed, damage, source, range);
        super.explosionRange = 15f;
    }

    @Override
    public void update(float tpf) {
        SyncManager manager = GameController.getInstance().getSynchronizer();
        if (manager == null) {
            return;
        }

        this.direction.y -= tpf;

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
