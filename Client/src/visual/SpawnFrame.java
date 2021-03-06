package visual;

import com.jme3.input.KeyInput;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.system.AppSettings;
import controllers.GameController;
import controls.entityes.DroneControl;
import controls.entityes.PlayerControl;
import controls.entityes.RobotControl;
import controls.entityes.TankControl;
import controls.weapons.AutoShotgun;
import controls.weapons.CannonControl;
import controls.weapons.GrenadeLauncher;
import controls.weapons.MachineGun;
import controls.weapons.MinigunControl;
import controls.weapons.MissleLauncher;
import controls.weapons.RobotGrenadeLauncher;
import controls.weapons.RpgControl;
import controls.weapons.SniperConotrol;
import controls.weapons.WeaponControl;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.TreeMap;

public class SpawnFrame extends Frame {

    private ArrayList<PlayerControl> players = new ArrayList<>();
    private TreeMap<String, ArrayList<WeaponControl>> pWeapons = new TreeMap();
    private TreeMap<String, ArrayList<WeaponControl>> sWeapons = new TreeMap();
    private int playerIndex = 0;
    private int pIndex = 0;
    private int sIndex = 0;
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
    private final Button nextPrimary = new Button(">") {
        @Override
        public void onMouseButtonEvent(MouseButtonEvent evt) {
            if (evt.isPressed()) {
                SpawnFrame.this.nextPrimary();
            }
        }
    };
    private final Button lastPrimary = new Button("<") {
        @Override
        public void onMouseButtonEvent(MouseButtonEvent evt) {
            if (evt.isPressed()) {
                SpawnFrame.this.lastPrimary();
            }
        }
    };
    private final Button nextSecondary = new Button(">") {
        @Override
        public void onMouseButtonEvent(MouseButtonEvent evt) {
            if (evt.isPressed()) {
                SpawnFrame.this.nextSecondary();
            }
        }
    };
    private final Button lastSecondary = new Button("<") {
        @Override
        public void onMouseButtonEvent(MouseButtonEvent evt) {
            if (evt.isPressed()) {
                SpawnFrame.this.lastSecondary();
            }
        }
    };
    private final Button spawn = new Button("Spawn") {
        @Override
        public void onMouseButtonEvent(MouseButtonEvent evt) {
            if (evt.isPressed()) {
                SpawnFrame.this.spawn();
            }
        }
    };

    public SpawnFrame() {
        this.players.add(new RobotControl());
        this.players.add(new TankControl());
        this.players.add(new DroneControl());

        ArrayList<WeaponControl> rpw = new ArrayList<>();
        this.pWeapons.put(RobotControl.class.getSimpleName(), rpw);
        rpw.add(new MachineGun());
        rpw.add(new RpgControl());

        ArrayList<WeaponControl> rsw = new ArrayList<>();
        this.sWeapons.put(RobotControl.class.getSimpleName(), rsw);
        rsw.add(new AutoShotgun());
        rsw.add(new RobotGrenadeLauncher());

        ArrayList<WeaponControl> tpw = new ArrayList<>();
        this.pWeapons.put(TankControl.class.getSimpleName(), tpw);
        tpw.add(new CannonControl());
        tpw.add(new MissleLauncher());

        ArrayList<WeaponControl> tsw = new ArrayList<>();
        this.sWeapons.put(TankControl.class.getSimpleName(), tsw);
        tsw.add(new MinigunControl());

        ArrayList<WeaponControl> dpw = new ArrayList<>();
        this.pWeapons.put(DroneControl.class.getSimpleName(), dpw);
        dpw.add(new SniperConotrol());

        ArrayList<WeaponControl> dsw = new ArrayList<>();
        this.sWeapons.put(DroneControl.class.getSimpleName(), dsw);
        dsw.add(new GrenadeLauncher());

        AppSettings set = GameController.getInstance().getSettings();

        super.size(set.getWidth() * 3 / 4, set.getHeight() * 3 / 4);
        super.center();

        final int unitW = super.width() / 10;
        final int unitH = super.height() / 4;

        this.lastVehicle.bounds(0, 0, unitW, unitH);
        this.vehicleInfo.bounds(unitW, 0, super.width() - unitW * 2, unitH);
        this.nextVehicle.bounds(super.width() - unitW, 0, unitW, unitH);

        this.lastPrimary.bounds(0, unitH, unitW, unitH * 2);
        this.primaryInfo.bounds(unitW, unitH, super.width() / 2 - unitW * 2, unitH * 2);
        this.nextPrimary.bounds(primaryInfo.width() + unitW, unitH, unitW, unitH * 2);

        this.lastSecondary.bounds(nextPrimary.getX() + unitW, unitH, unitW, unitH * 2);
        this.secondaryInfo.bounds(primaryInfo.width() + unitW * 3, unitH, super.width() / 2 - unitW * 2, unitH * 2);
        this.nextSecondary.bounds(super.width() - unitW, unitH, unitW, unitH * 2);
        this.spawn.bounds(0, unitH * 3, super.width(), unitH);

        this.lastVehicle.setFont(this.lastVehicle.getFont().deriveFont((float) unitW));
        this.nextVehicle.setFont(this.nextVehicle.getFont().deriveFont((float) unitW));

        super.add(this.nextVehicle);
        super.add(this.vehicleInfo);
        super.add(this.lastVehicle);
        super.add(this.nextPrimary);
        super.add(this.primaryInfo);
        super.add(this.lastPrimary);
        super.add(this.nextSecondary);
        super.add(this.secondaryInfo);
        super.add(this.lastSecondary);
        super.add(this.spawn);

        this.vehicleInfo.setPlayer(this.players.get(0));
        this.pIndex = -1;
        this.sIndex = -1;
        this.nextPrimary();
        this.nextSecondary();
    }

