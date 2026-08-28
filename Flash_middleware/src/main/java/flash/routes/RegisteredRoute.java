package flash.routes;

import flash.handler.Handler;

import java.util.List;
import java.util.regex.Pattern;

public class RegisteredRoute {
    private final Pattern pattern;
    private final String originalPath;
    private final List<String> paramNames;
    private final Handler handler;

    public RegisteredRoute(Pattern pattern, String originalPath, List<String> paramNames, Handler handler) {
        this.pattern = pattern;
        this.originalPath = originalPath;
        this.paramNames = paramNames;
        this.handler = handler;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public String getOriginalPath() {
        return originalPath;
    }

    public List<String> getParamNames() {
        return paramNames;
    }

    public Handler getHandler() {
        return handler;
    }
}
