package cz.wildweb.server.templates;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

public class ftl implements GenericTemplate {

    @Override
    public String render(Map<String, Object> variables, String path) {
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_23);

        File templates = new File("templates");
        if(templates.exists()) {
            try {
                configuration.setDirectoryForTemplateLoading(templates);
                configuration.setDefaultEncoding("UTF-8");
                configuration.setShowErrorTips(true);
                configuration.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            Template template = configuration.getTemplate(path);
            StringWriter result = new StringWriter();
            template.process(variables, result);
            return result.toString();
        } catch (IOException | TemplateException e) {
            e.printStackTrace();
            return "";
        }
    }

}
