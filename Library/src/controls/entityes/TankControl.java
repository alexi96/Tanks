package controls.entityes;

import com.jme3.audio.AudioNode;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.VehicleControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import controllers.GameController;
import utilities.LoadingManager;
import utilities.TankWheelManager;

public class TankControl extends PlayerControl {

    
    public static final float STEER_SPEED = 2f;
    public static final float MAX_STEER = FastMath.HALF_PI / 1.5f;
    public static final float MASS = 500;
    private static final Node MODEL = (Node) GameController.getInstance().getLoader().loadModel("Models/Tank.j3o");
    protected boolean altDown;
    private final transient VehicleControl vehicle = new VehicleControl();
    private transient float steer;
    private TankWheelManager wheelManager = new TankWheelManager();
    private transient AudioNode radio;

    public TankControl() {
    }

    @Override
    public void setSpatial(Spatial spatial) {
        if (spatial != null) {
            Spatial hull = LoadingManager.findByName(spatial, "Hull");
            CollisionShape hullShape = CollisionShapeFactory.createDynamicMeshShape(hull);
            this.vehicle.setCollisionShape(hullShape);
            this.vehicle.setMass(TankControl.MASS);

            spatial.addControl(this.vehicle);
            float stiff = 25;
            float damping = 0.1f;
            float comp = 0.75f;
            this.vehicle.setSuspensionCompression(comp * 2 * FastMath.sqrt(stiff));
            this.vehicle.setSuspensionDamping(damping * 2.0f * FastMath.sqrt(stiff));
            this.vehicle.setSuspensionStiffness(stiff);
            this.vehicle.setFrictionSlip(2);

            Spatial wheel = LoadingManager.findByName(spatial, "Wheel.FL");
            BoundingBox box = (BoundingBox) wheel.getWorldBound();
            float radius = box.getYExtent();

            Vector3f direction = new Vector3f(0, -1, 0);
            Vector3f axle = new Vector3f(-1, 0, 0);
            this.vehicle.addWheel(wheel, box.getCenter(), direction, axle, 0.2f, radius, true);

            wheel = LoadingManager.findByName(spatial, "Wheel.CL");
            this.vehicle.addWheel(wheel, wheel.getWorldBound().getCenter(), direction, axle, 0.2f, radius, false);

            wheel = LoadingManager.findByName(spatial, "Wheel.BL");
            this.vehicle.addWheel(wheel, wheel.getWorldBound().getCenter(), direction, axle, 0.2f, radius, false);

            wheel = LoadingManager.findByName(spatial, "Wheel.FR");
            this.vehicle.addWheel(wheel, wheel.getWorldBound().getCenter(), direction, axle, 0.2f, radius, true);

            wheel = LoadingManager.findByName(spatial, "Wheel.CR");
            this.vehicle.addWheel(wheel, wheel.getWorldBound().getCenter(), direction, axle, 0.2f, radius, false);

            wheel = LoadingManager.findByName(spatial, "Wheel.BR");
            this.vehicle.addWheel(wheel, wheel.getWorldBound().getCenter(), direction, axle, 0.2f, radius, false);
        } else {
            super.spatial.removeControl(this.vehicle);
        }

        super.setSpatial(spatial);
    }

    @Override
    public void create() {
        GameController gc = GameController.getInstance();

        Node n = (Node) TankControl.MODEL.clone();

        if (gc.isBestVisualStyles()) {
            n.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);

            radio = new AudioNode(gc.getApplication().getAssetManager(), "Sounds/War - Low Rider.wav");
            this.radio.setPositional(PlayerControl.serverId != this.id);
            radio.play();
        }

        n.addControl(this);

        gc.getApplication().getRootNode().attachChild(n);
        gc.getPhysics().add(this.vehicle);

        this.wheelManager.initialise(this.vehicle);
    }

    @Override
    public void update(float tpf) {
        if (super.up) {
            this.vehicle.accelerate(100);
        } else if (!this.down) {
            this.vehicle.accelerate(0);
        }
        if (super.down) {
            this.vehicle.accelerate(-100);
        } else if (!this.up) {
            this.vehicle.accelerate(0);
        }

        if (super.space) {
            this.vehicle.brake(30);
        } else {
            this.vehicle.brake(0);
        }

        float ss = TankControl.STEER_SPEED * tpf;
        float ms = TankControl.MAX_STEER;
        if (super.left) {
            if (this.steer < ms) {
                this.steer += ss;
                if (this.steer > ms) {
                    this.steer = ms;
                }
            }
        } else if (super.right) {
            if (this.steer > -ms) {
                this.steer -= ss;
                if (this.steer < -ms) {
                    this.steer = -ms;
                }
            }
        } else {
            if (steer > 0) {
                this.steer -= ss;
                if (this.steer < 0) {
                    this.steer = 0;
                }
            } else if (this.steer < 0) {
                this.steer += ss;
                if (this.steer > 0) {
                    this.steer = 0;
                }
            }
        }

        this.wheelManager.update(tpf);

        if (super.ctrl) {
            super.ctrl = false;
            float dur = radio.getAudioData().getDuration();
            float time = radio.getTimeOffset();
            if (time + 15 >= dur) {
                this.radio.setTimeOffset(0);
            } else {
                this.radio.setTimeOffset(radio.getTimeOffset() + 15);
            }
        }
        if (super.shift) {
            super.shift = false;

            float dur = radio.getAudioData().getDuration();
            float time = radio.getTimeOffset();
            if (time - 15 < 0) {
                this.radio.setTimeOffset(dur - 15);
            } else {
                this.radio.setTimeOffset(radio.getTimeOffset() - 15);
            }
        }
        this.radio.setLocalTranslation(super.spatial.getWorldTranslation());

        this.vehicle.steer(this.steer * FastMath.QUARTER_PI / 2);
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        super.onAction(name, isPressed, tpf);
        switch (name) {
            case PlayerControl.ALT_UP:
                this.wheelManager.engine(0, isPressed);
                this.wheelManager.engine(3, isPressed);
                break;
            case PlayerControl.ALT_DOWN:
                this.wheelManager.engine(2, isPressed);
                this.wheelManager.engine(5, isPressed);
                break;
            case PlayerControl.ALT_LEFT:
                this.wheelManager.engine(0, isPressed);
                this.wheelManager.engine(1, isPressed);
                this.wheelManager.engine(2, isPressed);
                break;
            case PlayerControl.ALT_RIGHT:
                this.wheelManager.engine(3, isPressed);
                this.wheelManager.engine(4, isPressed);
                this.wheelManager.engine(5, isPressed);
                break;
        }
    }
}
