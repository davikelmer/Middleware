package flash.handler;

import java.lang.reflect.Method;

public class Handler {
    public final String objectId;
    public final Method method;

    public Handler(String objectId, Method method) {
        this.objectId = objectId;
        this.method = method;
    }
}
