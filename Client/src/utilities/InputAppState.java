package utilities;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.controls.ActionListener;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import connection.ControlsConnection;
import controls.entityes.PlayerControl;

public class InputAppState extends ClientAppState implements ActionListener {

    private PlayerControl player;
    private ControlsConnection controls;
    private Camera camera;
    private Vector3f lastLook = new Vector3f();

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
        PlayerControl result = this.controls.spawn();
        this.managed.put(result.getId(), result);
        result.create();

        this.player = result;
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.camera = app.getCamera();
    }

    @Override
    public void cleanup() {
        this.camera = null;
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if (this.player == null) {
            return;
        }

        this.controls.command(this.player.getId(), name, isPressed);
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);

        if (this.camera.getDirection().equals(this.lastLook)) {
            return;
        }

        this.lastLook.set(this.camera.getDirection());
        this.controls.command(this.player.getId(), this.lastLook);
    }
}
