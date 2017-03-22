
import rpc.HiRpc;

public class ClientMain {

    public static void main(String[] args) throws Exception {
        Connection c = (Connection) HiRpc.connect("localhost", 4321, Connection.class);
        int t = c.test(7);
        System.out.println(t);
        System.out.println(c.test("aaa"));
        
        ReverseServer rs = new ReverseServer();
        HiRpc.connectReverse("localhost", 4321, rs);
    }
}
