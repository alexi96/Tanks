package controls.entityes;

import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.VehicleControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import controllers.GameController;
import synchronization.SyncManager;
import synchronization.Synchronizer;
import utilities.LoadingManager;
import utilities.TankWheelManager;

public class TankControl extends PlayerControl {

    public static final float STEER_SPEED = 2f;
    public static final float MAX_STEER = FastMath.HALF_PI / 1.5f;
    public static final float MASS = 750;
    private static final Node MODEL = (Node) GameController.getInstance().getLoader().loadModel("Models/Tank.j3o");
    private transient VehicleControl vehicle;
    private transient float steer;
    private TankWheelManager wheelManager = new TankWheelManager();
    private Vector3f location;
    private Quaternion rotation;
    private Quaternion headRot = new Quaternion();
    private Quaternion eyeRot = new Quaternion();
    private transient Spatial[] wheels;
    private transient Node head;
    private transient Node eye;
    private float aimState;

    public TankControl() {
        super.resetHealth(400);
    }

    @Override
    public void prepare(Synchronizer newData) {
        TankControl o = (TankControl) newData;
        super.health = o.health;

        this.location.set(o.location);
        this.rotation.set(o.rotation);
        this.eyeRot.set(o.eyeRot);
        this.headRot.set(o.headRot);
        this.wheelManager.prepare(o.wheelManager);
        this.primary.prepare(o.primary);
        this.secondary.prepare(o.secondary);
        if (o.selected == o.primary) {
            this.selected = this.primary;
        } else {
            this.selected = this.secondary;
        }

        this.aimState = o.aimState;
    }

    @Override
    public void setSpatial(Spatial spatial) {
        boolean server = GameController.getInstance().getSynchronizer() != null;

        if (spatial != null) {
            Node n = (Node) spatial;
            this.wheels[0] = n.getChild("Wheel.FL");
            this.wheels[1] = n.getChild("Wheel.CL");
            this.wheels[2] = n.getChild("Wheel.BL");
            this.wheels[3] = n.getChild("Wheel.FR");
            this.wheels[4] = n.getChild("Wheel.CR");
            this.wheels[5] = n.getChild("Wheel.BR");
            this.head = (Node) n.getChild("Head");
            this.eye = (Node) n.getChild("Eye");
            this.eye.detachAllChildren();
            this.eye.attachChild(this.primary.getSpatial());
            this.eye.attachChild(this.secondary.getSpatial());

            if (server) {
                Spatial hull = LoadingManager.findByName(spatial, "Hull");
                CollisionShape hullShape = CollisionShapeFactory.createDynamicMeshShape(hull);
                this.vehicle = new VehicleControl(hullShape, TankControl.MASS);
                spatial.addControl(this.vehicle);

                float stiff = 25;
                float damping = 0.1f;
                float comp = 0.75f;
                this.vehicle.setSuspensionCompression(comp * 2 * FastMath.sqrt(stiff));
                this.vehicle.setSuspensionDamping(damping * 2.0f * FastMath.sqrt(stiff));
                this.vehicle.setSuspensionStiffness(stiff);
                this.vehicle.setFrictionSlip(2);

                Spatial wheel = this.wheels[0];
                BoundingBox box = (BoundingBox) wheel.getWorldBound();
                float radius = box.getYExtent();

                Vector3f direction = new Vector3f(0, -1, 0);
                Vector3f axle = new Vector3f(-1, 0, 0);
                this.vehicle.addWheel(wheel, box.getCenter(), direction, axle, 0.2f, radius, true);

                wheel = this.wheels[1];
                this.vehicle.addWheel(wheel, wheel.getWorldBound().getCenter(), direction, axle, 0.2f, radius, false);

                wheel = this.wheels[2];
                this.vehicle.addWheel(wheel, wheel.getWorldBound().getCenter(), direction, axle, 0.2f, radius, false);

                wheel = this.wheels[3];
                this.vehicle.addWheel(wheel, wheel.getWorldBound().getCenter(), direction, axle, 0.2f, radius, true);

                wheel = this.wheels[4];
                this.vehicle.addWheel(wheel, wheel.getWorldBound().getCenter(), direction, axle, 0.2f, radius, false);

                wheel = this.wheels[5];
                this.vehicle.addWheel(wheel, wheel.getWorldBound().getCenter(), direction, axle, 0.2f, radius, false);

                GameController.getInstance().getPhysics().add(this.vehicle);
                this.wheelManager.initialise(this.vehicle);
            }

            this.location = spatial.getLocalTranslation();
            this.rotation = spatial.getLocalRotation();
        } else if (server) {
            GameController.getInstance().getPhysics().remove(this.vehicle);
            super.spatial.removeControl(this.vehicle);
            this.vehicle = null;
        }

        super.setSpatial(spatial);
    }

