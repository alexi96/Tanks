package visual;

import com.jme3.input.RawInputListener;
import com.jme3.input.event.JoyAxisEvent;
import com.jme3.input.event.JoyButtonEvent;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.input.event.TouchEvent;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class Component implements RawInputListener {

    protected int x;
    protected int y;
    protected BufferedImage image;
    protected Panel parent;

    public Component() {
    }

    public Component(int x, int y) {
        this.location(x, y);
    }

    public Component(int x, int y, int w, int h) {
        this(x, y);
        this.size(w, h);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int width() {
        return image.getWidth();
    }

    public int height() {
        return image.getHeight();
    }

    public void location(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void size(int w, int h) {
        this.image = new BufferedImage(w, h, BufferedImage.TYPE_4BYTE_ABGR);
    }

    public void fitParrent() {
        if (this.parent != null) {
            this.size(this.parent.width(), this.parent.height());
        }
    }

    public void bounds(int x, int y, int w, int h) {
        this.location(x, y);
        this.size(w, h);
    }

    public Frame frame() {
        if (this.parent == null) {
            return null;
        }
        if (this.parent instanceof Frame) {
            return (Frame) this.parent;
        }
        return this.parent.frame();
    }

    public void invalidate() {
        Frame f = this.frame();
        if (f != null) {
            f.valid = false;
        }
    }

    @Override
    public void beginInput() {
    }

    @Override
    public void endInput() {
    }

    @Override
    public void onJoyAxisEvent(JoyAxisEvent evt) {
    }

    @Override
    public void onJoyButtonEvent(JoyButtonEvent evt) {
    }

    @Override
    public void onMouseMotionEvent(MouseMotionEvent evt) {
    }

    @Override
    public void onMouseButtonEvent(MouseButtonEvent evt) {
    }

    @Override
    public void onKeyEvent(KeyInputEvent evt) {
    }

    @Override
    public void onTouchEvent(TouchEvent evt) {
    }

    protected void paint() {
        for (int yc = 0; yc < this.height(); ++yc) {
            for (int xc = 0; xc < this.width(); ++xc) {
                this.image.setRGB(xc, yc, 0x00000000);
            }
        }

        Graphics g = this.image.getGraphics();
        this.paint(g);
    }

    public void paint(Graphics g) {
    }
}
