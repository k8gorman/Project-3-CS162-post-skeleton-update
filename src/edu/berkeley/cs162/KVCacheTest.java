package edu.berkeley.cs162;

import static org.junit.Assert.*;

import org.junit.Test;

public class KVCacheTest {

	@Test
	public void testGet() {
		KVCache dCache = new KVCache(100, 10);
		
		String key = "key1";
		String key1 = samehash(key,10);
		String key2 = samehash(key,20);
		String key3 = samehash(key,30);
		String key4 = samehash(key,40);
		String key5 = samehash(key,5);
		String key6 = samehash(key,6);
		String key7 = samehash(key,7);
		String key8 = samehash(key,8);
		String key9 = samehash(key,7);
		String key10 = samehash(key,80);
		String key11 = samehash(key,70);
		String key12 = samehash(key,80);
		
		assertNull(dCache.get(key1));
		
		dCache.put(key1, "1");
		dCache.put(key2, "2");
		dCache.put(key3, "3");
		dCache.put(key4, "4");
		dCache.put(key5, "5");
		dCache.put(key6, "6");
		dCache.put(key7, "7");
		dCache.put(key8, "8");
		dCache.put(key9, "9");
		dCache.put(key10, "10");
		
		//This will set Key1's Usebit to true
		assertNotNull(dCache.get(key1));
		
		//This will evict key2
		dCache.put(key11, "11");

		assertNull(dCache.get(key2));
		
		//This will set key3 Use bit to true
		dCache.get(key3);
		dCache.put(key12, "12");
		
		assertNull(dCache.get(key4));
		
		
	}

	@Test
	public void testPut() {
		KVCache dCache = new KVCache(100, 10);
		
		String key = "key1";
		String key1 = "cs162";
		String key2 = "cs160";
		String key3 = "cs188";
		String key4 = "cs182";
		
		dCache.put(key, "12");
		dCache.put(key1, "12");
		dCache.put(key2, "12");
		dCache.put(key3, "12");
		dCache.put(key4, "12");
		
		assertEquals(dCache.get(key), "12");
		assertEquals(dCache.get(key1), "12");
		assertEquals(dCache.get(key2), "12");
		assertEquals(dCache.get(key3), "12");
		assertEquals(dCache.get(key4), "12");
	}

	@Test
	public void testDel() {
		KVCache dCache = new KVCache(100, 10);
		
		String key = "key1";
		String key1 = "cs162";
		String key2 = "cs160";
		String key3 = "cs188";
		String key4 = "cs182";
		
		dCache.put(key, "12");
		dCache.put(key1, "12");
		dCache.put(key2, "12");
		dCache.put(key3, "12");
		dCache.put(key4, "12");
		
		assertEquals(dCache.get(key), "12");
		dCache.del(key);
		assertNull(dCache.get(key));
		
		assertEquals(dCache.get(key1), "12");
		dCache.del(key1);
		assertNull(dCache.get(key1));
		dCache.del(key2);
		assertNull(dCache.get(key2));
		dCache.del(key3);
		assertNull(dCache.get(key3));
		dCache.del(key4);
		assertNull(dCache.get(key4));
	}

	@Test
	public void testGetWriteLock() {
		
	}

	public String samehash(String s, int level) {
	    if (s.length() < 2)
	        return s;
	    String sub2 = s.substring(0, 2);
	    char c0 = sub2.charAt(0);
	    char c1 = sub2.charAt(1);
	    c0 = (char) (c0 + level);
	    c1 = (char) (c1 - 31 * level);
	    String newsub2 = new String(new char[] { c0, c1 });
	    String re =  newsub2 + s.substring(2);
	    return re;
	}
}

