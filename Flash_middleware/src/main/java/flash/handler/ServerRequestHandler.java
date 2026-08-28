package flash.handler;

import flash.broker.Broker;
import flash.routes.MatchResult;
import flash.routes.RouteRegistry;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONObject;


public class ServerRequestHandler {

    private final int port;
    private final RouteRegistry routeRegistry;
    private final Broker broker;
    private ExecutorService executor;

    public ServerRequestHandler(int port, RouteRegistry routeRegistry, Broker broker) {
        this.port = port;
        this.routeRegistry = routeRegistry;
        this.broker = broker;
        this.executor = Executors.newCachedThreadPool();
    }

    public void start() {
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                System.out.println("[Middleware] Servidor HTTP iniciado na porta " + port);
                while (!Thread.currentThread().isInterrupted()) {
                    Socket clientSocket = serverSocket.accept();
                    executor.submit(() -> handleClient(clientSocket));
                }
            } catch (IOException e) {
                System.err.println("[Middleware] Erro no servidor HTTP: " + e.getMessage());
            }
        }).start();
    }

    private void handleClient(Socket clientSocket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             OutputStream out = clientSocket.getOutputStream()) {

            String requestLine = in.readLine();
            if (requestLine == null || requestLine.isEmpty()) {
                sendHttpResponse(out, 400, "{\"error\":\"Requisição vazia\"}");
                return;
            }

            String[] parts = requestLine.split(" ");
            if (parts.length < 2) {
                sendHttpResponse(out, 400, "{\"error\":\"Requisição malformada\"}");
                return;
            }

            String httpMethod = parts[0];
            String path = parts[1];

            int contentLength = 0;
            String line;
            while ((line = in.readLine()) != null && !line.isEmpty()) {
                if (line.toLowerCase().startsWith("content-length:")) {
                    contentLength = Integer.parseInt(line.split(":")[1].trim());
                }
            }

            String body = "";
            if (httpMethod.equalsIgnoreCase("POST") || httpMethod.equalsIgnoreCase("PUT")) {
                if (contentLength > 0) {
                    char[] bodyChars = new char[contentLength];
                    int read = in.read(bodyChars);
                    body = new String(bodyChars, 0, read);
                }
            }

            JSONObject requestBody = body.isEmpty() ? new JSONObject() : new JSONObject(body);

            MatchResult match = routeRegistry.getHandler(httpMethod, path);
            if (match == null || match.handler == null) {
                sendHttpResponse(out, 404, "{\"error\":\"Rota não encontrada para " + httpMethod + " " + path + "\"}");
                return;
            }

            JSONObject input = new JSONObject();
            if (match.pathParams != null) {
                for (var e : match.pathParams.entrySet()) {
                    input.put(e.getKey(), e.getValue());
                }
            }
            if (requestBody != null) {
                for (String key : requestBody.keySet()) {
                    input.put(key, requestBody.get(key));
                }
            }

            JSONObject responseJson = broker.process(match.handler, input);

            sendHttpResponse(out, 200, responseJson.toString());

        } catch (java.net.SocketException e) {
            if (e.getMessage() != null && e.getMessage().contains("anulada")) {
            } else {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException ignored) {}
        }
    }

    private void sendHttpResponse(OutputStream out, int statusCode, String body) throws IOException {
        String statusText = switch (statusCode) {
            case 200 -> "OK";
            case 400 -> "Bad Request";
            case 404 -> "Not Found";
            case 405 -> "Method Not Allowed";
            default -> "Internal Server Error";
        };

        byte[] bodyBytes = body.getBytes(StandardCharsets.UTF_8);
        String responseHeaders = "HTTP/1.1 " + statusCode + " " + statusText + "\r\n" +
                "Content-Type: application/json\r\n" +
                "Content-Length: " + bodyBytes.length + "\r\n" +
                "Connection: close\r\n" +
                "\r\n";

        out.write(responseHeaders.getBytes(StandardCharsets.UTF_8));
        out.write(bodyBytes);
        out.flush();

        System.out.println("[Middleware] Resposta enviada: " + body);
    }
}

