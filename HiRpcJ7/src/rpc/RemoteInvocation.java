package rpc;

import java.io.Serializable;

class RemoteInvocation implements Serializable {

    private String method;
    private Class[] paramsTypes;
    private Object[] params;

    public RemoteInvocation() {
    }

    public RemoteInvocation(String method, Class[] paramsTypes, Object[] params) {
        this.method = method;
        this.paramsTypes = paramsTypes;
        this.params = params;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Class[] getParamsTypes() {
        return paramsTypes;
    }

    public void setParamsTypes(Class[] paramsTypes) {
        this.paramsTypes = paramsTypes;
    }

    public Object[] getParams() {
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }

    public static Class[] types(Object[] params) {
        if (params == null) {
            return null;
        }

        Class[] res = new Class[params.length];
        for (int i = 0; i < res.length; ++i) {
            res[i] = params[i].getClass();
        }
        return res;
    }
}
