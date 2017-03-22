public class ReverseServer implements ReverseConnection {
    @Override
    public void test(String s) {
        System.out.println("Client: " + s);
    }
}
