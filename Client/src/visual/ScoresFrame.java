package visual;

import com.jme3.system.AppSettings;
import controllers.GameController;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.ArrayList;
import model.Score;

public class ScoresFrame extends Frame {

    private ArrayList<Score> session;
    private ArrayList<Score> all;

    public ScoresFrame() {
        AppSettings set = GameController.getInstance().getSettings();

        super.size(set.getWidth() * 3 / 4, set.getHeight() * 3 / 4);
        super.center();
    }

    public ArrayList<Score> getSession() {
        return session;
    }

    public void setSession(ArrayList<Score> session) {
        this.session = session;
    }

    public ArrayList<Score> getAll() {
        return all;
    }

    public void setAll(ArrayList<Score> all) {
        this.all = all;
    }

    @Override
    public void show() {
        GameController.getInstance().getApplication().getGuiNode().attachChild(this.screen);

        this.screen.addControl(this);
    }

    @Override
    public void hide() {
        this.screen.removeFromParent();
        this.screen.removeControl(this);
    }

    @Override
    public void paint(Graphics g) {
        g.setColor(new Color(0x600000ff, true));
        g.fillRect(0, 0, super.width(), super.height());

        final int lines = 22;
        final int lineSize = super.height() / lines;

        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD | Font.ITALIC, lineSize));
        FontMetrics fm = g.getFontMetrics();

        int lineY = lineSize;

        String text = "Best scores this session";
        g.setColor(Color.RED);
        g.drawString(text, (super.width() - fm.stringWidth(text)) / 2, lineY);

        lineY += lineSize;

        g.setColor(Color.BLACK);

        for (int i = 0; i < 10 && i < this.session.size(); i++) {
            text = this.session.get(i).toString();
            g.drawString(text, 0, lineY);
            lineY += lineSize;
        }

        text = "Best scores";
        g.setColor(Color.RED);
        g.drawString(text, (super.width() - fm.stringWidth(text)) / 2, lineY);

        lineY += lineSize;

        g.setColor(Color.BLACK);

        for (int i = 0; i < 10 && i < this.all.size(); i++) {
            text = this.all.get(i).toString();
            g.drawString(text, 0, lineY);
            lineY += lineSize;
        }
    }
}
