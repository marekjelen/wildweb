package cz.wildweb.impl;

import cz.wildweb.api.HttpHandler;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpRouter {

    private final Pattern urlMatch = Pattern.compile("(\\{([^}]+)\\})|(\\[([^\\]]+)\\])");

    private Map<String, HttpHandler> handlers = new LinkedHashMap<>();
    private List<HttpRouterPattern> patterns = new LinkedList<>();

    public void register(String method, String url, HttpHandler handler) {
        this.handlers.put(url, handler);
        Matcher matcher = urlMatch.matcher(url);
        List<String> names = new LinkedList<>();
        StringBuilder pattern = new StringBuilder(url);
        while(matcher.find()) {
            String single = matcher.group(2);
            String multi = matcher.group(4);
            if(single != null) {
                names.add(single);
                pattern.replace(matcher.start(2) - 1, matcher.end(2) + 1, "(?<" + single + ">[^/]+)");
            }
            if(multi != null) {
                names.add(multi);
                pattern.replace(matcher.start(4) - 1, matcher.end(4) + 1, "(?<" + multi + ">.+)");
            }
        }
        HttpRouterPattern httpRouterPattern = new HttpRouterPattern(method, url, Pattern.compile(pattern.toString()));
        LoggerFactory.getLogger(getClass()).debug("HTTP Routing pattern: {}", httpRouterPattern);
        this.patterns.add(httpRouterPattern);
    }

    public HttpRouterMatch match(String method, String url) {
        for(HttpRouterPattern pattern : this.patterns) {
            if(!pattern.method().equals("*") && !method.equals(pattern.method())) {
                continue;
            }
            Matcher matcher = pattern.pattern().matcher(url);
            if(matcher.matches()) {
                HttpRouterMatch match = new HttpRouterMatch(pattern, this.handlers.get(pattern.url()));
                for(String name : pattern.names()) {
                    match.parameters().put(name, matcher.group(name));
                }
                return match;
            }
        }
        return null;
    }

    public void unregister(HttpHandler handler) {
        for(String key : this.handlers.keySet()) {
            if(this.handlers.get(key) == handler) {
                LoggerFactory.getLogger(getClass()).debug("Removing handler {} for {}", handler.getClass().getName(), key);
                this.handlers.remove(key);
                Iterator<HttpRouterPattern> it = this.patterns.iterator();
                while(it.hasNext()) {
                    HttpRouterPattern pattern = it.next();
                    if(pattern.url().equals(key)) {
                        LoggerFactory.getLogger(getClass()).debug("Removing pattern {}", pattern);
                        it.remove();
                    }
                }
            }
        }
    }
}
