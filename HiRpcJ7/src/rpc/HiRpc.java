package rpc;

import java.io.IOException;
import java.lang.reflect.Proxy;
import java.net.ServerSocket;
import java.net.Socket;

public final class HiRpc {

    private HiRpc() {
    }

    public static void start(Object proc, int port, ConnectionHandler handler, Class... clientProc) throws IOException {
        final ServerSocket ss = new ServerSocket(port);

        String name = "Server ";
        if (proc != null) {
            name += proc.getClass().getSimpleName();
        }
        name += " (" + port + ')';
        RpcServer server = new RpcServer(ss, proc, clientProc, handler);
        server.setHandler(handler);
        new Thread(server, name).start();
    }

    public static void start(Object proc, int port) throws IOException {
        ConnectionHandler handler = new ConnectionHandler() {
            @Override
            public void connected(Object proc) throws Exception {
            }
        };
        HiRpc.start(proc, port, handler, (Class[]) null);
    }

    public static Object connect(String ip, int port, Class... procs) throws IOException {
        Socket s = new Socket(ip, port);
        s.getOutputStream().write(0);

        InvocationEndPoint inv = new InvocationEndPoint(s);
        Object r = Proxy.newProxyInstance(HiRpc.class.getClassLoader(), procs, inv);
        return r;
    }

    public static <C> C connectSimple(String ip, int port, Class<C> proc) throws IOException {
        Class[] procs = {proc};
        return (C) HiRpc.connect(ip, port, procs);
    }

    public static void connectReverse(String ip, int port, Object client) throws IOException {
        Socket s = new Socket(ip, port);

        ExecutionEndPoint exe = new ExecutionEndPoint(s, client);
        String name = "Client " + client.getClass().getSimpleName() + " (" + port + ')';
        new Thread(exe, name).start();

        s.getOutputStream().write(1);
    }
}
