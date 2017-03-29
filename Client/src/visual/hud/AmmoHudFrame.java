package visual.hud;

import com.jme3.system.AppSettings;
import controllers.GameController;
import controls.entityes.PlayerControl;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import visual.Frame;

public class AmmoHudFrame extends Frame {

    private PlayerControl player;

    public AmmoHudFrame() {
        AppSettings set = GameController.getInstance().getSettings();
        super.bounds(set.getWidth() - 200, set.getHeight()- 100, 200, 100);
    }

    public AmmoHudFrame(PlayerControl player) {
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
        g.fillRect(super.width() - super.width(), super.height() - super.height(), super.width(), super.height());

        g.setColor(Color.BLUE);
        String text = "Ammo: " + this.player.getSelected().getAmmo();
        g.setFont(new Font(Font.SANS_SERIF, Font.ITALIC & Font.BOLD, 25));
        FontMetrics m = g.getFontMetrics();
        g.drawString(text, super.width() - (super.width() + m.stringWidth(text)) / 2, super.height() - (super.height() - m.getHeight() / 2) / 2);
    }
}