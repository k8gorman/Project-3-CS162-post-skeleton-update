package edu.berkeley.cs162;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

public class KVClientTest {
	String serverAddress = "localhost";
	int port = 8080;
	
	@Test 
	public void testPutNullKV(){
		String key = null;
		String value = null;
		
		KVClient client = new KVClient(serverAddress, port);
		try{
			client.put(key, value);
		} catch (KVException e){
			return;
		}
		
		//Should we reach here, this means the exception was never thrown
		fail("No Exception thrown");
		
	}

	@Test (expected = KVException.class)
	public void testPutNullKeyBlankValue() throws KVException{
		String key = null;
		String value = "";
		
		KVClient client = new KVClient(serverAddress, port);
		client.put(key, value);
		
	}
	
	@Test (expected = KVException.class)
	public void testPutEmptyKeyNullValue() throws KVException{
		String key = "";
		String value = null;
		
		KVClient client = new KVClient(serverAddress, port);
		client.put(key, value);
		
	}
	
	@Test
	public void testPutNormal() throws KVException{
		ArrayList<String> keys = new ArrayList<String>();
		ArrayList<String> values = new ArrayList<String>();
		for(int i = 0; i < 100; i++){
			keys.add("KEY"+i);
			values.add("VALUE"+i);
		}
		
		KVClient client = new KVClient(serverAddress, port);
		for(int i = 0; i < keys.size(); i++){
			client.put(keys.get(i), values.get(i));
		}
		for(int i = 0; i < keys.size(); i++){
			assertEquals(values.get(i), client.get(keys.get(i)));
		}
	}
	
	@Test (expected = KVException.class)
	public void testGet() throws KVException{
		String key = null;
		
		KVClient client = new KVClient(serverAddress, port);
		client.get(key);
	}
	
	@Test  (expected = KVException.class)
	public void testGetExceptionThrow() throws KVException{
		String key = "seven";
		
		KVClient client = new KVClient(serverAddress, port);
		client.get(key);
	}
	
	@Test  (expected = KVException.class)
	public void testDelExceptionThrow() throws KVException{
		
		KVClient client = new KVClient(serverAddress, port);
		String key = "THISSHOULDTHROWERROR";
		client.del(key);
	}
	
	
	@Test (expected = KVException.class)
	public void testDel2() throws KVException{
		
		KVClient client = new KVClient(serverAddress, port);
		String key = null;
		client.del(key);
	}
	
	
	
	
	
	


}
