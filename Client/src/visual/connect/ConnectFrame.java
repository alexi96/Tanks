package visual.connect;

import application.ClientApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.system.AppSettings;
import controllers.GameController;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import visual.Frame;

public class ConnectFrame extends Frame {

    private final IpTextFrame ipText = new IpTextFrame();
    private ClientApplication application;

    public ConnectFrame(ClientApplication application) {
        this();
        this.application = application;
    }

    public ConnectFrame() {
        AppSettings set = GameController.getInstance().getSettings();

        super.size(set.getWidth() * 3 / 4, set.getHeight() * 3 / 4);
        super.center();

        this.ipText.bounds(0, 0, super.width(), super.height() / 10);
        super.add(this.ipText);
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
        } catch (IOException ex) {
            Logger.getLogger(ConnectFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
