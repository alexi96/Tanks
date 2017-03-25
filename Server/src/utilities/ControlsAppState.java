package utilities;

import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import connection.ControlsConnection;
import controllers.GameController;
import controls.entityes.PlayerControl;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ControlsAppState extends ServerAppState implements ControlsConnection {

    private static final String SPAWN_NAME = "$Spawn";

    protected final TreeMap<Integer, PlayerControl> players = new TreeMap<>();

    public static ArrayList<Vector3f> findSpawnPoints(Node map) {
        ArrayList<Vector3f> res = new ArrayList<>();
        ControlsAppState.findSpawnPointsDeep(map, res);
        return res;
    }

    private static void findSpawnPointsDeep(Spatial n, ArrayList<Vector3f> res) {
        if (n.getName().endsWith(ControlsAppState.SPAWN_NAME)) {
            res.add(n.getWorldTranslation());
        }

        if (n instanceof Node) {
            List<Spatial> chs = ((Node) n).getChildren();
            chs.forEach((ch) -> ControlsAppState.findSpawnPointsDeep(ch, res));
        }
    }

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
