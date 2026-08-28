package flash.start;

import flash.broker.Broker;
import flash.invoker.Invoker;
import flash.handler.ServerRequestHandler;
import flash.marshalling.Marshaller;
import flash.registry.ObjectRegistry;
import flash.registry.RemoteComponentScanner;
import flash.routes.RouteRegistry;


public class Flash {

    private static final ObjectRegistry registry = new ObjectRegistry();
    private static final RouteRegistry routeRegistry = new RouteRegistry();
    private static final RemoteComponentScanner scanner = new RemoteComponentScanner(registry, routeRegistry);
    private static final Marshaller marshaller = new Marshaller();
    private static final Invoker invoker = new Invoker();
    private static final Broker broker = new Broker(invoker, marshaller, registry);

    public static void register(Object obj) {
        scanner.registerRemoteObject(obj);
    }

    public static void start(int port) {
        ServerRequestHandler server = new ServerRequestHandler(port, routeRegistry, broker);
        server.start();
        System.out.println("[Flash] Servidor iniciado na porta " + port);
    }
}


