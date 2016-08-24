package cz.wildweb.server.templates;

import java.util.Map;

public interface GenericTemplate {

    String render(Map<String, Object> variables, String path);

}
