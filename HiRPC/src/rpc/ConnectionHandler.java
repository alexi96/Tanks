package rpc;

@FunctionalInterface
public interface ConnectionHandler {

    void connected(Object proc) throws Exception;
}