    @Override
    public void create() {
        super.create();

        GameController gc = GameController.getInstance();

        Node n = (Node) TankControl.MODEL.clone();

        boolean server = GameController.getInstance().getSynchronizer() != null;
        if (!server) {
            n.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        }

        this.wheels = new Spatial[6];

        this.primary.setHolder(this);
        this.secondary.setHolder(this);
        this.primary.create();
        this.secondary.create();
        this.selected = primary;
        n.addControl(this);

        gc.getApplication().getRootNode().attachChild(n);

        if (server) {
            this.vehicle.setPhysicsLocation(Vector3f.UNIT_Y);
        }
    }

    @Override
    public void synchronize() {
        for (int i = 0; i < this.wheels.length; ++i) {
            this.wheels[i].setLocalTranslation(this.wheelManager.getLocations()[i]);
            this.wheels[i].setLocalRotation(this.wheelManager.getRotations()[i]);
        }

        super.spatial.setLocalTranslation(this.location);
        super.spatial.setLocalRotation(this.rotation);
        this.head.setLocalRotation(this.headRot);
        this.eye.setLocalRotation(this.eyeRot);
        this.primary.synchronize();
        this.secondary.synchronize();

        if (super.id != PlayerControl.serverId) {
            return;
        }

        Camera c = GameController.getInstance().getApplication().getCamera();
        Vector3f dep = Vector3f.UNIT_Y.mult(0).add(c.getDirection().mult(-5));
        dep.multLocal(this.aimState);
        c.setLocation(this.eye.getWorldTranslation().add(dep));
    }

    @Override
    public void moveTo(Vector3f loc) {
        this.vehicle.setPhysicsLocation(loc);
    }

    @Override
    public void restrictCamra(Camera camera) {
        final float min = FastMath.DEG_TO_RAD * 20;
        final float max = -FastMath.DEG_TO_RAD * 45;

        float[] angs = camera.getRotation().toAngles(null);
        if (angs[0] > min && angs[0] < FastMath.PI) {
            angs[0] = min;
            camera.setRotation(new Quaternion(angs));
        } else if (angs[0] < max && angs[0] > -FastMath.PI) {
            angs[0] = max;
            camera.setRotation(new Quaternion(angs));
        }
    }

    private void updateFirstPerson(float tpf) {
        tpf *= 3;
        if (this.secondaryFire) {
            if (this.aimState > 0) {
                this.aimState -= tpf;
                if (this.aimState < 0) {
                    this.aimState = 0;
                }
            }
        } else {
            if (this.aimState < 1) {
                this.aimState += tpf;
                if (this.aimState > 1) {
                    this.aimState = 1;
                }
            }
        }
    }

    private void updateWeapons(float tpf) {
        if (super.swap) {
            super.swap = false;
            this.selected.secondaryFire(false);
            this.selected.fire(false);
            if (this.primary == this.selected) {
                this.selected = this.secondary;
            } else {
                this.selected = this.primary;
            }
        }

        this.selected.secondaryFire(super.secondaryFire);
        this.selected.fire(super.fire);
    }

    @Override
    public void update(float tpf) {
        SyncManager sm = GameController.getInstance().getSynchronizer();
        if (sm == null) {
            return;
        }

        float[] dt = super.spatial.getLocalRotation().toAngles(null);

        Quaternion rot = new Quaternion();
        rot.lookAt(super.look, Vector3f.UNIT_Y);
        float[] t = rot.toAngles(null);
        t[0] = 0;
        t[2] = 0;
        t[1] -= dt[1];
        this.headRot.set(new Quaternion(t));
        this.head.setLocalRotation(this.headRot);

        t = rot.toAngles(null);
        t[0] -= dt[0];
        t[1] = 0;
        t[2] = 0;
        this.eyeRot.set(new Quaternion(t));
        this.eye.setLocalRotation(this.eyeRot);

        final float acc = TankControl.MASS;
        if (super.up) {
            this.vehicle.accelerate(acc);
        } else if (!this.down) {
            this.vehicle.accelerate(0);
        }
        if (super.down) {
            this.vehicle.accelerate(-acc);
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

        this.updateWeapons(tpf);

        this.wheelManager.update(tpf);

        this.updateFirstPerson(tpf);

        this.vehicle.steer(this.steer * FastMath.QUARTER_PI / 2);

        sm.update(this);
    }

    @Override
    public boolean hit(float dmg, Vector3f dir, Vector3f loc) {
        this.vehicle.applyImpulse(dir.mult(dmg), loc);

        return super.hit(dmg, dir, loc);
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

    public static Node getModel() {
        return MODEL;
    }
}
