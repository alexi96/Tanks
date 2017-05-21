package ut;

import application.ClientApplication;
import com.jme3.audio.AudioData;
import com.jme3.audio.AudioNode;
import com.jme3.system.AppSettings;
import controllers.GameController;
import controls.entityes.TankControl;
import controls.weapons.CannonControl;
import controls.weapons.MinigunControl;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import utilities.InputAppState;
import utilities.LoadingManager;

public class PimpMyTank {

    public static void main(String[] args) {
        ClientApplication app = new ClientApplication() {
            @Override
            public void simpleInitApp() {
                super.cam.setFrustumPerspective(45, (float) super.settings.getWidth() / super.settings.getHeight(), 0.01f, 1000);
                super.flyCam.setMoveSpeed(15);

                LoadingManager loader = new LoadingManager(this.assetManager);
                GameController.getInstance().initialise(this, super.settings, loader, null, null);

                super.initKeys();

                try {
                    super.connect("localhost");
                } catch (IOException ex) {
                    Logger.getLogger(PimpMyTank.class.getName()).log(Level.SEVERE, null, ex);
                }
                TankControl tc = new TankControl();
                tc.setPrimary(new CannonControl());
                tc.setSecondary(new MinigunControl());
                super.stateManager.getState(InputAppState.class).spawn(tc);

                AudioNode an = new AudioNode(assetManager, "Sounds/War - Low Rider.wav", AudioData.DataType.Stream);
                super.rootNode.attachChild(an);
                an.play();
            }
        };

        AppSettings set = new AppSettings(true);
        set.setResolution(800, 800);
        app.setSettings(set);
        app.setShowSettings(false);
        app.start();

    }
}