    private void nextVehicle() {
        ++this.playerIndex;
        if (this.playerIndex >= this.players.size()) {
            this.playerIndex = 0;
        }
        this.vehicleInfo.setPlayer(this.players.get(this.playerIndex));
        this.vehicleInfo.invalidate();
        this.pIndex = -1;
        this.sIndex = -1;
        this.nextPrimary();
        this.nextSecondary();
    }

    private void lastVehicle() {
        --this.playerIndex;
        if (this.playerIndex < 0) {
            this.playerIndex = this.players.size() - 1;
        }
        this.vehicleInfo.setPlayer(this.players.get(this.playerIndex));
        this.vehicleInfo.invalidate();
        this.pIndex = -1;
        this.sIndex = -1;
        this.nextPrimary();
        this.nextSecondary();
    }

    private void nextPrimary() {
        ++this.pIndex;
        PlayerControl p = this.players.get(this.playerIndex);
        ArrayList<WeaponControl> ps = this.pWeapons.get(p.getClass().getSimpleName());
        if (this.pIndex >= ps.size()) {
            this.pIndex = 0;
        }
        this.primaryInfo.setWeapon(ps.get(this.pIndex));
        this.primaryInfo.invalidate();
    }

    private void lastPrimary() {
        --this.pIndex;
        PlayerControl p = this.players.get(this.playerIndex);
        ArrayList<WeaponControl> ps = this.pWeapons.get(p.getClass().getSimpleName());
        if (this.pIndex < 0) {
            this.pIndex = ps.size() - 1;
        }
        this.primaryInfo.setWeapon(ps.get(this.pIndex));
        this.primaryInfo.invalidate();
    }

    private void nextSecondary() {
        ++this.sIndex;
        PlayerControl p = this.players.get(this.playerIndex);
        ArrayList<WeaponControl> ps = this.sWeapons.get(p.getClass().getSimpleName());
        if (this.sIndex >= ps.size()) {
            this.sIndex = 0;
        }
        this.secondaryInfo.setWeapon(ps.get(this.sIndex));
        this.secondaryInfo.invalidate();
    }

    private void lastSecondary() {
        --this.sIndex;
        PlayerControl p = this.players.get(this.playerIndex);
        ArrayList<WeaponControl> ps = this.sWeapons.get(p.getClass().getSimpleName());
        if (this.sIndex < 0) {
            this.sIndex = ps.size() - 1;
        }
        this.secondaryInfo.setWeapon(ps.get(this.sIndex));
        this.secondaryInfo.invalidate();
    }

    @Override
    public void onKeyEvent(KeyInputEvent evt) {
        if (evt.isPressed() && evt.getKeyCode() == KeyInput.KEY_RETURN) {
            this.spawn();
        }
    }
    
    private void spawn() {
        PlayerControl p = this.players.get(this.playerIndex);
        ArrayList<WeaponControl> ps = this.pWeapons.get(p.getClass().getSimpleName());
        ArrayList<WeaponControl> ss = this.sWeapons.get(p.getClass().getSimpleName());

        WeaponControl pw = ps.get(this.pIndex);
        WeaponControl sw = ss.get(this.sIndex);

        p.setPrimary(pw);
        p.setSecondary(sw);
        p.setName(PlayerControl.playerName);

        this.spawn(p);
    }

    public void spawn(PlayerControl p) {
    }

    @Override
    public void paint(Graphics g) {
    }
}
