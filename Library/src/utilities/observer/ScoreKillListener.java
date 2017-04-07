package utilities.observer;

import controls.entityes.PlayerControl;

@FunctionalInterface
public interface ScoreKillListener {
    void playerKilled(PlayerControl source, PlayerControl dest);
}
