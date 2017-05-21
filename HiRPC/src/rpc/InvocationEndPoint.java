package rpc;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InvocationEndPoint implements InvocationHandler {

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
            Logger.getLogger(HiRpc.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            Object.class.getDeclaredMethod(method.getName(), method.getParameterTypes());
            if (method.getName().equals("equals")) {
                return proxy == args[0];
            }
            return method.invoke(this, args);
        } catch (NoSuchMethodException | SecurityException e) {
        }
        
        synchronized (this) {
            RemoteInvocation inv = new RemoteInvocation(method.getName(), method.getParameterTypes(), args);
            this.output.reset();
            //this.output.writeObject(inv);
            this.output.writeObject(inv);
            if (method.getReturnType().equals(Void.TYPE)) {
                return Void.TYPE;
            }
            return this.input.readObject();
        }
    }
}
