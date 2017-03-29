package visual;

import controls.entityes.PlayerControl;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

public class HudFrame extends Frame {

    private PlayerControl player;

    public HudFrame() {
        super.bounds(10, 10, 300, 100);
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
        g.fillRect(0, 0, 200, 200);
        
        g.setColor(new Color(0x7f007f00, true));
        g.fillRect(0, 140, 200, 200);
        
        g.setColor(new Color(0x7f007f00, true));
        g.fillRect(140, 0 , 200, 200);
        
        g.setColor(new Color(0x7f007f00, true));
        g.fillRect(140 , 140, 200, 200);
        
        // de ce naiba nu mi deseneaza 4 dreptunghiuri in coluri diferite?
        
        
        g.setColor(Color.RED);
        
        String text = "Health: xxx/xxx";
        g.setFont(new Font(Font.SANS_SERIF, Font.ITALIC & Font.BOLD, 30));
        FontMetrics m = g.getFontMetrics();
        
        g.drawString(text, (super.width() - m.stringWidth(text)) / 2, (super.height() + m.getAscent() / 2) / 2);
        //pe asta nu o inteleg
      
        g.setColor(Color.BLUE);
        String text2 = "Ammo: xxx/xxx";
        g.setFont(new Font(Font.SANS_SERIF, Font.ITALIC & Font.BOLD, 30));
        FontMetrics m2 = g.getFontMetrics();
        
        //HELP PLZ
    }
}
