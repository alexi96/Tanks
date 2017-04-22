package visual;

import com.jme3.system.AppSettings;
import controllers.GameController;
import controls.entityes.PlayerControl;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

public class HudFrame extends Frame {

    private PlayerControl player;

    public HudFrame() {
        AppSettings set = GameController.getInstance().getSettings();
        super.bounds(0, 0, 800, 800);
        GameController.getInstance().getApplication().setDisplayStatView(false);
    }

    public PlayerControl getPlayer() {
        return player;
    }

    public void setPlayer(PlayerControl player) {
        this.player = player;
    }

    @Override
    public void paint(Graphics g) {

        /*g.setColor(new Color(0x7f007f00, true));
        g.fillRect(0, 0, 200, 200);

        

        g.setColor(new Color(0x7f007f00, true));
        g.fillRect(0, 140, 200, 200);

        g.setColor(new Color(0x7f007f00, true));
        g.fillRect(140, 0, 200, 200);

        g.setColor(new Color(0x7f007f00, true));
        g.fillRect(140, 140, 200, 200);*/
        int hw = 200;
        int hh = 100;
        g.setColor(new Color(0x7f007f00, true));
        g.fillRect(0, super.height() - hh, hw, hh);

        g.setColor(Color.RED);
        String text = "Health: " + (int) this.player.getHealth() + "/" + (int) this.player.getMaxHealth();
        g.setFont(new Font(Font.SANS_SERIF, Font.ITALIC & Font.BOLD, 25));
        FontMetrics m = g.getFontMetrics();
        g.drawString(text, (hw - m.stringWidth(text)) / 2, super.height() - (hh - m.getHeight() / 2) / 2);

        g.setColor(new Color(0x7f007f00, true));
        g.fillRect(super.width() - hw, super.height() - hh, hw, hh);

        g.setColor(Color.BLUE);
        text = "Ammo: " + this.player.getSelected().getAmmo();
        g.setFont(new Font(Font.SANS_SERIF, Font.ITALIC & Font.BOLD, 25));
        m = g.getFontMetrics();
        g.drawString(text, super.width() - (hw + m.stringWidth(text)) / 2, super.height() - (hh - m.getHeight() / 2) / 2);

        // de ce naiba nu mi deseneaza 4 dreptunghiuri in coluri diferite?
        //pe asta nu o inteleg
        //HELP PLZ
    }
}
