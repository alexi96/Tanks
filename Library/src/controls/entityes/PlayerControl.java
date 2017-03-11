package controls.entityes;

import com.jme3.input.controls.ActionListener;
import com.jme3.math.Vector3f;
import controls.DestroyableControl;

public abstract class PlayerControl extends DestroyableControl implements ActionListener {

    public static int serverId;
    public static final String DOWN = "DOWN";
    public static final String UP = "UP";
    public static final String LEFT = "LEFT";
    public static final String RIGHT = "RIGHT";
    public static final String FIRE = "FIRE";
    public static final String SECONDARY_FIRE = "SECONDARY_FIRE";
    public static final String SPACE = "SPACE";
    public static final String CTRL = "CTRL";
    public static final String SHIFT = "SHIFT";
    public static final String SWAP = "SWAP";
    
    public static final String ALT_UP = "ALT_UP";
    public static final String ALT_DOWN = "ALT_DOWN";
    public static final String ALT_LEFT = "ALT_LEFT";
    public static final String ALT_RIGHT = "ALT_RIGHT";
    
    public static final String[] MAPPINGS = {UP, DOWN, LEFT, RIGHT, FIRE, SECONDARY_FIRE, SPACE, CTRL, SHIFT, SWAP, ALT_UP, ALT_DOWN, ALT_LEFT, ALT_RIGHT};
    protected String name = "";
    protected transient boolean up;
    protected transient boolean down;
    protected transient boolean left;
    protected transient boolean right;
    protected transient Vector3f look = new Vector3f(0, 0, 1);
    protected transient boolean fire;
    protected transient boolean secondaryFire;
    protected transient boolean space;
    protected transient boolean ctrl;
    protected transient boolean shift;
    protected transient boolean swap;

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
            case PlayerControl.DOWN:
                this.down = isPressed;
                break;
            case PlayerControl.LEFT:
                this.left = isPressed;
                break;
            case PlayerControl.RIGHT:
                this.right = isPressed;
                break;
            case PlayerControl.FIRE:
                this.fire = isPressed;
                break;
            case PlayerControl.SECONDARY_FIRE:
                this.secondaryFire = isPressed;
                break;
            case PlayerControl.SPACE:
                this.space = isPressed;
                break;
            case PlayerControl.CTRL:
                this.ctrl = isPressed;
                break;
            case PlayerControl.SHIFT:
                this.shift = isPressed;
                break;
            case PlayerControl.SWAP:
                this.swap = isPressed;
                break;
        }
    }
}