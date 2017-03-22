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
import java.util.logging.Level;
import java.util.logging.Logger;
import rpc.HiRpc;
import utilities.ClientAppState;
import utilities.InputAppState;
import utilities.LoadingManager;
import visual.HudFrame;
import visual.SpawnFrame;

public class ClientApplication extends SimpleApplication {

    private String ip;

    @Override
    public void simpleInitApp() {
        super.cam.setFrustumPerspective(45, (float) super.settings.getWidth() / super.settings.getHeight(), 0.01f, 1000);
        super.flyCam.setMoveSpeed(15);
        this.initKeys();

        LoadingManager loader = new LoadingManager(this.assetManager);
        GameController.getInstance().initialise(this, super.settings, loader, null, null);

        try {
            final ControlsConnection cc = HiRpc.connectSimple(this.ip, ClientAppState.PORT, ControlsConnection.class);
            InputAppState state = new InputAppState(cc);
            HiRpc.connectReverse(this.ip, GameConnection.PORT, state);
            super.stateManager.attach(state);

            PlayerControl pl;

            /*pl = new TankControl();
            pl.setPrimary(new CannonControl());
            pl.setSecondary(new MinigunControl());

            state.spawn(pl);*/

            inputManager.addListener(state, PlayerControl.MAPPINGS);
        } catch (IOException ex) {
            Logger.getLogger(ClientApplication.class.getName()).log(Level.SEVERE, null, ex);
        }

        HudFrame f = new HudFrame();
        //f.show();

        SpawnFrame sf = new SpawnFrame();
        //sf.show();
    }

    private void initKeys() {
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

        inputManager.addMapping(PlayerControl.ALT_UP, new KeyTrigger(KeyInput.KEY_NUMPAD8));
        inputManager.addMapping(PlayerControl.ALT_DOWN, new KeyTrigger(KeyInput.KEY_NUMPAD2));
        inputManager.addMapping(PlayerControl.ALT_LEFT, new KeyTrigger(KeyInput.KEY_NUMPAD4));
        inputManager.addMapping(PlayerControl.ALT_RIGHT, new KeyTrigger(KeyInput.KEY_NUMPAD6));
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
        app.ip = "localhost";
        if (debug) {
            if (args.length > 1) {
                app.ip = args[1];
            }
        } else {
            if (args.length > 0) {
                app.ip = args[0];
            }
        }
        app.start();
    }
}
