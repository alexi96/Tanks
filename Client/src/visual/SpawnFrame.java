package visual;

import com.jme3.input.event.MouseButtonEvent;
import com.jme3.system.AppSettings;
import controllers.GameController;
import controls.entityes.DroneControl;
import controls.entityes.PlayerControl;
import controls.entityes.RobotControl;
import controls.entityes.TankControl;
import java.awt.Graphics;
import java.util.ArrayList;

public class SpawnFrame extends Frame {

    private ArrayList<PlayerControl> players = new ArrayList<>();
    private int playerIndex = 0;
    final private VehicleInfo vehicleInfo = new VehicleInfo();
    final private WeaponInfo primaryInfo = new WeaponInfo();
    final private WeaponInfo secondaryInfo = new WeaponInfo();

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
        this.players.add(new RobotControl());
        this.players.add(new TankControl());
        this.players.add(new DroneControl());

        AppSettings set = GameController.getInstance().getSettings();

        super.size(set.getWidth() * 3 / 4, set.getHeight() * 3 / 4);
        super.center();

        final int unitW = super.width() / 10;
        final int unitH = super.height() / 4;

        this.lastVehicle.bounds(0, 0, unitW, unitH);
        this.vehicleInfo.bounds(unitW, 0, super.width() - unitW * 2, unitH);
        this.nextVehicle.bounds(super.width() - unitW, 0, unitW, unitH);
        
        this.primaryInfo.bounds(unitW, unitH, super.width() / 2 - unitW * 2, unitH * 2);
        this.secondaryInfo.bounds(primaryInfo.width() + unitW * 3, unitH, super.width() / 2 - unitW * 2, unitH * 2);
        
        this.lastVehicle.setFont(this.lastVehicle.getFont().deriveFont((float) unitW));
        this.nextVehicle.setFont(this.nextVehicle.getFont().deriveFont((float) unitW));

        super.add(this.nextVehicle);
        super.add(this.vehicleInfo);
        super.add(this.lastVehicle);
        super.add(this.primaryInfo);
        super.add(this.secondaryInfo);

        this.vehicleInfo.setPlayer(this.players.get(0));
    }

    private void nextVehicle() {
        ++this.playerIndex;
        if (this.playerIndex >= this.players.size()) {
            this.playerIndex = 0;
        }
        this.vehicleInfo.setPlayer(this.players.get(this.playerIndex));
        this.vehicleInfo.invalidate();
    }

    private void lastVehicle() {
        --this.playerIndex;
        if (this.playerIndex < 0) {
            this.playerIndex = this.players.size() - 1;
        }
        this.vehicleInfo.setPlayer(this.players.get(this.playerIndex));
        this.vehicleInfo.invalidate();
    }

    @Override
    public void paint(Graphics g) {
    }
}
