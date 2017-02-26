package controls.entityes;

import com.jme3.input.controls.ActionListener;
import controls.DestroyableControl;

public abstract class PlayerControl extends DestroyableControl implements ActionListener {

    public static final String UP = "UP";
    public static final String DOWN = "DOWN";
    protected String name;
    protected boolean up;
    protected boolean down;
    protected boolean left;
    protected boolean right;
    protected boolean look;
    protected boolean fire;

    public PlayerControl() {
    }

    public PlayerControl(String name, boolean up, boolean down, boolean left, boolean right, boolean look, boolean fire) {
        this.name = name;
        this.up = up;
        this.down = down;
        this.left = left;
        this.right = right;
        this.look = look;
        this.fire = fire;
    }

    public String getname() {
        return name;
    }

    public void setname(String name) {
        this.name = name;
    }

    public boolean getup() {
        return up;
    }

    public void setup(boolean up) {
        this.up = up;
    }

    public boolean getdown() {
        return down;
    }

    public void setdown(boolean down) {
        this.down = down;
    }

    public boolean getleft() {
        return left;
    }

    public void setleft(boolean left) {
        this.left = left;
    }

    public boolean getright() {
        return right;
    }

    public void setright(boolean right) {
        this.right = right;
    }

    public boolean getlook() {
        return look;
    }

    public void setlook(boolean look) {
        this.look = look;
    }

    public boolean getfire() {
        return fire;
    }

    public void setfire(boolean fire) {
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