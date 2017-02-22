package rpc;

import java.io.IOException;
import java.lang.reflect.Proxy;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

class RpcServer implements Runnable {

    private static final int SERVER_TYPE = 0;
    private static final int CLIENT_TYPE = 1;
    private ServerSocket server;
    private Object serverProcedures;
    private Class[] clientProcedures;
    private ConnectionHandler handler;

    public RpcServer() {
    }

    public RpcServer(ServerSocket server, Object procedures) {
        this.server = server;
        this.serverProcedures = procedures;
    }

    public RpcServer(ServerSocket server, Object serverProcedures, Class[] clientProcedures, ConnectionHandler handler) {
        this.server = server;
        this.serverProcedures = serverProcedures;
        this.clientProcedures = clientProcedures;
        this.handler = handler;
    }

    public ServerSocket getServer() {
        return server;
    }

    public void setServer(ServerSocket server) {
        this.server = server;
    }

    public Object getServerProcedures() {
        return serverProcedures;
    }

    public void setServerProcedures(Object serverProcedures) {
        this.serverProcedures = serverProcedures;
    }

    public Class[] getClientProcedures() {
        return clientProcedures;
    }

    public void setClientProcedures(Class[] clientProcedures) {
        this.clientProcedures = clientProcedures;
    }

    public ConnectionHandler getHandler() {
        return handler;
    }

    public void setHandler(ConnectionHandler handler) {
        this.handler = handler;
    }

    private void accept(Socket s) throws IOException, ClassNotFoundException {
        int type = s.getInputStream().read();
        if (type == RpcServer.SERVER_TYPE) {
            ExecutionEndPoint exe = new ExecutionEndPoint(s, this.serverProcedures);
            new Thread(exe, s.getInetAddress().getHostAddress()).start();
        } else {
            InvocationEndPoint inv = new InvocationEndPoint(s);
            final Object rpcs = Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), this.clientProcedures, inv);

            try {
                Runnable rb = new Runnable() {

                    @Override
                    public void run() {
                        try {
                            handler.connected(rpcs);
                        } catch (Exception ex) {
                            Logger.getLogger(RpcServer.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                };

                new Thread(rb, s.getInetAddress().getHostAddress()).start();
            } catch (Exception ex) {
            }
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                Socket s = this.server.accept();

                this.accept(s);
            }
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(HiRpc.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
