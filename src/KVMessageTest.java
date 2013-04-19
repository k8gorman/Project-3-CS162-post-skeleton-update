
import static org.junit.Assert.*;


import java.io.ByteArrayInputStream;
import java.io.InputStream;


import org.junit.Test;

import edu.berkeley.cs162.KVException;
import edu.berkeley.cs162.KVMessage;












public class KVMessageTest {


	@Test
	public void test() {
		
	}
	/*
	 * test a stream input with a normal key to make sure parsing is correct
	 */
	@Test
	public void testinput1() throws KVException {
		String str = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><KVMessage type=\"putreq\"><Key>kittykat</Key></KVMessage>";
		InputStream is = new ByteArrayInputStream(str.getBytes());
		KVMessage inputtest = new KVMessage(is);
		assertTrue(inputtest.getKey().equals("kittykat"));
		assertTrue(inputtest.isClosed() == true);
		System.out.println("closed: " + inputtest.isClosed());
		System.out.println(inputtest.getKey());
	}
	/*
	 * test a bad message type to make sure the test is correct for this
	 */
	@Test
	public void testinput2() throws KVException {
		try{
			String str = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><KVMessage type=\"meoww\"><Key>boba</Key></KVMessage>";
			InputStream is = new ByteArrayInputStream(str.getBytes());
			KVMessage inputtest2 = new KVMessage(is);
			
		}
		catch (KVException e){
			assertTrue(e.getMsg().getMsgType().equals("resp"));
			assertTrue(e.getMsg().getMessage().equals("XML Error: Received unparseable message"));
			
		}


	}
	/*
	 * test a normal key value input is parsed correctly
	 */
	@Test
	public void testinput3() throws KVException {


		String str = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><KVMessage type=\"putreq\"><Key>meow</Key><Value>mix</Value></KVMessage>";
		InputStream is = new ByteArrayInputStream(str.getBytes());
		KVMessage inputtest3 = new KVMessage(is);
		assertTrue(inputtest3.getKey().equals("meow"));
		assertTrue(inputtest3.getValue().equals("mix"));
		assertTrue(inputtest3.getMsgType().equals("putreq"));
	}
	/*
	 * Even if a get request gets a value input, it shouldn't store it. Testing that.
	 * 
	 */
	@Test
	public void testinput4() throws KVException {
		String str = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><KVMessage type=\"getreq\"><Key>gold</Key><Value>watch</Value></KVMessage>";
		InputStream is = new ByteArrayInputStream(str.getBytes());
		KVMessage inputtest4 = new KVMessage(is);
		assertTrue(inputtest4.getKey().equals("gold"));
		assertTrue(inputtest4.getValue() == null);
		assertTrue(inputtest4.getMsgType().equals("getreq"));
	}

	/*
	 * Test whether key and no value works properly
	 */
	@Test
	public void getTester1() throws KVException {

		KVMessage getTest1Msg = new KVMessage("getreq");
		getTest1Msg.setKey("kate");
		//no value set
		if (getTest1Msg == null){
			System.out.println("gettest msg 1 is null");
		}
		System.out.println(getTest1Msg.toXML());


	}
	/*
	 * Test a get request with no key 
	 */
	@Test
	public void getTester2() throws KVException{
		try{
			KVMessage getTest2Msg = new KVMessage("getreq");
			System.out.println(" getTest2Msg XML: " + getTest2Msg.toXML());
		}catch (KVException e){
			assertTrue(e.getMsg().getMsgType().equals("resp"));
			assertTrue(e.getMsg().getMessage().equals("XML Error: Received unparseable message"));

		}
	}

	/*
	 * Test a standard Key and Value get
	 */
	@Test
	public void getTester3() throws KVException{
		KVMessage getTesterMsg3 = new KVMessage("getreq");
		getTesterMsg3.setKey("Katherine");
		getTesterMsg3.setValue("Gorman");
		System.out.println("Katherine Gorman get Test: " + getTesterMsg3.toXML());
	}

	/*
	 * Test a put, with key and value
	 */
	@Test
	public void putTester1() throws KVException{
		KVMessage putTest1 = new KVMessage("putreq");
		putTest1.setKey("Michael");
		putTest1.setValue("Jackson");
		System.out.println("Michael Jackson put test: " +putTest1.toXML());
	}

	@Test
	public void respTester() throws KVException{
		String input = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><KVMessage type=\"resp\"><Key>peanut butter</Key><Value>jelly</Value></KVMessage>";
		InputStream stream = new ByteArrayInputStream(input.getBytes());
		KVMessage respTest = new KVMessage(stream);
		assertTrue(respTest.getKey().equals("peanut butter"));
		assertTrue(respTest.getValue().equals("jelly"));

	}


}