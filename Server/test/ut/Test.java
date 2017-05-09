package ut;

import application.ServerApplication;
import com.jme3.math.Vector3f;
import com.jme3.system.JmeContext;
import controllers.GameController;
import controls.maps.TestMap;
import controls.projectiles.RocketControl;
import controls.props.BoxControl;
import synchronization.SyncManager;

public class Test {

    public static void main(String[] args) {
        ServerApplication s = new ServerApplication();
        s.start(JmeContext.Type.Headless);

        s.enqueue(Test::physics);
        s.enqueue(Test::projectiles);
    }

    private static void physics() {
        SyncManager sm = GameController.getInstance().getSynchronizer();

        sm.create(new TestMap());

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                BoxControl bc = new BoxControl() {
                    @Override
                    public boolean hit(float dmg, Vector3f dir, Vector3f loc) {
                        System.out.println(dmg);
                        return super.hit(dmg, dir, loc);
                    }
                
                };
                bc.getLocation().set(i - 1, 0.5f + j, 0);
                sm.create(bc);
            }
        }
    }

    private static void projectiles() {
        SyncManager sm = GameController.getInstance().getSynchronizer();

        RocketControl rc = new RocketControl(Vector3f.UNIT_Z, 1, 10, null, 10000) {
            @Override
            public void destroy() {
                System.out.println("Done");
                super.destroy(); //To change body of generated methods, choose Tools | Templates.
            }
        };
        rc.setLocation(Vector3f.UNIT_Z.negate().mult(5).add(Vector3f.UNIT_Y.mult(2)));
        sm.create(rc);
    }
}
