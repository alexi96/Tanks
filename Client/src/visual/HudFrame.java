package visual;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

public class HudFrame extends Frame {

    public HudFrame() {
        super.bounds(10, 10, 300, 100);
    }
    
    @Override
    public void paint(Graphics g) {
        g.setColor(new Color(0x7f007f00, true));
        g.fillRoundRect(0, 0, super.width(), super.height(), 80, 40);

        g.setColor(Color.RED);
        String text = "Viata: xxx/xxx";
        g.setFont(new Font(Font.SANS_SERIF, Font.ITALIC & Font.BOLD, 30));
        FontMetrics m = g.getFontMetrics();
        g.drawString(text, (super.width() - m.stringWidth(text)) / 2, (super.height() + m.getAscent() / 2) / 2);
    }
}
