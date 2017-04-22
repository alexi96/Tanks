package visual.hud;

import com.jme3.system.AppSettings;
import controllers.GameController;
import controls.entityes.PlayerControl;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import visual.Frame;

public class HealthHudFrame extends Frame {

    private PlayerControl player;

    public HealthHudFrame() {
        AppSettings set = GameController.getInstance().getSettings();
        super.bounds(0, set.getHeight()- 100, 200, 100);
    }

    public HealthHudFrame(PlayerControl player) {
        this();
        this.player = player;
    }

    public PlayerControl getPlayer() {
        return player;
    }

    public void setPlayer(PlayerControl player) {
        this.player = player;
    }

    @Override
    public void paint(Graphics g) {
        g.setColor(new Color(0x7f007f00, true));
        g.fillRect(0, 0, super.width(), super.height());

        g.setColor(Color.RED);
        String text = "Health: " + (int) this.player.getHealth() + "/" + (int) this.player.getMaxHealth();
        g.setFont(new Font(Font.SANS_SERIF, Font.ITALIC & Font.BOLD, 25));
        FontMetrics m = g.getFontMetrics();
        g.drawString(text, (super.width() - m.stringWidth(text)) / 2, super.height() - (super.height() - m.getHeight() / 2) / 2);

        /*g.setColor(new Color(0x7f007f00, true));
        g.fillRect(super.width() - hw, super.height() - hh, hw, hh);

        g.setColor(Color.BLUE);
        text = "Ammo: " + this.player.getSelected().getAmmo();
        g.setFont(new Font(Font.SANS_SERIF, Font.ITALIC & Font.BOLD, 25));
        m = g.getFontMetrics();
        g.drawString(text, super.width() - (hw + m.stringWidth(text)) / 2, super.height() - (hh - m.getHeight() / 2) / 2);*/
    }
}
