
import rpc.ConnectionHandler;
import rpc.HiRpc;
import rpc.RpcServer;

public class ServerMain {

    public static void main(String[] args) throws Exception {
        ConnectionHandler ch = (o) -> {
            ReverseConnection rc = (ReverseConnection) o;
            rc.test("Hello");
        };
        
        Class[] crpcs = {ReverseConnection.class};
        HiRpc.start(new Server(), 4321, ch, crpcs);
    }
}
