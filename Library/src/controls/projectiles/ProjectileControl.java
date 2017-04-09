package controls.projectiles;

import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import controllers.GameController;
import controls.DestroyableControl;
import controls.GameControl;
import controls.entityes.PlayerControl;
import synchronization.SyncManager;
import utilities.LoadingManager;
import utilities.observer.ScoreObserverSubject;

public abstract class ProjectileControl extends GameControl {

    protected static final Node MODEL = (Node) GameController.getInstance().getLoader().loadModel("Models/Projectiles.j3o");
    protected Vector3f direction;
    protected Vector3f location;
    protected transient float speed;
    protected transient float size;
    protected transient float damage;
    protected transient PlayerControl source;
    protected transient float range;

    public ProjectileControl() {
    }

    public ProjectileControl(Vector3f direction, float speed, float size, float damage, PlayerControl source, float range) {
        this.direction = direction;
        this.speed = speed;
        this.size = size;
        this.damage = damage;
        this.source = source;
        this.range = range;
    }

    public Vector3f getDirection() {
        return direction;
    }

    public void setDirection(Vector3f direction) {
        this.direction = direction;
    }

    public Vector3f getLocation() {
        return location;
    }

    public void setLocation(Vector3f location) {
        this.location = location;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getSize() {
        return size;
    }

    public void setSize(float size) {
        this.size = size;
    }

    public float getDamage() {
        return damage;
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public PlayerControl getSource() {
        return source;
    }

    public void setSource(PlayerControl source) {
        this.source = source;
    }

    public float getRange() {
        return range;
    }

    public void setRange(float range) {
        this.range = range;
    }

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

    public void hit(DestroyableControl d, Vector3f dir, Vector3f loc) {
        boolean died = d.hit(this.damage, dir, loc);
        if (!(d instanceof PlayerControl)) {
            return;
        }
        
        ScoreObserverSubject so = GameController.getInstance().getScoreSubject();
        so.hitted(this.source, (PlayerControl) d, this.damage);
        if (died) {
            so.killed(this.source, (PlayerControl) d);
        }
    }

    
    @Override
    public void synchronize() {
        super.spatial.setLocalTranslation(this.location);
    }
    
    @Override
    public void destroy() {
        super.destroy();
    }
    
    @Override
    public void update(float tpf) {
        SyncManager manager = GameController.getInstance().getSynchronizer();
        if (manager == null) {
            return;
        }

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
