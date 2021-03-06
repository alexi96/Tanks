package visual.connect;

import com.jme3.input.KeyInput;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import visual.Component;

public class TextField extends Component {

    private StringBuilder text = new StringBuilder();
    private String toolTip;
    private boolean selected;

    public TextField() {
    }

    public TextField(String toolTip) {
        this.toolTip = toolTip;
    }

    public String getToolTip() {
        return toolTip;
    }

    public void setToolTip(String toolTip) {
        this.toolTip = toolTip;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
    
    public void input(char ch, int code) {
        if (Character.isLetterOrDigit(ch) || (ch > 32 && ch < 128)) {
            this.text.append(ch);
        } else if (code == KeyInput.KEY_BACK) {
            if (this.text.length() > 0) {
                this.text.deleteCharAt(this.text.length() - 1);
            }
        }

        super.invalidate();
    }
    
    public String text() {
        return this.text.toString();
    }

    public void changeText(String text) {
        this.text.setLength(0);
        this.text.append(text);
    }
    
    @Override
    public void paint(Graphics g) {
        g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, super.height()));
        FontMetrics fm = g.getFontMetrics();

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, super.width(), super.height());
        
        if (this.text.length() == 0) {
            g.setColor(Color.LIGHT_GRAY);
        g.drawString(this.toolTip, 0, (super.height() + fm.getHeight() / 2) / 2);
            return;
        }
        
        
        g.setColor(Color.BLACK);
        g.drawString(this.text.toString(), 0, (super.height() + fm.getHeight() / 2) / 2);
    }
}
