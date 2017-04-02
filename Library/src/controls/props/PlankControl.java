package controls.props;

import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Spatial;
import controllers.GameController;
import synchronization.SyncManager;

public class PlankControl extends BoxControl {

    public PlankControl() {
        super(30);
    }

    @Override
    public void create() {
        Spatial plank = BoxControl.PROPS.getChild("Plank").clone();

        plank.setLocalTranslation(this.location);
        plank.setLocalRotation(this.rotation);

        SyncManager sm = GameController.getInstance().getSynchronizer();
        if (sm == null) {
            plank.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        }

        plank.addControl(this);

        GameController.getInstance().getApplication().getRootNode().attachChild(plank);
    }
}
