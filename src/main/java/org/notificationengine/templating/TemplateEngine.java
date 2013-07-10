package org.notificationengine.templating;

import java.io.File;
import java.io.FileReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.notificationengine.constants.Constants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

@Component(value=Constants.TEMPLATE_ENGINE)
public class TemplateEngine {

	private static Logger LOGGER = Logger.getLogger(TemplateEngine.class);
	
	private MustacheFactory templateFactory;
	
	@Value("${templates.directory}")
	private String templatesDirectory;

	private Map<String, Mustache> templates;
	
	public TemplateEngine() {
		
		this.templateFactory = new DefaultMustacheFactory();
		
		this.templates = new HashMap<>();
	}
	
	public void loadTemplate(String templateName) {
		try {
			File templateFile = new File(this.templatesDirectory + System.getProperty("file.separator") + templateName + Constants.TEMPLATE_EXTENSION);
			
			Mustache template = this.templateFactory.compile(new FileReader(templateFile), templateName);
			
			this.templates.put(templateName, template);
			
			LOGGER.debug("Loaded template : " + templateName);
		}
		catch(Exception e) {
			
			LOGGER.error(ExceptionUtils.getFullStackTrace(e));
			
			LOGGER.error("Unable to load template " + templateName);
		}
	}
	
	private Mustache getTemplate(String templateName) {
		
		return this.templates.get(templateName);
	}
	
	public String processTemplate(String templateName, Map<String, Object> context) {
		
		StringWriter result = new StringWriter();
		
		Mustache template = this.getTemplate(templateName);
		
		template.execute(result, context);
		
		return result.toString();
	}
	
	public Map<String, Mustache> getTemplates() {
		return templates;
	}

	public void setTemplates(Map<String, Mustache> templates) {
		this.templates = templates;
	}

	public String getTemplatesDirectory() {
		return templatesDirectory;
	}

	public void setTemplatesDirectory(String templatesDirectory) {
		this.templatesDirectory = templatesDirectory;
	}
	
	
}
