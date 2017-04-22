package utilities;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.controls.ActionListener;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import connection.ControlsConnection;
import controllers.GameController;
import controls.entityes.PlayerControl;
import utilities.observer.ObserverListener;
import visual.SpawnFrame;

public class InputAppState extends ClientAppState implements ActionListener {

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
    }

    @Override
    public void cleanup() {
        this.camera = null;
        GameController.getInstance().getDeathSubject().removeListener(this.deathListener);
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
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
