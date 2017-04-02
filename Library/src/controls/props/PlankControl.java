package controls.props;

import com.jme3.bullet.control.RigidBodyControl;
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

    @Override
    public void setSpatial(Spatial spatial) {
        GameController gc = GameController.getInstance();

        if (gc.getSynchronizer() != null) {
            if (spatial != null) {
                RigidBodyControl rbc = new RigidBodyControl(50);
                spatial.addControl(rbc);

                gc.getPhysics().add(rbc);
            } else {
                RigidBodyControl rbc = super.spatial.getControl(RigidBodyControl.class);
                super.spatial.removeControl(rbc);

                gc.getPhysics().remove(rbc);
            }
        }

        if (spatial != null) {
            this.location = spatial.getLocalTranslation();
            this.rotation = spatial.getLocalRotation();
        } else {
            this.location = null;
            this.rotation = null;
        }

        super.setSpatial(spatial);
    }
    
    
}
