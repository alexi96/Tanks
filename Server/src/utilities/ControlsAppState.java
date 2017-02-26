package utilities;

import connection.ControlsConnection;
import controls.entityes.PlayerControl;
import java.util.TreeMap;

public class ControlsAppState extends ServerAppState implements ControlsConnection {

    private TreeMap<String, PlayerControl> players = new TreeMap<>();
    
    @Override
    public void command(String id, String com, boolean pressed) {
        PlayerControl pc = this.players.get(id);
        pc.onAction(com, pressed, 0);
    }
}
