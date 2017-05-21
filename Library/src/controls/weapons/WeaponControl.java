package controls.weapons;

import com.jme3.scene.Spatial;
import controllers.GameController;
import controls.GameControl;
import controls.entityes.PlayerControl;
import synchronization.SyncManager;
import utilities.LoadingManager;

public abstract class WeaponControl extends GameControl {

    protected float damage;
    protected float fireRate;
    protected transient boolean fireing;
    protected float state;
    protected int ammo;
    protected transient PlayerControl holder;
    protected transient Spatial barrel;

    public WeaponControl() {
    }

    public WeaponControl(float damage, float fireRate, int ammo) {
        this.damage = damage;
        this.fireRate = fireRate;
        this.ammo = ammo;
    }

    public float getDamage() {
        return damage;
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public float getFireRate() {
        return fireRate;
    }

    public void setFireRate(float fireRate) {
        this.fireRate = fireRate;
    }

    public float getState() {
        return state;
    }

    public int getAmmo() {
        return ammo;
    }

    public void setAmmo(int ammo) {
        this.ammo = ammo;
    }

    public void fire(boolean f) {
        this.fireing = f;
    }

    public void secondaryFire(boolean f) {
        this.fire(f);
    }

    public PlayerControl getHolder() {
        return holder;
    }

    public void setHolder(PlayerControl holder) {
        this.holder = holder;
    }

    @Override
    public void setSpatial(Spatial spatial) {

        SyncManager sm = GameController.getInstance().getSynchronizer();
        if (sm != null) {
            if (spatial != null) {
                this.barrel = LoadingManager.findByName(spatial, "Barrel");
            } else {
                this.barrel = null;
            }
        }

        super.setSpatial(spatial);
    }

    protected boolean fire() {
        if (ammo <= 0) {
            return false;
        }

        --ammo;
        state = this.fireRate;
        return true;
    }

    @Override
    public void update(float tpf) {
        SyncManager sm = GameController.getInstance().getSynchronizer();
        if (sm == null) {
            return;
        }

        if (state > 0) {
            state -= tpf;
            if (state < 0) {
                state = 0;
            }
        }

        if (state == 0 && fireing) {
            this.fire();
        }
    }
}
