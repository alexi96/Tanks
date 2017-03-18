package utilities;

import com.jme3.math.Vector3f;
import connection.ControlsConnection;
import controllers.GameController;
import controls.entityes.PlayerControl;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import synchronization.Synchronizer;

public class ControlsAppState extends ServerAppState implements ControlsConnection {

    protected final TreeMap<Integer, PlayerControl> players = new TreeMap<>();

    @Override
    public void command(int id, String com, boolean pressed) {
        PlayerControl pc = this.players.get(id);
        pc.onAction(com, pressed, 0);
    }

    @Override
    public void command(int id, Vector3f look) {
        PlayerControl pc = this.players.get(id);
        pc.setLook(look);
    }

    @Override
    public PlayerControl spawn(final PlayerControl pl) {
        try {
            final GameController gc = GameController.getInstance();
            return gc.getApplication().enqueue(() -> {
                gc.getSynchronizer().create(pl);
                players.put(pl.getId(), pl);
                return pl;
            }).get();
        } catch (InterruptedException | ExecutionException ex) {
            Logger.getLogger(ControlsAppState.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
