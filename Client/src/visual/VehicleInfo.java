package visual;

import controls.entityes.PlayerControl;
import java.awt.Color;
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
    }
    
    
}
