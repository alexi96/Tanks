package visual;

import controls.weapons.WeaponControl;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

public class WeaponInfo extends Component {

    private WeaponControl weapon;

    public WeaponInfo() {
    }

    public WeaponInfo(int x, int y) {
        super(x, y);
    }

    public WeaponInfo(int x, int y, int w, int h) {
        super(x, y, w, h);
    }

    public WeaponControl getWeapon() {
        return weapon;
    }

    public void setWeapon(WeaponControl weapon) {
        this.weapon = weapon;
    }

    @Override
    public void paint(Graphics g) {
        g.setColor(new Color(0x7f0000ff, true));
        g.fillRect(0, 0, super.width(), super.height());

        int line = 0;
        g.setColor(Color.WHITE);
        String title = this.weapon.getClass().getSimpleName();
        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 20));
        FontMetrics m = g.getFontMetrics();
        g.drawString(title, (super.width() - m.stringWidth(title)) / 2, m.getHeight());
        line += m.getHeight();

        String text = "Damage: " + this.weapon.getDamage();
        g.setColor(Color.LIGHT_GRAY);
        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 20));
        m = g.getFontMetrics();
        g.drawString(text, (super.width() - m.stringWidth(text)) / 2, line + m.getHeight());
        line += m.getHeight();
        
        text = "Fire rate: " + this.weapon.getFireRate();
        g.setColor(Color.LIGHT_GRAY);
        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 20));
        m = g.getFontMetrics();
        g.drawString(text, (super.width() - m.stringWidth(text)) / 2, line + m.getHeight());
        line += m.getHeight();
        
        text = "Ammo: " + this.weapon.getAmmo();
        g.setColor(Color.LIGHT_GRAY);
        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 20));
        m = g.getFontMetrics();
        g.drawString(text, (super.width() - m.stringWidth(text)) / 2, line + m.getHeight());
        line += m.getHeight();
    }
}
