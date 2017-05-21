package controls;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Spatial;
import controllers.GameController;
import controls.props.PropControl;

public class TestBall extends PropControl {

    @Override
    public void create() {
        GameController gc = GameController.getInstance();
        Spatial s = gc.getLoader().loadModel("Models/Ball.j3o");

        s.setLocalTranslation(this.location);
        s.setLocalRotation(this.rotation);

        if (gc.getSynchronizer() == null) {
            s.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        }

        gc.getApplication().getRootNode().attachChild(s);

        s.addControl(this);
    }

    @Override
    public void setSpatial(Spatial spatial) {
        GameController gc = GameController.getInstance();

        if (gc.getSynchronizer() != null) {
            if (spatial != null) {
                RigidBodyControl rbc = new RigidBodyControl(200);
                spatial.addControl(rbc);

                gc.getPhysics().add(rbc);
            } else {
                RigidBodyControl rbc = super.spatial.getControl(RigidBodyControl.class);
                super.spatial.removeControl(rbc);

                gc.getPhysics().remove(rbc);
            }
        }

        super.setSpatial(spatial);
    }

    public void move(Vector3f loc) {
        RigidBodyControl rbc = super.spatial.getControl(RigidBodyControl.class);
        rbc.setPhysicsLocation(loc);
    }

    @Override
    public boolean hit(float dmg, Vector3f dir, Vector3f loc) {
        RigidBodyControl rbc = super.spatial.getControl(RigidBodyControl.class);
        rbc.applyImpulse(dir.mult(dmg), loc);
        return false;
    }

    @Override
    public String toString() {
        return this.location.toString();
    }
}
