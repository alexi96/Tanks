package utilities;

import com.jme3.bullet.control.VehicleControl;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import synchronization.Synchronizer;

public class TankWheelManager extends Synchronizer {

    public static final float HIDRA_SPEED = 3f;
    public static final float MAX_HIDRA = 0.4f;
    private transient VehicleControl vehicle;
    private Vector3f[] locations;
    private Quaternion[] rotations;
    private transient float[] hidra;
    private transient byte[] engine;
    private transient float defaultSuspensionLength;

    public Vector3f[] getLocations() {
        return locations;
    }

    public Quaternion[] getRotations() {
        return rotations;
    }
    
    public float getDefaultSuspensionLength() {
        return defaultSuspensionLength;
    }

    public void setDefaultSuspensionLength(float defaultSuspensionLength) {
        this.defaultSuspensionLength = defaultSuspensionLength;
    }

    @Override
    public void synchronize() {
        
    }

    public void initialise(VehicleControl vc) {
        this.vehicle = vc;
        int ws = vc.getNumWheels();
        this.locations = new Vector3f[ws];
        this.rotations = new Quaternion[ws];
        this.hidra = new float[ws];
        this.engine = new byte[ws];
        
        for (int i = 0; i < ws; i++) {
            this.locations[i] = vc.getWheel(i).getWheelSpatial().getLocalTranslation();
            this.rotations[i] = vc.getWheel(i).getWheelSpatial().getLocalRotation();
        }
        
        //this.defaultSuspensionLength = this.vehicle.getWheel(0).getWheelInfo().getSuspensionRestLength();
        this.defaultSuspensionLength = this.vehicle.getWheel(0).getRestLength();
    }

    public void engine(int index, boolean fired) {
        if (fired) {
            ++this.engine[index];
        } else {
            --this.engine[index];
        }
    }

    public void update(float tpf) {
        float hs = tpf * TankWheelManager.HIDRA_SPEED;
        for (int i = 0; i < this.vehicle.getNumWheels(); ++i) {
            if (this.engine[i] > 0 && this.hidra[i] < TankWheelManager.MAX_HIDRA) {
                this.hidra[i] += hs;
                if (this.hidra[i] < TankWheelManager.MAX_HIDRA) {
                    this.hidra[i] = TankWheelManager.MAX_HIDRA;
                }
            } else if (this.hidra[i] > 0) {
                this.hidra[i] -= hs;
                if (this.hidra[i] < 0) {
                    this.hidra[i] = 0;
                }
            }

            //this.vehicle.getWheel(i).getWheelInfo().suspensionRestLength1 = this.defaultSuspensionLength + this.hidra[i];
            this.vehicle.getWheel(i).setRestLength(this.defaultSuspensionLength + this.hidra[i]);
        }
    }
}
