package visual;

import controls.weapons.WeaponControl;
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
        String title = this.weapon.getClass().getSimpleName();
        g.setFont(new Font(Font.MONOSPACED, , x, x));
        
    }
}
