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

        g.setColor(Color.WHITE);
        String title = this.player.getClass().getSimpleName();
        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, super.height() / 4));
        FontMetrics m = g.getFontMetrics();
        g.drawString(title, (super.width() - m.stringWidth(title)) / 2, m.getAscent() / 2);
    }

}
