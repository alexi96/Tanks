package visual;

import com.jme3.input.event.MouseButtonEvent;
import com.jme3.system.AppSettings;
import controllers.GameController;
import java.awt.Graphics;

public class SpawnFrame extends Frame {
    
    final private VehicleInfo vehicleInfo = new VehicleInfo();

    private final Button nextVehicle = new Button(">") {
        @Override
        public void onMouseButtonEvent(MouseButtonEvent evt) {
            if (evt.isPressed()) {
                SpawnFrame.this.nextVehicle();
            }
        }
    };
    private final Button lastVehicle = new Button("<") {
        @Override
        public void onMouseButtonEvent(MouseButtonEvent evt) {
            if (evt.isPressed()) {
                SpawnFrame.this.lastVehicle();
            }
        }
    };

    public SpawnFrame() {
        AppSettings set = GameController.getInstance().getSettings();

        super.size(set.getWidth() * 3 / 4, set.getHeight() * 3 / 4);
        super.center();

        final int buttonSize = 100;

        this.lastVehicle.bounds(0, 0, buttonSize, buttonSize);
        this.nextVehicle.bounds(super.width()-buttonSize, 0, buttonSize, buttonSize);
        this.vehicleInfo.bounds(buttonSize, 0, super.width() - buttonSize*2, buttonSize);
        this.lastVehicle.setFont(this.lastVehicle.getFont().deriveFont((float) buttonSize));
        this.nextVehicle.setFont(this.nextVehicle.getFont().deriveFont((float) buttonSize));

        super.add(this.nextVehicle);
        super.add(this.vehicleInfo);
        super.add(this.lastVehicle);
    }

    private void nextVehicle() {
        
    }

    private void lastVehicle() {
        
    }

    @Override
    public void paint(Graphics g) {
    }
}
