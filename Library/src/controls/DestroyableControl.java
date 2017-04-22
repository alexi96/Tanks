package controls;

import com.jme3.math.Vector3f;
import controllers.GameController;

public abstract class DestroyableControl extends GameControl {

    protected float health;
    protected float maxHealth;
    protected float armor;

    public DestroyableControl() {
    }

    public DestroyableControl(float health) {
        this.health = health;
        this.maxHealth = health;
    }

    public DestroyableControl(float health, float armor) {
        this(health);
        this.armor = armor;
    }

    public float getHealth() {
        return health;
    }

    public void setHealth(float health) {
        this.health = health;
    }

    public float getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(float maxHealth) {
        this.maxHealth = maxHealth;
    }

    public float getArmor() {
        return armor;
    }

    public void setArmor(float armor) {
        this.armor = armor;
    }
    
    public void resetHealth() {
        this.health = this.maxHealth;
    }
    
    public void resetHealth(float health) {
        this.health = health;
        this.maxHealth = health;
    }

    private boolean hit(float val) {
        if (this.armor >= val) {
            return false;
        }
        val -= this.armor;
        this.health -= val;
        if (this.health <= 0) {
            this.die();
            return true;
        }
        return false;
    }
    
    public boolean hit(float dmg, Vector3f dir, Vector3f loc) {
        return this.hit(dmg);
    }

    protected void die() {
        GameController.getInstance().getSynchronizer().destroy(this);
    }
}
