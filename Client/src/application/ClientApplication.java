package application;

import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.system.AppSettings;
import connection.ControlsConnection;
import connection.GameConnection;
import controllers.GameController;
import controls.entityes.PlayerControl;
import java.io.IOException;
import rpc.HiRpc;
import utilities.ClientAppState;
import utilities.InputAppState;
import utilities.LoadingManager;
import visual.connect.ConnectFrame;

public class ClientApplication extends SimpleApplication {

    @Override
    public void simpleInitApp() {
        super.cam.setFrustumPerspective(45, (float) super.settings.getWidth() / super.settings.getHeight(), 0.01f, 1000);
        super.flyCam.setMoveSpeed(15);
        this.initKeys();

        LoadingManager loader = new LoadingManager(this.assetManager);
        GameController.getInstance().initialise(this, super.settings, loader, null, null);

        new ConnectFrame(this).show();
    }

    public void connect(String ip) throws IOException {
        final ControlsConnection cc = HiRpc.connectSimple(ip, ClientAppState.PORT, ControlsConnection.class);
        InputAppState state = new InputAppState(cc);
        HiRpc.connectReverse(ip, GameConnection.PORT, state);
        super.stateManager.attach(state);
    }

    protected void initKeys() {
        inputManager.addMapping(PlayerControl.UP, new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping(PlayerControl.DOWN, new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping(PlayerControl.LEFT, new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping(PlayerControl.RIGHT, new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping(PlayerControl.FIRE, new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addMapping(PlayerControl.SECONDARY_FIRE, new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        inputManager.addMapping(PlayerControl.SPACE, new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping(PlayerControl.CTRL, new KeyTrigger(KeyInput.KEY_LCONTROL));
        inputManager.addMapping(PlayerControl.SHIFT, new KeyTrigger(KeyInput.KEY_LSHIFT));
        inputManager.addMapping(PlayerControl.SWAP, new KeyTrigger(KeyInput.KEY_Q));
        inputManager.addMapping(PlayerControl.RESPAWN, new KeyTrigger(KeyInput.KEY_M));

        inputManager.addMapping(PlayerControl.ALT_UP, new KeyTrigger(KeyInput.KEY_NUMPAD8));
        inputManager.addMapping(PlayerControl.ALT_DOWN, new KeyTrigger(KeyInput.KEY_NUMPAD2));
        inputManager.addMapping(PlayerControl.ALT_LEFT, new KeyTrigger(KeyInput.KEY_NUMPAD4));
        inputManager.addMapping(PlayerControl.ALT_RIGHT, new KeyTrigger(KeyInput.KEY_NUMPAD6));

        inputManager.addMapping(InputAppState.SHOW_SCORES, new KeyTrigger(KeyInput.KEY_TAB));
    }

    @Override
    public void destroy() {
        super.destroy();
        System.exit(0);
    }

    public static void main(String[] args) {
        ClientApplication app = new ClientApplication();
        boolean debug = args.length > 0 && args[0].equalsIgnoreCase("debug");
        if (debug) {
            AppSettings set = new AppSettings(true);
            set.setResolution(800, 800);
            app.setSettings(set);
            app.setShowSettings(false);
        } else {
            app.setDisplayFps(false);
            app.setDisplayStatView(false);
        }
        app.start();
    }
}
