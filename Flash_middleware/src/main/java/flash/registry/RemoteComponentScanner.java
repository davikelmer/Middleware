package flash.registry;

import flash.annotations.*;
import flash.routes.RouteRegistry;

import java.lang.reflect.Method;
import java.util.UUID;

public class RemoteComponentScanner {

    private final ObjectRegistry registry;
    private final RouteRegistry routeRegistry;

    public RemoteComponentScanner(ObjectRegistry registry, RouteRegistry routeRegistry) {
        this.registry = registry;
        this.routeRegistry = routeRegistry;
    }

    public void registerRemoteObject(Object obj) {
        Class<?> clazz = obj.getClass();

        if (!clazz.isAnnotationPresent(Remote.class)) {
            System.out.printf("Classe %s ignorada: não está anotada com @Remote%n", clazz.getSimpleName());
            return;
        }

        Remote remoteAnnotation = clazz.getAnnotation(Remote.class);
        String name = remoteAnnotation.name().isEmpty() ? clazz.getSimpleName() : remoteAnnotation.name();

        String objectId = UUID.randomUUID().toString();
        registry.register(objectId, obj, name);

        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(MethodMapping.class)) {
                MethodMapping mapping = method.getAnnotation(MethodMapping.class);
                String httpMethod = mapping.method().name();
                String path = mapping.path();

                routeRegistry.register(httpMethod, path, objectId, method);
                System.out.printf("[Route] Registrado %s %s -> %s.%s (objectId=%s)%n",
                        httpMethod, path, clazz.getSimpleName(), method.getName(), objectId);
            }
        }
    }
}
