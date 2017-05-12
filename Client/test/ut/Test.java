package ut;

import application.ClientApplication;
import com.jme3.system.AppSettings;
import controllers.GameController;
import java.io.File;
import utilities.LoadingManager;
import utilities.MappedSettings;
import visual.SpawnFrame;
import visual.connect.ConnectFrame;

public class Test {

    public static void main(String[] args) {
        ClientApplication app = new ClientApplication() {
            @Override
            public void simpleInitApp() {
                super.cam.setFrustumPerspective(45, (float) super.settings.getWidth() / super.settings.getHeight(), 0.01f, 1000);
                super.flyCam.setMoveSpeed(15);

                LoadingManager loader = new LoadingManager(this.assetManager);
                GameController.getInstance().initialise(this, super.settings, loader, null, null);
            }
        };
        AppSettings set = new AppSettings(true);
        set.setResolution(800, 800);
        app.setSettings(set);
        app.setShowSettings(false);
        app.start();

        app.enqueue(Test::connect);
        app.enqueue(Test::spawn);

        Test.save();    
    }

    private static void connect() {
        ConnectFrame cf = new ConnectFrame();
        cf.show();
    }

    private static void spawn() {
        SpawnFrame sw = new SpawnFrame();
        sw.show();
    }

    private static void save() {
        MappedSettings<Integer> ms = MappedSettings.<Integer>getInstance(Test.class);
        File f = new File("utSettings");
        ms.setFile(f);
        if (!ms.open(true)) {
            ms.mapSetting("set1", 6);
            ms.mapSetting("set2", 7);
            ms.mapSetting("set3", 8);
            ms.save();
        }

        System.out.println(ms.findSetting("set1"));
        System.out.println(ms.findSetting("set2"));
        System.out.println(ms.findSetting("set3"));
    }
}
