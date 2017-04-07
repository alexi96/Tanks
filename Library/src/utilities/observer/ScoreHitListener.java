package utilities.observer;

import controls.entityes.PlayerControl;

@FunctionalInterface
public interface ScoreHitListener {

    void playerHit(PlayerControl source, PlayerControl dest, float val);
}
