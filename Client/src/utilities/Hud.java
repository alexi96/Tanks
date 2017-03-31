package utilities;

import controls.entityes.PlayerControl;
import visual.hud.AmmoHudFrame;
import visual.hud.HealthHudFrame;

public class Hud {

    private final HealthHudFrame health = new HealthHudFrame();
    private final AmmoHudFrame ammo = new AmmoHudFrame();

    public void setPlayer(PlayerControl pc) {
        this.health.setPlayer(pc);
        this.ammo.setPlayer(pc);
    }

    public void show() {
        this.health.show();
        this.ammo.show();
    }

    public void hide() {
        this.health.hide();
        this.ammo.hide();
    }

    public void invalidate() {
        this.health.invalidate();
        this.ammo.invalidate();
    }
}
