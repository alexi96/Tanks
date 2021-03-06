package controls.projectiles;

import com.jme3.bounding.BoundingBox;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import controllers.GameController;
import controls.DestroyableControl;
import controls.GameControl;
import controls.entityes.PlayerControl;
import java.util.List;
import utilities.LoadingManager;
import utilities.observer.ScoreObserverSubject;

public abstract class ExplosibileProjectile extends ProjectileControl {

    protected transient float explosionRange;

    public ExplosibileProjectile() {
    }

    public ExplosibileProjectile(Vector3f direction, float speed, float size, float damage, PlayerControl source, float range) {
        super(direction, speed, size, damage, source, range);
    }

    public ExplosibileProjectile(float explosionRange, Vector3f direction, float speed, float size, float damage, PlayerControl source, float range) {
        super(direction, speed, size, damage, source, range);
        this.explosionRange = explosionRange;
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
                this.blow(null, r.getContactPoint().subtract(super.direction.mult(super.size)));
                return true;
            }
            if (gc == this) {
                continue;
            }
            if (!(gc instanceof DestroyableControl)) {
                this.blow(null, r.getContactPoint().subtract(super.direction.mult(super.size)));
                return true;
            }
            if (gc == this.source) {
                continue;
            }
            DestroyableControl dc = (DestroyableControl) gc;
            this.blow(dc, r.getContactPoint().subtract(super.direction.mult(super.size)));
            this.hit(dc, r.getContactNormal().negate(), r.getContactPoint());
            return true;
        }

        return false;
    }

    public void hitExplosion(DestroyableControl d, Vector3f dir, Vector3f loc, float dist) {
        if (dist < 0) {
            dist = 0;
        }
        dist /= this.explosionRange;
        dist = 1 - dist;
        
        boolean died = d.hit(this.damage * dist, dir, loc);
        if (!(d instanceof PlayerControl)) {
            return;
        }

        ScoreObserverSubject so = GameController.getInstance().getScoreSubject();
        so.hitted(this.source, (PlayerControl) d, this.damage);
        if (died) {
            so.killed(this.source, (PlayerControl) d);
        }
    }

    protected void blow(DestroyableControl d, Vector3f loc) {
        Node root = GameController.getInstance().getApplication().getRootNode();

        List<Spatial> sp = root.getChildren();
        for (Spatial s : sp) {
            DestroyableControl dc = s.getControl(DestroyableControl.class);
            if (dc == null || dc == d) {
                continue;
            }
            BoundingBox box = (BoundingBox) s.getWorldBound();
            float sz = (box.getXExtent() + box.getYExtent() + box.getZExtent());
            sz /= 3f;
            Vector3f center = box.getCenter();
            float dist = center.subtract(loc).length();
            if (dist > this.explosionRange + sz) {
                continue;
            }

            this.hitExplosion(dc, center.subtract(loc).normalize(), center, dist - sz);
        }
    }
}
