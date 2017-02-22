package controls;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import controllers.GameController;

public class TestMap extends NetworkControl {

    @Override
    public void create(boolean server) {
        GameController gc = GameController.getInstance();
        Spatial s = gc.getLoader().loadModel("Models/TestMap.j3o");
        gc.getApplication().getRootNode().attachChild(s);

        if (!server) {
            DirectionalLight sun = new DirectionalLight();
            sun.setDirection((new Vector3f(-0.5f, -0.5f, -0.5f)).normalizeLocal());
            sun.setColor(ColorRGBA.White);
            gc.getApplication().getRootNode().addLight(sun);

            return;
        }

        RigidBodyControl rbc = new RigidBodyControl(0);
        s.addControl(rbc);
        gc.getPhysics().add(rbc);
    }

    @Override
    public void update(float tpf) {
    }
}
