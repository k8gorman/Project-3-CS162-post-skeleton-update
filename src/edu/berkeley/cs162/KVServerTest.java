package edu.berkeley.cs162;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class KVServerTest { 
	
	@Test
	public void testPutExeptionThrowing() {
		KVServer testServer = new KVServer(256, 256*1024);
	
		String largeKey = "";
		String smallKey = "";
		String largeValue = "";
		String smallValue = ""; 
		
		for(int i = 0; i < 254; i++){
			largeKey += "a a ";
			smallKey += "a";
			
		}
		
		for(int i = 0; i < 1024; i++){
			largeValue += largeKey;
		}
		smallValue = smallKey;
		
		try{
			testServer.put(largeKey, smallValue);
		} catch (KVException e) {
			String toCheck = e.getMsg().getMessage();
			assertTrue(toCheck.equals("Oversized key"));
		}
		
		try{
			testServer.put(smallKey, largeValue);
		} catch (KVException e) {
			assertTrue(e.getMsg().getMessage().equals("Oversized value"));
		}

	}
	
	@Test
	public void testPut() {
		KVServer testServer = new KVServer(256, 256*1024);
		
		String key = "CS162";
		String value = "nachos-project";
		
		try{
			testServer.put(key, value);
		} catch (KVException e){
			fail("Exception is thrown for put operation");
		}
		
		String valueInCache = testServer.getDataCache("givemedatacache").get(key);
		String valueInStore = "";
		try{
			valueInStore = testServer.getDataStore("givemedatastore").get(key);
		} catch (KVException e){
			fail("Exception is thrown for put operation");
		}
		
		assertTrue(valueInCache.equals(value));
		assertTrue(valueInStore.equals(value));
		
		//Make sure put also updates values if already in cache/store
		value = "client-server-project";
		try{
			testServer.put(key, value);
		} catch (KVException e){
			fail("Exception is thrown for put operation");
		}
		valueInCache = testServer.getDataCache("givemedatacache").get(key);
		try{
			valueInStore = testServer.getDataStore("givemedatastore").get(key);
		} catch (KVException e){
			fail("Exception is thrown for put operation");
		}
		assertTrue(valueInCache.equals(value));
		assertTrue(valueInStore.equals(value));
	}

	@Test
	public void testGet() {
		KVServer testServer = new KVServer(256, 256*1024);
		
		KVStore dStore = testServer.getDataStore("givemedatastore");
		KVCache dCache = testServer.getDataCache("givemedatastore");
		
		String key = "CS162";
		String value = "nachos-project";
		
		String key1 = "CS184";
		String value1 = "raytrace";
		
		//Manually add k/v pairs to testServer
		try {
			dStore.put(key, value);
		} catch (KVException e) {
			fail();
		}
		
		try {
			dStore.put(key1, value1);
		} catch (KVException e) {
			fail();
		}
		
		//Adding through server methods
		String key2 = "CS186";
		String value2 = "cache-evict-algorithm";
		
		try {
			testServer.put(key2, value2);
		} catch (KVException e) {
			fail();
		}
		
		//Check for existence of previously inserted keys
		try {
			assertTrue(testServer.get(key).equals(value));
		} catch (KVException e) {
			// TODO Auto-generated catch block
			fail();
		}
		
		try {
			assertTrue(testServer.get(key1).equals(value1));
		} catch (KVException e) {
			// TODO Auto-generated catch block
			fail();
		}
		
		try {
			assertTrue(testServer.get(key2).equals(value2));
		} catch (KVException e) {
			// TODO Auto-generated catch block
			fail();
		}
		
	}

	@Test
	public void testDel() {
		KVServer testServer = new KVServer(256, 256*1024);
		
		KVStore dStore = testServer.getDataStore("givemedatastore");
		KVCache dCache = testServer.getDataCache("givemedatacache");
		
		String key = "CS162";
		String value = "nachos-project";
		
		String key1 = "CS184";
		String value1 = "raytrace";
		
		//Setup the test cases
		try {
			testServer.put(key, value);
		} catch (KVException e) {
			fail();
		}
		
		try {
			testServer.put(key1, value1);
		} catch (KVException e) {
			fail();
		}
		
		//Calling Delete Method
		try {
			testServer.del(key);
		} catch (KVException e) {
			fail();
		}
		
		try {
			testServer.del(key1);
		} catch (KVException e) {
			fail();
		}
		
		//Checking for no existence
		try {
			String valueInStore = dStore.get(key);
		} catch (KVException e1) {
			//This is good
		}
		
		String valueInCache = dCache.get(key);
		assertNull(valueInCache);
		
		try {
			String valueReturnedFromGet = testServer.get(key);
		} catch (KVException e) {
			assertTrue(e.getMsg().getMessage().equals("Does not exist"));
		}
		
		
		try {
			String valueInStore = dStore.get(key1);
		} catch (KVException e1) {
			//This is good
		}
		
		valueInCache = dCache.get(key1);
		assertNull(valueInCache);
		
		try {
			String valueReturnedFromGet = testServer.get(key1);
		} catch (KVException e) {
			assertTrue(e.getMsg().getMessage().equals("Does not exist"));
		}
	}
	
}
