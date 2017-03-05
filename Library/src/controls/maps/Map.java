package controls.maps;

import com.jme3.light.LightList;
import controllers.GameController;
import controls.GameControl;

public abstract class Map extends GameControl {

    @Override
    public void create() {
        this.clearScene();
    }

    protected void clearScene() {
        GameController gc = GameController.getInstance();
        gc.getApplication().getRootNode().detachAllChildren();

        LightList ll = gc.getApplication().getRootNode().getLocalLightList();
        while (ll.size() > 0) {
            ll.remove(0);
        }
    }
}