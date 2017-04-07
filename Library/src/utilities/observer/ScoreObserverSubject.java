package utilities.observer;

import controls.entityes.PlayerControl;
import java.util.ArrayList;

public class ScoreObserverSubject {

    private final ArrayList<ScoreHitListener> hits = new ArrayList<>();
    private final ArrayList<ScoreKillListener> kills = new ArrayList<>();

    public void hitted(PlayerControl s, PlayerControl d, float dmg) {
        this.hits.forEach((k) -> k.playerHit(s, d, dmg));
    }

    public void addHitListener(ScoreHitListener hit) {
        this.hits.add(hit);
    }

    public void removeHitListener(ScoreHitListener hit) {
        this.hits.remove(hit);
    }
    
    public void killed(PlayerControl s, PlayerControl d) {
        this.kills.forEach((k) -> k.playerKilled(s, d));
    }

    public void addKillListener(ScoreKillListener kill) {
        this.kills.add(kill);
    }

    public void removeKillListener(ScoreKillListener kill) {
        this.kills.remove(kill);
    }
}
