package utilities;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.controls.ActionListener;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import connection.ControlsConnection;
import controllers.GameController;
import controls.entityes.DroneControl;
import controls.entityes.PlayerControl;
import controls.weapons.GrenadeLauncher;
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
        PlayerControl result = this.controls.spawn(player);
        PlayerControl.serverId = result.getId();
        this.managed.put(result.getId(), result);
        result.create();

        this.player = result;
    }

    private void death(PlayerControl p) {
        if (p.getId() != this.player.getId()) {
            return;
        }

        this.player = null;
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
            if (name.equals(PlayerControl.SPACE)) {
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

        if (this.camera.getDirection().equals(this.lastLook)) {
            return;
        }

        final float min = FastMath.DEG_TO_RAD * 20;
        final float max = -FastMath.DEG_TO_RAD * 45;

        float[] angs = this.camera.getRotation().toAngles(null);
        if (angs[0] > min && angs[0] < FastMath.PI) {
            angs[0] = min;
            this.camera.setRotation(new Quaternion(angs));
        } else if (angs[0] < max && angs[0] > -FastMath.PI) {
            angs[0] = max;
            this.camera.setRotation(new Quaternion(angs));
        }

        this.lastLook.set(this.camera.getDirection());
        this.controls.command(this.player.getId(), this.lastLook.clone());
    }
}
