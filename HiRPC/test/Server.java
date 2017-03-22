
public class Server implements Connection {

    @Override
    public void test() {
        System.out.println("Server!");
    }

    @Override
    public int test(int i) {
        return i + 1;
    }

    @Override
    public String test(String s) {
        return "" + System.currentTimeMillis();
    }
}
