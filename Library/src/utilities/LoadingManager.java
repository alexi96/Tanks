package utilities;

import com.jme3.asset.AssetManager;
import com.jme3.asset.AssetNotFoundException;
import com.jme3.asset.TextureKey;
import com.jme3.material.MatParamTexture;
import com.jme3.material.Material;
import com.jme3.material.MaterialDef;
import com.jme3.material.RenderState;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import com.jme3.texture.Texture;
import com.jme3.util.TangentBinormalGenerator;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoadingManager {

    private final AssetManager assetManager;
    private final HashMap<Texture, Texture> textures = new HashMap<>();

    public LoadingManager(AssetManager assetManager) {
        this.assetManager = assetManager;
    }

    public static void transparent(Spatial s) {
        if (s instanceof Node) {
            Node n = (Node) s;
            List<Spatial> ch = n.getChildren();
            for (Spatial c : ch) {
                LoadingManager.transparent(c);
            }
        } else if (s instanceof Geometry) {
            Geometry g = (Geometry) s;
            Material m = g.getMaterial();

            g.setQueueBucket(RenderQueue.Bucket.Transparent);
            m.getAdditionalRenderState().setAlphaTest(true);
            m.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
            m.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        }
    }

    public static <C extends Control> C findControl(Spatial s, Class<C> c) {
        C r = s.getControl(c);
        if (s.getControl(c) != null) {
            return r;
        }

        Spatial p = s.getParent();
        if (p == null) {
            return null;
        }

        return LoadingManager.findControl(p, c);
    }

    public static Spatial findByName(Spatial s, String b) {
        if (s.getName().contains(b)) {
            return s;
        }

        if (s instanceof Node) {
            Node n = (Node) s;
            List<Spatial> ch = n.getChildren();
            for (Spatial c : ch) {
                Spatial res = LoadingManager.findByName(c, b);
                if (res != null) {
                    return res;
                }
            }
        }
        return null;
    }

    public void loadTextures(Spatial s) {
        if (s instanceof Node) {
            Node n = (Node) s;
            List<Spatial> ch = n.getChildren();
            for (Spatial c : ch) {
                this.loadTextures(c);
            }
        } else if (s instanceof Geometry) {
            this.loadTexture((Geometry) s);
        }
    }

    private void loadTexture(Geometry g) {
        Material m = g.getMaterial();
        if (m == null) {
            return;
        }
        MaterialDef def = m.getMaterialDef();
        if (def == null) {
            return;
        }
        String an = def.getAssetName();
        if (an == null || !an.equals("Common/MatDefs/Light/Lighting.j3md")) {
            return;
        }
        MatParamTexture param = m.getTextureParam("DiffuseMap");
        if (param == null) {
            return;
        }
        Texture diff = param.getTextureValue();
        if (diff == null) {
            return;
        }

        Texture norm = this.textures.get(diff);
        if (norm != null) {
            TangentBinormalGenerator.generate(g);
            m.setTexture("NormalMap", norm);
            return;
        }

        String key = diff.getKey().getName();
        key = key.substring(0, key.length() - diff.getKey().getExtension().length());
        key += "n.";
        try {
            norm = this.assetManager.loadTexture(new TextureKey(key + "jpg", false));
        } catch (AssetNotFoundException e) {
            try {
                norm = this.assetManager.loadTexture(new TextureKey(key + "png", false));
            } catch (AssetNotFoundException ex) {
                return;
            }
        }

        norm.setWrap(Texture.WrapMode.Repeat);
        this.textures.put(diff, norm);
        TangentBinormalGenerator.generate(g);
        m.setTexture("NormalMap", norm);
    }

    public static void loadNames(Spatial s) {
        LoadingManager.loadName(s);

        if (!(s instanceof Node)) {
            return;
        }

        Node n = (Node) s;
        List<Spatial> ch = n.getChildren();
        for (Spatial c : ch) {
            LoadingManager.loadNames(c);
        }
    }

    public static void loadName(Spatial s) {
        String name = s.getName();
        if (!name.contains("#")) {
            return;
        }
        String[] names = name.split("#");
        for (int i = 1; i < names.length; ++i) {
            StringBuilder methodName = new StringBuilder(names[i]);
            try {
                methodName.setCharAt(0, Character.toLowerCase(methodName.charAt(0)));
                Method m = Names.class.getDeclaredMethod(methodName.toString(), Spatial.class);
                m.setAccessible(true);
                m.invoke(null, s);
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                Logger.getLogger(LoadingManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public Spatial loadModel(String key) {
        Spatial s = this.assetManager.loadModel(key);
        this.loadTextures(s);
        LoadingManager.loadNames(s);
        return s;
    }
}
