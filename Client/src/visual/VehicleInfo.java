package visual;

import controls.entityes.PlayerControl;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

public class VehicleInfo extends Component {

    private PlayerControl player;

    public PlayerControl getPlayer() {
        return player;
    }

    public void setPlayer(PlayerControl player) {
        this.player = player;
    }

    @Override
    public void paint(Graphics g) {
        g.setColor(new Color(0x7f0000ff, true));
        g.fillRect(0, 0, super.width(), super.height());

        int line = 0;

        g.setColor(Color.WHITE);
        String title = this.player.getClass().getSimpleName();
        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, super.height() / 5));
        FontMetrics m = g.getFontMetrics();
        g.drawString(title, (super.width() - m.stringWidth(title)) / 2, m.getHeight());
        line += m.getHeight();

        String text = "Health: " + this.player.getMaxHealth();

        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, super.height() / 8));
        m = g.getFontMetrics();
        g.drawString(text, super.width() / 10, line + m.getHeight());
        
        text = "Armor: " + this.player.getArmor();
        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, super.height() / 8));
        m = g.getFontMetrics();
        g.drawString(text, super.width() * 9 / 10 - m.stringWidth(text), line + m.getHeight());

    }

}
