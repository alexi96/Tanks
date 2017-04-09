package utilities;

import com.jme3.app.state.AppStateManager;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import connection.ControlsConnection;
import controllers.GameController;
import controls.entityes.PlayerControl;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Score;
import utilities.observer.ObserverListener;
import utilities.observer.ScoreKillListener;

public class ControlsAppState extends ServerAppState implements ControlsConnection {

    private static final File SCORES = new File("Scores");
    private static final String SPAWN_NAME = "$Spawn";
    private static Random rand = new Random();

    protected final TreeMap<Integer, PlayerControl> players = new TreeMap<>();
    protected final ArrayList<Vector3f> spawnPoints = new ArrayList<>();
    private final ObserverListener<PlayerControl> deathListener = (p) -> {
        this.players.remove(p.getId());

        while (this.scores.size() > 10) {
            this.scores.remove(this.scores.first());
        }

        this.saveScores();
    };
    private final ScoreKillListener killListener = (s, d) -> {
        if (s != d) {
            return;
        }
        this.scoreMaps.get(s).addPoints(1);
    };
    private final TreeMap<PlayerControl, Score> scoreMaps = new TreeMap<>();
    private final TreeSet<Score> scores = new TreeSet<>();

    public ControlsAppState() {
    }

    public void findSpawnPoints(Node map) {
        this.spawnPoints.clear();
        this.findSpawnPointsDeep(map, this.spawnPoints);
    }

    private void findSpawnPointsDeep(Spatial n, ArrayList<Vector3f> res) {
        if (n.getName().endsWith(ControlsAppState.SPAWN_NAME)) {
            res.add(n.getWorldTranslation());
        }

        if (n instanceof Node) {
            List<Spatial> chs = ((Node) n).getChildren();
            chs.forEach((ch) -> this.findSpawnPointsDeep(ch, res));
        }
    }

    @Override
    public void stateAttached(AppStateManager stateManager) {
        this.findSpawnPoints(GameController.getInstance().getApplication().getRootNode());
        GameController.getInstance().getDeathSubject().addListener(this.deathListener);
        GameController.getInstance().getScoreSubject().addKillListener(this.killListener);

        this.openScores();
    }

    @Override
    public void stateDetached(AppStateManager stateManager) {
        super.stateDetached(stateManager);
        GameController.getInstance().getDeathSubject().removeListener(this.deathListener);
        GameController.getInstance().getScoreSubject().removeKillListener(this.killListener);
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
        final Vector3f spawn = this.spawnPoints.get(ControlsAppState.rand.nextInt(this.spawnPoints.size()));

        try {
            final GameController gc = GameController.getInstance();
            return gc.getApplication().enqueue(() -> {
                gc.getSynchronizer().create(pl);
                players.put(pl.getId(), pl);
                Score sc = new Score(0, pl.getName(), new Date());
                this.scoreMaps.put(pl, sc);
                this.scores.add(sc);
                pl.moveTo(spawn);
                return pl;
            }).get();
        } catch (InterruptedException | ExecutionException ex) {
            Logger.getLogger(ControlsAppState.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private void saveScores() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(ControlsAppState.SCORES))) {
            out.writeObject(this.scores);
        } catch (IOException ex) {
            Logger.getLogger(ControlsAppState.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void openScores() {
        try (ObjectInputStream out = new ObjectInputStream(new FileInputStream(ControlsAppState.SCORES))) {
            Set<Score> scs = (Set<Score>) out.readObject();
            this.scores.addAll(scs);
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(ControlsAppState.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
