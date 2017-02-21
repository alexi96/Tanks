package rpc;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

class InvocationEndPoint implements InvocationHandler {

    public Socket socket;
    public ObjectInputStream input;
    public ObjectOutputStream output;

    public InvocationEndPoint() {
    }

    public InvocationEndPoint(Socket socket) {
        this.prepareSocket(socket);
    }

    public Socket getSocket() {
        return socket;
    }

    public final void prepareSocket(Socket s) {
        try {
            this.socket = s;

            this.output = new ObjectOutputStream(s.getOutputStream());
            this.input = new ObjectInputStream(s.getInputStream());
        } catch (IOException ex) {
            Logger.getLogger(InvocationEndPoint.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        synchronized (this) {
            RemoteInvocation inv = new RemoteInvocation(method.getName(), method.getParameterTypes(), args);
            this.output.reset();
            this.output.writeObject(inv);
            return this.input.readObject();
        }
    }
}
