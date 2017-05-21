package connection;

import com.jme3.math.Vector3f;
import controls.entityes.PlayerControl;
import model.Score;

public interface ControlsConnection {

    void command(int id, String com, boolean pressed);

    void command(int id, Vector3f look);

    PlayerControl spawn(PlayerControl pl);
    
    Score[] requestScores();
}
