package utilities;

import com.jme3.scene.Spatial;
import controllers.GameController;
import controls.props.BoxControl;
import synchronization.SyncManager;

public class Names {

    private static void box(Spatial s) {
        SyncManager sm = GameController.getInstance().getSynchronizer();
        if (sm != null) {
            BoxControl b = new BoxControl();
            b.getLocation().set(s.getLocalTranslation());
            b.getRotation().set(s.getLocalRotation());
            sm.create(b);
        }

        s.removeFromParent();
    }
}
