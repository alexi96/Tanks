package visual;

import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import java.awt.Graphics;
import java.util.ArrayList;

public class Panel extends Component {

    protected final ArrayList<Component> components = new ArrayList<>();
    protected final ArrayList<Component> adder = new ArrayList<>();
    protected final ArrayList<Component> remover = new ArrayList<>();

    public Panel() {
    }

    public Panel(int x, int y) {
        super(x, y);
    }

    public Panel(int x, int y, int w, int h) {
        super(x, y, w, h);
    }

    public ArrayList<Component> getComponents() {
        return components;
    }

    public void removeAll() {
        for (Component c : this.components) {
            this.remove(c);
        }
    }

    public void add(Component c) {
        if (c.parent != null) {
            c.parent.remove(c);
        }
        this.adder.add(c);
        c.parent = this;
    }

    public void remove(Component c) {
        this.remover.add(c);
        if (c.parent == this) {
            c.parent = null;
        }
    }

    @Override
    public void onMouseMotionEvent(MouseMotionEvent evt) {
        for (Component c : this.components) {
            int tx = evt.getX() - c.x;
            int ty = evt.getY() - c.y;

            if (tx < 0 || tx > c.width()) {
                continue;
            }
            if (ty < 0 || ty > c.height()) {
                continue;
            }

            MouseMotionEvent t = new MouseMotionEvent(tx, ty, evt.getDX(), evt.getDY(), evt.getWheel(), evt.getDeltaWheel());
            c.onMouseMotionEvent(t);
        }
    }

    @Override
    public synchronized void onMouseButtonEvent(MouseButtonEvent evt) {
        for (Component c : this.components) {
            int tx = evt.getX() - c.x;
            int ty = evt.getY() - c.y;

            if (tx < 0 || tx > c.width()) {
                continue;
            }
            if (ty < 0 || ty > c.height()) {
                continue;
            }

            MouseButtonEvent t = new MouseButtonEvent(evt.getButtonIndex(), evt.isPressed(), tx, ty);
            c.onMouseButtonEvent(t);
        }
    }

    @Override
    protected void paint() {
        for (int yc = 0; yc < this.height(); ++yc) {
            for (int xc = 0; xc < this.width(); ++xc) {
                this.image.setRGB(xc, yc, 0x00000000);
            }
        }

        Graphics g = this.image.getGraphics();
        this.paint(g);

        if (!this.adder.isEmpty()) {
            this.components.addAll(this.adder);
            this.adder.clear();
        }
        if (!this.remover.isEmpty()) {
            this.components.removeAll(this.remover);
            this.remover.clear();
        }
        for (Component c : this.components) {
            c.paint();
            g.drawImage(c.image, c.x, c.y, null);
        }
    }
}
