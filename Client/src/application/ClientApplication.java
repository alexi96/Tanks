package application;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.Vector3f;
import com.jme3.system.AppSettings;
import connection.ControlsConnection;
import connection.GameConnection;
import controllers.GameController;
import controls.entityes.PlayerControl;
import controls.entityes.RobotControl;
import controls.entityes.TankControl;
import controls.maps.TestMap;
import controls.weapons.AutoShotgun;
import controls.weapons.MachineGun;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.logging.Level;
import java.util.logging.Logger;
import rpc.HiRpc;
import synchronization.SyncManager;
import synchronization.Synchronizer;
import utilities.ClientAppState;
import utilities.InputAppState;
import utilities.LoadingManager;
import visual.Component;
import visual.Frame;

public class ClientApplication extends SimpleApplication {

    private String ip;

    @Override
    public void simpleInitApp() {
        super.cam.setFrustumPerspective(45, (float) super.settings.getWidth() / super.settings.getHeight(), 0.01f, 1000);
        super.flyCam.setMoveSpeed(15);
        this.initKeys();

        LoadingManager loader = new LoadingManager(this.assetManager);
        GameController.getInstance().initialise(this, super.settings, loader, null, null);

        if (this.ip != null) {
            try {
                super.flyCam.setMoveSpeed(0);
                final ControlsConnection cc = HiRpc.connectSimple(this.ip, ClientAppState.PORT, ControlsConnection.class);
                InputAppState state = new InputAppState(cc);
                HiRpc.connectReverse(this.ip, GameConnection.PORT, state);
                super.stateManager.attach(state);

                RobotControl rob = new RobotControl();
                rob.setPrimary(new MachineGun());
                rob.setSecondary(new AutoShotgun());

                state.spawn(rob);

                inputManager.addListener(state, PlayerControl.MAPPINGS);
            } catch (Exception ex) {
                Logger.getLogger(ClientApplication.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            BulletAppState bulletState = new BulletAppState();
            super.stateManager.attach(bulletState);
            SyncManager sm = new SyncManager() {
                @Override
                public void create(Synchronizer c) {
                    c.create();
                }

                @Override
                public void update(Synchronizer c) {
                    c.synchronize();
                }

                @Override
                public void destroy(Synchronizer c) {
                    c.destroy();
                }
            };

            GameController.getInstance().initialise(this, super.settings, loader, bulletState.getPhysicsSpace(), sm);

            sm.create(new TestMap());
            //sm.create(new TestBall());

            TankControl tc = new TankControl() {
                @Override
                public void update(float tpf) {
                    super.update(tpf);
                    cam.setLocation(spatial.getWorldTranslation().add(Vector3f.UNIT_Y).subtract(cam.getDirection().mult(5)));
                }
            };
            tc.setId(1);
            PlayerControl.serverId = 1;
            sm.create(tc);

            super.inputManager.addListener(tc, PlayerControl.MAPPINGS);
        }

        final Frame f = new Frame();
        f.size(200, 100);//daca nu setezi size la o componenta da exceptie
        f.location(0, 0);

        Component c = new Component(0, 0, 200, 100) {//size se seteaza in constructor
            @Override
            public void paint(Graphics g) {
                g.setColor(new Color(0x7f007f00, true));
                g.fillRoundRect(0, 0, super.width(), super.height(), 80, 40);

                g.setColor(Color.RED);
                String text = "Viata: xxx/xxx";
                g.setFont(new Font(Font.SANS_SERIF, Font.ITALIC & Font.BOLD, 30));
                FontMetrics m = g.getFontMetrics();
                g.drawString(text, (super.width() - m.stringWidth(text)) / 2, (super.height() + m.getAscent() / 2) / 2);
            }

            @Override
            public void onMouseButtonEvent(MouseButtonEvent evt) {
                f.hide();// pentru a modifica cand se face clik pe o componenta (f e final ca sa poate fi accesata in blocul asta)
            }
        };

        f.add(c);//poti sa suprascrii paint din fereastra si sa nu mai creezi componente
        //f.show();
    }

    private void initKeys() {
        inputManager.addMapping("DOWN", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("UP", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("LEFT", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("RIGHT", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("FIRE", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addMapping("SECONDARY_FIRE", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        inputManager.addMapping("SPACE", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("CTRL", new KeyTrigger(KeyInput.KEY_LCONTROL));
        inputManager.addMapping("SHIFT", new KeyTrigger(KeyInput.KEY_LSHIFT));
        inputManager.addMapping("SWAP", new KeyTrigger(KeyInput.KEY_Q));
        
        inputManager.addMapping(PlayerControl.ALT_UP, new KeyTrigger(KeyInput.KEY_NUMPAD8));
        inputManager.addMapping(PlayerControl.ALT_DOWN, new KeyTrigger(KeyInput.KEY_NUMPAD2));
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
        app.ip = null;
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
