package org.notificationengine.mail;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.notificationengine.constants.Constants;

public class TestMailOptionsUtils {

	@Test
	public void testBuildMailOptionsFromContextWithGoodFromAndSubject() {
		
		Map<String, Object> context = new HashMap<>();
		context.put(Constants.FROM, "from");
		context.put(Constants.SUBJECT, "subject");
		
		Map<String,String> result = MailOptionsUtils.buildMailOptionsFromContext(context);
		
		assertEquals("from", result.get(Constants.FROM));
		assertEquals("subject", result.get(Constants.SUBJECT));
	}

	@Test
	public void testBuildMailOptionsFromContextWithGoodFromAndEmptySubject() {
		
		Map<String, Object> context = new HashMap<>();
		context.put(Constants.FROM, "from");
		
		Map<String,String> result = MailOptionsUtils.buildMailOptionsFromContext(context);
		
		assertEquals("from", result.get(Constants.FROM));
		assertNull(result.get(Constants.SUBJECT));
	}
	
	@Test
	public void testBuildMailOptionsFromContextWithEmptyFromAndGoodSubject() {
		
		Map<String, Object> context = new HashMap<>();
		context.put(Constants.SUBJECT, "subject");
		
		Map<String,String> result = MailOptionsUtils.buildMailOptionsFromContext(context);
		
		assertEquals("subject", result.get(Constants.SUBJECT));
		assertNull(result.get(Constants.FROM));
	}
	
	@Test
	public void testBuildMailOptionsFromContextWithEmptyFromAndEmptySubject() {
		
		Map<String, Object> context = new HashMap<>();
		
		Map<String,String> result = MailOptionsUtils.buildMailOptionsFromContext(context);
		
		assertNull(result.get(Constants.SUBJECT));
		assertNull(result.get(Constants.FROM));
	}
	
	@Test
	public void testBuildMailOptionsFromContextsWithGoodFromAndSubject() {
		
		Collection<Map<String, Object>> contexts = new ArrayList<>();
		
		Map<String, Object> context1 = new HashMap<>();
		context1.put(Constants.FROM, "from");
		context1.put(Constants.SUBJECT, "subject");
		contexts.add(context1);
		
		Map<String, Object> context2 = new HashMap<>();
		context2.put(Constants.FROM, "from");
		context2.put(Constants.SUBJECT, "subject");
		contexts.add(context2);
		
		Map<String, Object> context3 = new HashMap<>();
		contexts.add(context3);
		
		Map<String,String> result = MailOptionsUtils.buildMailOptionsFromContexts(contexts);
		
		assertEquals("from", result.get(Constants.FROM));
		assertEquals("subject", result.get(Constants.SUBJECT));
	}
	
	@Test
	public void testBuildMailOptionsFromContextsWithGoodFromAndEmptySubject() {
		
		Collection<Map<String, Object>> contexts = new ArrayList<>();
		
		Map<String, Object> context1 = new HashMap<>();
		context1.put(Constants.FROM, "from");
		context1.put(Constants.SUBJECT, "subject");
		contexts.add(context1);
		
		Map<String, Object> context2 = new HashMap<>();
		context2.put(Constants.FROM, "from");
		context2.put(Constants.SUBJECT, "subjecte");
		contexts.add(context2);
		
		Map<String, Object> context3 = new HashMap<>();
		context3.put(Constants.FROM, "from");
		context3.put(Constants.SUBJECT, "subject");
		contexts.add(context3);
		
		Map<String,String> result = MailOptionsUtils.buildMailOptionsFromContexts(contexts);
		
		assertEquals("from", result.get(Constants.FROM));
		assertNull(result.get(Constants.SUBJECT));
	}
	
	@Test
	public void testBuildMailOptionsFromContextsWithEmptyFromAndGoodSubject() {
		
		Collection<Map<String, Object>> contexts = new ArrayList<>();
		
		Map<String, Object> context1 = new HashMap<>();
		context1.put(Constants.FROM, "frome");
		context1.put(Constants.SUBJECT, "subject");
		contexts.add(context1);
		
		Map<String, Object> context2 = new HashMap<>();
		context2.put(Constants.FROM, "from");
		context2.put(Constants.SUBJECT, "subject");
		contexts.add(context2);
		
		Map<String, Object> context3 = new HashMap<>();
		context3.put(Constants.FROM, "from");
		context3.put(Constants.SUBJECT, "subject");
		contexts.add(context3);
		
		Map<String,String> result = MailOptionsUtils.buildMailOptionsFromContexts(contexts);
		
		assertEquals("subject", result.get(Constants.SUBJECT));
		assertNull(result.get(Constants.FROM));
	}
	
	@Test
	public void testBuildMailOptionsFromContextsWithEmptyFromAndEmptySubject() {
		
		Collection<Map<String, Object>> contexts = new ArrayList<>();
		
		Map<String, Object> context1 = new HashMap<>();
		context1.put(Constants.FROM, "frome");
		context1.put(Constants.SUBJECT, "subject");
		contexts.add(context1);
		
		Map<String, Object> context2 = new HashMap<>();
		context2.put(Constants.FROM, "from");
		context2.put(Constants.SUBJECT, "subject");
		contexts.add(context2);
		
		Map<String, Object> context3 = new HashMap<>();
		context3.put(Constants.FROM, "from");
		context3.put(Constants.SUBJECT, "subjecte");
		contexts.add(context3);
		
		Map<String,String> result = MailOptionsUtils.buildMailOptionsFromContexts(contexts);
		
		assertNull(result.get(Constants.SUBJECT));
		assertNull(result.get(Constants.FROM));
	}
}
