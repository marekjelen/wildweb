package cz.wildweb.api;

import java.util.HashMap;
import java.util.Map;

public class HttpContext {

    private Map<String, Object> values = new HashMap<>();

    public <A extends Object> HttpContext put(String name, A value) {
        this.values.put(name, value);
        return this;
    }

    public Object get(String name) {
        return this.values.get(name);
    }

    public <A extends Object> A get(String name, Class<? extends A> clazz) {
        return (A) this.values.get(name);
    }

}
