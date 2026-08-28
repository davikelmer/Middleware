package flash.routes;

import flash.handler.Handler;

import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RouteRegistry {

    private final Map<String, List<RegisteredRoute>> routes = new HashMap<>();

    public void register(String httpMethod, String pathTemplate, String objectId, Method method) {
        String regex = pathTemplate.replaceAll("\\{[^}]+}", "([^/]+)");
        Pattern pattern = Pattern.compile("^" + regex + "$");

        List<String> paramNames = extractParamNames(pathTemplate);

        RegisteredRoute route = new RegisteredRoute(pattern, pathTemplate, paramNames, new Handler(objectId, method));
        routes.computeIfAbsent(httpMethod.toUpperCase(), k -> new ArrayList<>()).add(route);
    }

    public MatchResult getHandler(String httpMethod, String requestPath) {
        List<RegisteredRoute> methodRoutes = routes.get(httpMethod.toUpperCase());
        if (methodRoutes == null) return null;
        for (RegisteredRoute route : methodRoutes) {
            Matcher matcher = route.getPattern().matcher(requestPath);
            if (matcher.matches()) {
                Map<String, String> pathParams = new HashMap<>();
                for (int i = 0; i < route.getParamNames().size(); i++) {
                    pathParams.put(route.getParamNames().get(i), matcher.group(i + 1));
                }
                return new MatchResult(route.getHandler(), pathParams);
            }
        }
        return null;
    }

    private List<String> extractParamNames(String path) {
        List<String> names = new ArrayList<>();
        Matcher matcher = Pattern.compile("\\{([^}]+)}").matcher(path);
        while (matcher.find()) {
            names.add(matcher.group(1));
        }
        return names;
    }
}

