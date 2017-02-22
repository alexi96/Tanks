package connection;

import controls.NetworkControl;
import java.util.Collection;

public interface GameConnection {

    void create(Collection<NetworkControl> cts);

    void destroy(Collection<NetworkControl> cts);

    void update(Collection<NetworkControl> cts);
}
