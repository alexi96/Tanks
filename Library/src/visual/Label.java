package visual;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

public class Label extends Component {

    protected String text = "";
    protected Color background = Color.LIGHT_GRAY;
    protected Color foreground = Color.BLACK;
    protected Font font = new Font(Font.MONOSPACED, Font.PLAIN, 20);

    public Label() {
    }

    public Label(int x, int y) {
        super(x, y);
    }

    public Label(int x, int y, int w, int h) {
        super(x, y, w, h);
    }

    public Label(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Color getBackground() {
        return background;
    }

    public void setBackground(Color background) {
        this.background = background;
    }

    public Color getForeground() {
        return foreground;
    }

    public void setForeground(Color foreground) {
        this.foreground = foreground;
    }

    public Font getFont() {
        return font;
    }

    public void setFont(Font font) {
        this.font = font;
    }

    @Override
    public void paint(Graphics g) {
        g.setColor(this.background);
        g.fillRect(0, 0, super.width(), super.height());

        g.setColor(this.foreground);
        g.setFont(this.font);
        FontMetrics m = g.getFontMetrics();
        g.drawString(text, (super.width() - m.stringWidth(text)) / 2, (super.height() + m.getAscent() / 2) / 2);
    }
}
