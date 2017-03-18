package controls.entityes;

import com.jme3.audio.AudioNode;
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
import controls.weapons.WeaponControl;
import synchronization.SyncManager;
import synchronization.Synchronizer;
import utilities.LoadingManager;
import utilities.TankWheelManager;

public class TankControl extends PlayerControl {

    public static final float STEER_SPEED = 2f;
    public static final float MAX_STEER = FastMath.HALF_PI / 1.5f;
    public static final float MASS = 500;
    private static final Node MODEL = (Node) GameController.getInstance().getLoader().loadModel("Models/Tank.j3o");
    private transient VehicleControl vehicle;
    private transient float steer;
    private TankWheelManager wheelManager = new TankWheelManager();
    private transient AudioNode radio;
    private Vector3f location;
    private Quaternion rotation;
    private Quaternion headRot = new Quaternion();
    private Quaternion eyeRot = new Quaternion();
    private transient Spatial[] wheels;
    private transient Node head;
    private transient Node eye;
    private WeaponControl primary;
    private float aimState;
    private float radioPos;

    public TankControl() {
    }

    public WeaponControl getPrimary() {
        return primary;
    }

    public void setPrimary(WeaponControl primary) {
        this.primary = primary;
    }

    @Override
    public void prepare(Synchronizer newData) {
        TankControl o = (TankControl) newData;
        this.location.set(o.location);
        this.rotation.set(o.rotation);
        this.eyeRot.set(o.eyeRot);
        this.headRot.set(o.headRot);
        this.wheelManager.prepare(o.wheelManager);
        this.primary.prepare(o.primary);
        this.aimState = o.aimState;
    }

    @Override
    public void setSpatial(Spatial spatial) {
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

            boolean server = GameController.getInstance().getSynchronizer() != null;

            if (server) {
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
            }

            this.location = spatial.getLocalTranslation();
            this.rotation = spatial.getLocalRotation();
        } else {
            super.spatial.removeControl(this.vehicle);
        }

        super.setSpatial(spatial);
    }

    @Override
    public void create() {
        super.create();

        GameController gc = GameController.getInstance();

        Node n = (Node) TankControl.MODEL.clone();

        if (gc.isBestVisualStyles()) {
            n.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        }
        boolean server = GameController.getInstance().getSynchronizer() != null;

        this.wheels = new Spatial[6];

        this.radio = new AudioNode(gc.getApplication().getAssetManager(), "Sounds/LMFAO ft. Lil Jon - Shots.wav");
        if (server) {
            this.vehicle = new VehicleControl();
        } else if (id == 2) {
            this.radio.setPositional(false);
            this.radio.setTimeOffset(this.radioPos);
            GameController.getInstance().getApplication().getRootNode().attachChild(radio);
            this.radio.play();
        } else {
            this.radio.setPositional(true);
        }

        this.primary.create();
        n.addControl(this);

        gc.getApplication().getRootNode().attachChild(n);

        if (server) {
            gc.getPhysics().add(this.vehicle);
            this.wheelManager.initialise(this.vehicle);
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

        this.radio.setLocalTranslation(this.location);

        if (super.id != PlayerControl.serverId) {
            return;
        }

        Camera c = GameController.getInstance().getApplication().getCamera();
        Vector3f dep = Vector3f.UNIT_Y.add(c.getDirection().mult(-5));
        dep.multLocal(this.aimState);
        c.setLocation(this.eye.getWorldTranslation().add(dep));
    }

    @Override
    public void destroy() {
        super.destroy();
        GameController.getInstance().getApplication().getInputManager().removeListener(this);
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

        final float acc = 200;
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

        this.primary.fire(super.fire);
        this.primary.secondaryFire(super.secondaryFire);

        this.wheelManager.update(tpf);

        this.updateFirstPerson(tpf);

        this.radioPos += tpf;

        /*if (super.ctrl) {
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
         }*/

        this.vehicle.steer(this.steer * FastMath.QUARTER_PI / 2);

        sm.update(this);
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
