package flash.broker;

import flash.handler.Handler;
import flash.invoker.Invoker;
import flash.marshalling.Marshaller;
import flash.registry.ObjectRegistry;
import org.json.JSONObject;

public class Broker {

    private final Invoker invoker;
    private final Marshaller marshaller;
    private final ObjectRegistry registry;

    public Broker(Invoker invoker, Marshaller marshaller, ObjectRegistry registry) {
        this.invoker = invoker;
        this.marshaller = marshaller;
        this.registry = registry;
    }

    public JSONObject process(Handler handler, JSONObject input) {
        try {
            Object target = registry.getById(handler.objectId);
            if (target == null) {
                return new JSONObject().put("error", "Objeto remoto não encontrado: " + handler.objectId);
            }
            Object[] args = marshaller.unmarshall(input, handler.method);
            Object result = invoker.invoke(target, handler.method, args);
            return marshaller.marshall(result);
        } catch (Exception e) {
            return new JSONObject().put("error", e.getMessage());
        } catch (Throwable e) {
            e.printStackTrace();
            return new JSONObject().put("error", "Erro interno: " + e.getMessage());
        }
    }
}