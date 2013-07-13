package org.notificationengine.templating;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.notificationengine.persistance.Persister;
import org.notificationengine.templating.TemplateEngine;

public class TestTemplateEngine {

	private TemplateEngine templateEngine;
	
	@Before
	public void init() {
		
		this.templateEngine = new TemplateEngine();
		
		this.templateEngine.setTemplatesDirectory("src/test/resources/templates");
		
		this.templateEngine.loadTemplate("test");
	}
	
	@Test
	public void testProcessTemplate() {
		
		Map<String, String> item1 = new HashMap<>();
		item1.put("name", "Article1");
		item1.put("price", "10");
		
		Map<String, String> item2 = new HashMap<>();
		item2.put("name", "Article2");
		item2.put("price", "20");
		
		Collection<Map<String, String>> items = new ArrayList<>();
		items.add(item1);
		items.add(item2);
		
		Map<String, Object> context = new HashMap<>();
		context.put("items", items);
		
		String result = this.templateEngine.processTemplate("test", context);
		
		String expected = "Name: Article1\nPrice: 10\nName: Article2\nPrice: 20\n";
		
		assertEquals(expected, result);
	}

}
