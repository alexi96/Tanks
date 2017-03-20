package visual;

import com.jme3.input.event.MouseButtonEvent;
import com.jme3.system.AppSettings;
import controllers.GameController;
import java.awt.Graphics;

public class SpawnFrame extends Frame {

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

        final int buttonSize = 100;

        this.lastVehicle.bounds(0, 0, buttonSize, buttonSize);
        this.nextVehicle.bounds(buttonSize, 0, buttonSize, buttonSize);
        this.lastVehicle.setFont(this.lastVehicle.getFont().deriveFont((float) buttonSize));
        this.nextVehicle.setFont(this.nextVehicle.getFont().deriveFont((float) buttonSize));

        super.add(this.nextVehicle);
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
