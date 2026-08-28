package flash.invoker;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Invoker {

    public Object invoke(Object target, Method method, Object[] args) throws Exception {
        try {
            return method.invoke(target, args);
        } catch (InvocationTargetException e) {
            throw e;
        }
    }
}
