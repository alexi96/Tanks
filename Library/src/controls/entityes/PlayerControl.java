package controls.entityes;

import com.jme3.input.controls.ActionListener;
import com.jme3.math.Vector3f;
import controls.DestroyableControl;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class PlayerControl extends DestroyableControl implements ActionListener {

    static {
        try {
            ArrayList<String> mappings = new ArrayList<>();
            Field[] fs = PlayerControl.class.getDeclaredFields();
            for (Field f : fs) {
                int mod = f.getModifiers();
                if (!f.getType().equals(String.class)) {
                    continue;
                }
                if (!Modifier.isStatic(mod)) {
                    continue;
                }
                if (!Modifier.isFinal(mod)) {
                    continue;
                }
                if (!Character.isUpperCase(f.getName().charAt(0))) {
                    continue;
                }

                mappings.add(f.getName());
            }

            Field f = PlayerControl.class.getDeclaredField("MAPPINGS");
            Field m = f.getClass().getDeclaredField("modifiers");
            m.setAccessible(true);
            f.setAccessible(true);
            m.set(f, f.getModifiers() ^ Modifier.FINAL);
            f.set(null, new String[mappings.size()]);

            for (int i = 0; i < PlayerControl.MAPPINGS.length; i++) {
                PlayerControl.MAPPINGS[i] = mappings.get(i);
            }
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
            Logger.getLogger(PlayerControl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public static final String UP = "UP";
    public static final String DOWN = "DOWN";
    public static final String[] MAPPINGS = null;

    
    protected String name;
    protected boolean up;
    protected boolean down;
    protected boolean left;
    protected boolean right;
    protected Vector3f look;
    protected boolean fire;
    protected boolean secondaryFire;
    protected boolean space;
    protected boolean ctrl;
    protected boolean shift;
    protected boolean swap;
    public static void main(String[] args) {
        
    }
    public PlayerControl() {
    }

    public PlayerControl(String name, boolean up, boolean down, boolean left, boolean right, Vector3f look, boolean fire) {
        this.name = name;
        this.up = up;
        this.down = down;
        this.left = left;
        this.right = right;
        this.look = look;
        this.fire = fire;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isUp() {
        return up;
    }

    public void setUp(boolean up) {
        this.up = up;
    }

    public boolean isDown() {
        return down;
    }

    public void setDown(boolean down) {
        this.down = down;
    }

    public boolean isLeft() {
        return left;
    }

    public void setLeft(boolean left) {
        this.left = left;
    }

    public boolean isRight() {
        return right;
    }

    public void setRight(boolean right) {
        this.right = right;
    }

    public Vector3f isLook() {
        return look;
    }

    public void setLook(Vector3f look) {
        this.look = look;
    }

    public boolean isFire() {
        return fire;
    }

    public void setFire(boolean fire) {
        this.fire = fire;
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        switch (name) {
            case PlayerControl.UP:
                this.up = isPressed;
                break;
        }
    }
}