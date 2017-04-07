package controls.maps;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.util.SkyFactory;
import com.jme3.water.WaterFilter;
import controllers.GameController;
import utilities.LoadingManager;

public class HillMap extends Map {

    @Override
    public void create() {
        GameController gc = GameController.getInstance();
        Spatial s = gc.getApplication().getAssetManager().loadModel("Models/HillMap.j3o");
        SimpleApplication app = gc.getApplication();

        boolean server = GameController.getInstance().getSynchronizer() != null;
        
        LoadingManager.loadNames(s);
        
        if (server) {
            RigidBodyControl rbc = new RigidBodyControl(0);
            s.addControl(rbc);
            gc.getPhysics().add(rbc);
        } else {
            s.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);

            gc.getLoader().loadTextures(s);

            Spatial sky = SkyFactory.createSky(app.getAssetManager(), "Textures/BrightSky.dds", SkyFactory.EnvMapType.CubeMap);
            app.getRootNode().attachChild(sky);

            DirectionalLight sun = new DirectionalLight();
            sun.setDirection((new Vector3f(-0.5f, -0.5f, -0.5f)).normalizeLocal());
            sun.setColor(ColorRGBA.White);
            gc.getApplication().getRootNode().addLight(sun);

            AmbientLight ambient = new AmbientLight();
            ambient.setColor(ColorRGBA.DarkGray);
            gc.getApplication().getRootNode().addLight(ambient);

            DirectionalLightShadowRenderer dlsr = new DirectionalLightShadowRenderer(app.getAssetManager(), 1024, 2);
            dlsr.setLight(sun);
            app.getViewPort().addProcessor(dlsr);
            
            WaterFilter water = new WaterFilter((Node) s, sun.getDirection());
            water.setReflectionScene(s);
            water.setLightColor(ColorRGBA.White);
            water.setLightDirection(sun.getDirection());
            water.setDeepWaterColor(ColorRGBA.Cyan);
            FilterPostProcessor fpp = new FilterPostProcessor(app.getAssetManager());
            fpp.addFilter(water);
            app.getViewPort().addProcessor(fpp);
        }

        gc.getApplication().getRootNode().attachChild(s);
    }
}
