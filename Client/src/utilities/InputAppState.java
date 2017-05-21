package utilities;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.controls.ActionListener;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import connection.ControlsConnection;
import controllers.GameController;
import controls.entityes.PlayerControl;
import java.util.ArrayList;
import model.Score;
import utilities.observer.ObserverListener;
import visual.ScoresFrame;
import visual.SpawnFrame;

public class InputAppState extends ClientAppState implements ActionListener {

    public static final String SHOW_SCORES = "SHOW_SCORES";

    private PlayerControl player;
    private ControlsConnection controls;
    private Camera camera;
    private Vector3f lastLook = new Vector3f();
    private final ObserverListener<PlayerControl> deathListener = (p) -> this.death(p);
    private final SpawnFrame spawnFrame = new SpawnFrame() {
        @Override
        public void spawn(PlayerControl p) {
            InputAppState.this.spawn(p);
        }
    };
    private final Hud hud = new Hud();
    private final ScoresFrame scores = new ScoresFrame();

    public InputAppState() {
    }

    public InputAppState(ControlsConnection controls) {
        this.controls = controls;
    }

    public PlayerControl getPlayer() {
        return player;
    }

    public ControlsConnection getControls() {
        return controls;
    }

    public void setControls(ControlsConnection controls) {
        this.controls = controls;
    }

    public void spawn(PlayerControl player) {
        GameController.getInstance().getApplication().enqueue(() -> {
            PlayerControl result = this.controls.spawn(player);
            PlayerControl.serverId = result.getId();
            this.managed.put(result.getId(), result);
            result.create();

            this.player = result;

            this.hud.setPlayer(result);
            this.hud.show();

            this.spawnFrame.hide();
        });
    }

    private void death(PlayerControl p) {
        if (this.player == null) {
            return;
        }

        if (p.getId() != this.player.getId()) {
            return;
        }

        this.player = null;
        this.hud.hide();
        System.out.println(p.getName() + " died!");
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.camera = app.getCamera();
        GameController.getInstance().getDeathSubject().addListener(this.deathListener);

        String[] mappings = new String[PlayerControl.MAPPINGS.length + 1];

        System.arraycopy(PlayerControl.MAPPINGS, 0, mappings, 0, PlayerControl.MAPPINGS.length);
        mappings[mappings.length - 1] = InputAppState.SHOW_SCORES;

        GameController.getInstance().getApplication().getInputManager().addListener(this, mappings);
    }

    @Override
    public void cleanup() {
        super.cleanup();
        this.camera = null;
        GameController.getInstance().getDeathSubject().removeListener(this.deathListener);

        GameController.getInstance().getApplication().getInputManager().removeListener(this);
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if (name.equals(InputAppState.SHOW_SCORES)) {
            if (isPressed) {
                Score[] ss = this.controls.requestScores();
                ArrayList<Score> all = new ArrayList<>(10);
                for (int i = 0; i < 10; i++) {
                    if (ss[i] != null) {
                        all.add(ss[i]);
                    }
                }
                ArrayList<Score> ses = new ArrayList<>(10);
                for (int i = 10; i < 20; i++) {
                    if (ss[i] != null) {
                        ses.add(ss[i]);
                    }
                }
                this.scores.setAll(all);    
                this.scores.setSession(ses);

                this.scores.show();
            } else {
                this.scores.hide();
            }
        }

        if (this.player == null) {
            if (name.equals(PlayerControl.SPACE) && isPressed) {
                if (this.spawnFrame.visible()) {
                    this.spawnFrame.hide();
                } else {
                    this.spawnFrame.show();
                }
            }
            return;
        }

        this.controls.command(this.player.getId(), name, isPressed);
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);

        if (this.player == null) {
            return;
        }

        this.hud.invalidate();

        if (this.camera.getDirection().equals(this.lastLook)) {
            return;
        }

        this.player.restrictCamra(this.camera);

        this.lastLook.set(this.camera.getDirection());
        this.controls.command(this.player.getId(), this.lastLook.clone());
    }
}
