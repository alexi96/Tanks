package rpc;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

class ExecutionEndPoint implements Runnable {

    private Socket socket;
    private Object procedures;

    public ExecutionEndPoint() {
    }

    public ExecutionEndPoint(Socket socket, Object procedures) {
        this.socket = socket;
        this.procedures = procedures;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public Object getProcedures() {
        return procedures;
    }

    public void setProcedures(Object procedures) {
        this.procedures = procedures;
    }

    private void invocation(ObjectInputStream in, ObjectOutputStream out) throws IOException {
        try {
            RemoteInvocation inv = (RemoteInvocation) in.readObject();

            Method m = this.procedures.getClass().getMethod(inv.getMethod(), inv.getParamsTypes());
            Object res = m.invoke(this.procedures, (Object[]) inv.getParams());
            out.writeObject(res);
        } catch (ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(ExecutionEndPoint.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        try {
            ObjectInputStream in = new ObjectInputStream(this.socket.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(this.socket.getOutputStream());

            while (true) {
                this.invocation(in, out);
            }
        } catch (IOException ex) {
        } finally {
            try {
                this.socket.close();
            } catch (IOException e) {
            }
        }
    }
}
