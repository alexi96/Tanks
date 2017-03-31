package visual.connect;

import application.ClientApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.system.AppSettings;
import controllers.GameController;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import utilities.MappedSettings;
import visual.Frame;
import visual.Label;

public class ConnectFrame extends Frame {

    private static final String SETTING_NAME = "ips";
    private final IpTextFrame ipText = new IpTextFrame();
    private ClientApplication application;
    private final MappedSettings<ArrayList<String>> settings = MappedSettings.<ArrayList<String>>getInstance(this);

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

        this.settings.setFile(new File("LastIps"));
        this.settings.open();
        ArrayList<String> ips = this.settings.findSetting(ConnectFrame.SETTING_NAME);
        if (ips != null) {
            for (int i = 0; i < 9 && i < ips.size(); ++i) {
                Label l = new Label(ips.get(i)) {
                    @Override
                    public void onMouseButtonEvent(MouseButtonEvent evt) {
                        if (evt.isPressed()) {
                            ConnectFrame.this.selectIp(super.text);
                        }
                    }
                };
                l.bounds(0, sz * i + sz, super.width(), sz);
                super.add(l);
            }
        }

        super.add(this.ipText);
    }

    private void selectIp(String ip) {
        this.ipText.text();
    }

    @Override
    public void onKeyEvent(KeyInputEvent evt) {
        if (evt.isPressed()) {
            if (evt.getKeyCode() == KeyInput.KEY_RETURN) {
                this.action();
            }

            this.ipText.input(evt.getKeyChar(), evt.getKeyCode());
        }
    }

    private void action() {
        try {
            this.application.connect(this.ipText.text());
            super.hide();
            this.settings.findSetting(ConnectFrame.SETTING_NAME).add(this.ipText.text());
        } catch (IOException ex) {
            Logger.getLogger(ConnectFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
