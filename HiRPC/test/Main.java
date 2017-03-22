
import rpc.HiRpc;

public class Main {

    public static void main(String[] args) throws Exception {
        HiRpc.start(new Server(), 4321);
    }
}
