package visual;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;

public class Button extends Label {

    protected Color border = Color.WHITE;

    public Button() {
    }

    public Button(int x, int y) {
        super(x, y);
    }

    public Button(int x, int y, int w, int h) {
        super(x, y, w, h);
    }

    public Button(String text) {
        super(text);
    }

    public Color getBorder() {
        return border;
    }

    public void setBorder(Color border) {
        this.border = border;
    }

    @Override
    public void paint(Graphics g) {
        g.setColor(this.background);
        g.fillRect(0, 0, super.width(), super.height());

        int brt = super.width() + super.height();
        brt /= 40;
        for (int i = 0; i < brt; ++i) {
            g.setColor(new Color(this.border.getRed() * i / brt, this.border.getGreen() * i / brt, this.border.getBlue() * i / brt, this.border.getAlpha()));
            g.drawRect(i, i, super.width() - i * 2, super.height() - i * 2);
        }
        
        g.setColor(this.foreground);
        g.setFont(this.font);
        FontMetrics m = g.getFontMetrics();
        g.drawString(text, (super.width() - m.stringWidth(text)) / 2, (super.height() + m.getAscent() / 2) / 2);
    }
}
