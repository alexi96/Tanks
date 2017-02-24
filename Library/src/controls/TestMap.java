package controls;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import controllers.GameController;

public class TestMap extends SyncGameControl {

    @Override
    public void create() {
        GameController gc = GameController.getInstance();
        Spatial s = gc.getApplication().getAssetManager().loadModel("Models/TestMap.j3o");

        if (gc.isBestVisualStyles()) {
            gc.getLoader().loadTextures(s);

            DirectionalLight sun = new DirectionalLight();
            sun.setDirection((new Vector3f(-0.5f, -0.5f, -0.5f)).normalizeLocal());
            sun.setColor(ColorRGBA.White);
            gc.getApplication().getRootNode().addLight(sun);
        }

        boolean server = GameController.getInstance().getSynchronizer() != null;
        if (server) {
            RigidBodyControl rbc = new RigidBodyControl(0);
            s.addControl(rbc);
            gc.getPhysics().add(rbc);
        }

        gc.getApplication().getRootNode().attachChild(s);
    }

    @Override
    public void update(float tpf) {
    }
}
