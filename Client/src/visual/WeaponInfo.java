package visual;

import controls.weapons.WeaponControl;
import java.awt.Color;
import java.awt.Font;
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

//        String title = this.weapon.getClass().getSimpleName();
        //g.setFont(new Font(Font.MONOSPACED, , x, x));
    }
}
