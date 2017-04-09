package visual.connect;

import application.ClientApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.system.AppSettings;
import controllers.GameController;
import controls.entityes.PlayerControl;
import java.io.File;
import java.io.IOException;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import utilities.MappedSettings;
import visual.Frame;
import visual.Label;

public class ConnectFrame extends Frame {

    private static final String SETTING_NAME = "ips";
    private static final String NAME_SETTING_NAME = "name";
    private final TextField ipText = new TextField() {
        @Override
        public void onMouseButtonEvent(MouseButtonEvent evt) {
            if (evt.isPressed()) {
                ConnectFrame.this.focusGained(this);
            }
        }
    };
    private final TextField nameText = new TextField() {
        @Override
        public void onMouseButtonEvent(MouseButtonEvent evt) {
            if (evt.isPressed()) {
                ConnectFrame.this.focusGained(this);
            }
        }
    };
    private ClientApplication application;
    private final MappedSettings<Object> settings = MappedSettings.<Object>getInstance(this);
    private TextField selectedField = this.ipText;

    public ConnectFrame(ClientApplication application) {
        this();
        this.application = application;
    }

    public ConnectFrame() {
        AppSettings set = GameController.getInstance().getSettings();

        super.size(set.getWidth() * 3 / 4, set.getHeight() * 3 / 4);
        super.center();

        final int sz = super.height() / 10;

        this.ipText.bounds(0, 0, super.width(), sz);
        this.nameText.bounds(0, sz, super.width(), sz);

        this.settings.setFile(new File("LastSettings"));
        if (!this.settings.open()) {
            this.settings.mapSetting(ConnectFrame.SETTING_NAME, new TreeSet<>());
            this.settings.mapSetting(ConnectFrame.NAME_SETTING_NAME, "");
            this.settings.save();
        }
        TreeSet<String> ips = (TreeSet<String>) this.settings.findSetting(ConnectFrame.SETTING_NAME);
        int i = 0;
        for (String ip : ips) {
            if (i >= 8) {
                break;
            }

            Label l = new Label(ip) {
                @Override
                public void onMouseButtonEvent(MouseButtonEvent evt) {
                    if (evt.isPressed()) {
                        ConnectFrame.this.selectIp(super.text);
                    }
                }
            };
            l.bounds(0, sz * i + sz * 2, super.width(), sz);
            super.add(l);
            ++i;
        }
        this.nameText.changeText((String) this.settings.findSetting(ConnectFrame.NAME_SETTING_NAME));

        super.add(this.ipText);
        super.add(this.nameText);
    }

    private void selectIp(String ip) {
        this.ipText.changeText(ip);
        super.invalidate();
    }

    @Override
    public void onKeyEvent(KeyInputEvent evt) {
        if (evt.isPressed()) {
            if (evt.getKeyCode() == KeyInput.KEY_RETURN) {
                this.action();
            }

            this.selectedField.input(evt.getKeyChar(), evt.getKeyCode());
        }
    }

    private void action() {
        try {
            PlayerControl.playerName = this.nameText.text();
            this.application.connect(this.ipText.text());
            super.hide();
            TreeSet<String> ips = (TreeSet<String>) this.settings.findSetting(ConnectFrame.SETTING_NAME);
            ips.add(this.ipText.text());
            this.settings.mapSetting(ConnectFrame.NAME_SETTING_NAME, this.nameText.text());
            this.settings.save();
        } catch (IOException ex) {
            Logger.getLogger(ConnectFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void focusGained(TextField f) {
        this.selectedField = f;
    }
}
