package org.notificationengine.persistance;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.mongodb.ServerAddress;

public class TestMongoDbUtils {

	@Test
	public void testGetPortFromSingleServerUrl() {
		
		int port = MongoDbUtils.getPortFromSingleServerUrl("localhost:27017");
		
		assertEquals(27017, port);
	}
	
	@Test
	public void testGetHostFromSingleServerUrl() {
		
		String host = MongoDbUtils.getHostFromSingleServerUrl("localhost:27017");
		
		assertEquals("localhost", host);
	}
	
	@Test
	public void testGetServerAddressListFromMultipleServerUrl() {
		
		List<ServerAddress> addrs = MongoDbUtils.getServerAddressListFromMultipleServerUrl("localhost:27017,localhost:37017");
		
		assertEquals(2, addrs.size());
	}
}
