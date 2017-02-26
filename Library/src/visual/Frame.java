package visual;

import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.input.RawInputListener;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import com.jme3.scene.shape.Quad;
import com.jme3.system.AppSettings;
import com.jme3.texture.Texture2D;
import com.jme3.texture.plugins.AWTLoader;
import controllers.GameController;
import java.io.IOException;

public class Frame extends Panel implements Control, RawInputListener {

    private static final AWTLoader LOADER = new AWTLoader();
    public static boolean ignoreInput;
    protected Geometry screen = new Geometry("Screen", new Quad(1, 1));
    private Texture2D texture = new Texture2D();
    protected boolean valid;

    public Frame() {
        Material mat = new Material(GameController.getInstance().getApplication().getAssetManager(), "Common/MatDefs/Gui/Gui.j3md");
        mat.setColor("Color", ColorRGBA.White);
        mat.setTexture("Texture", this.texture);
        this.screen.setMaterial(mat);

        this.screen.setQueueBucket(RenderQueue.Bucket.Gui);
        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
    }

    public void center() {
        AppSettings s = GameController.getInstance().getSettings();
        int lx = (s.getWidth() - super.width()) / 2;
        int ly = (s.getHeight() - super.height()) / 2;
        this.location(lx, ly);
    }

    @Override
    public void location(int x, int y) {
        super.location(x, y);
        if (this.image != null) {
            this.screen.setLocalTranslation(x, Frame.toY(y, super.height()), 0);
        }
    }

    @Override
    public void size(int w, int h) {
        super.size(w, h);
        this.texture.setImage(Frame.LOADER.load(this.image, true));
        this.screen.setLocalScale(w, h, 1);

        this.location(this.x, this.y);
    }

    @Override
    public void invalidate() {
        this.valid = false;
    }

    public boolean visible() {
        return this.screen.getParent() != null;
    }

    public void show() {
        GameController.getInstance().getApplication().getFlyByCamera().setRotationSpeed(0);
        GameController.getInstance().getApplication().getInputManager().setCursorVisible(true);
        GameController.getInstance().getApplication().getFlyByCamera().setDragToRotate(true);
        GameController.getInstance().getApplication().getGuiNode().attachChild(this.screen);

        this.screen.addControl(this);

        GameController.getInstance().getApplication().getInputManager().addRawInputListener(this);
    }

    public void hide() {
        GameController.getInstance().getApplication().getFlyByCamera().setRotationSpeed(1);
        GameController.getInstance().getApplication().getInputManager().setCursorVisible(false);

        this.screen.removeFromParent();
        this.screen.removeControl(this);

        GameController.getInstance().getApplication().getInputManager().removeRawInputListener(this);
    }

    @Override
    public Control cloneForSpatial(Spatial spatial) {
        return null;
    }

    @Override
    public void setSpatial(Spatial spatial) {
    }

    @Override
    public void update(float tpf) {
        if (this.valid) {
            return;
        }
        this.paint();

        this.texture.setImage(Frame.LOADER.load(this.image, true));
        this.valid = true;
    }

    @Override
    public void render(RenderManager rm, ViewPort vp) {
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
    }

    @Override
    public void read(JmeImporter im) throws IOException {
    }

    @Override
    public void onMouseMotionEvent(MouseMotionEvent evt) {
        int tx = evt.getX();
        int ty = evt.getY();

        ty = Frame.toY(ty, 0);
        tx -= super.x;
        ty -= super.y;

        super.onMouseMotionEvent(new MouseMotionEvent(tx, ty, evt.getDX(), -evt.getDY(), evt.getWheel(), evt.getDeltaWheel()));
    }

    @Override
    public void onMouseButtonEvent(MouseButtonEvent evt) {
        int tx = evt.getX();
        int ty = evt.getY();

        ty = Frame.toY(ty, 0);
        tx -= super.x;
        ty -= super.y;

        super.onMouseButtonEvent(new MouseButtonEvent(evt.getButtonIndex(), evt.isPressed(), tx, ty));
    }

    private static int toY(int y, int h) {
        AppSettings s = GameController.getInstance().getSettings();
        return s.getHeight() - y - h;
    }
}
