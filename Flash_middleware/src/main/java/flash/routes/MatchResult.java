package flash.routes;

import flash.handler.Handler;

import java.util.Map;

public class MatchResult {
    public final Handler handler;
    public final Map<String, String> pathParams;

    public MatchResult(Handler handler, Map<String, String> pathParams) {
        this.handler = handler;
        this.pathParams = pathParams;
    }
}

