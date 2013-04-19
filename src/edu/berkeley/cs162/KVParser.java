package edu.berkeley.cs162;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class KVParser extends DefaultHandler {
	
	private KeyValue kv;
	private String temp;
	private ArrayList<KeyValue> kvlist = new ArrayList<KeyValue>();

	public KVParser() {
	}

	public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException {

		SAXParserFactory spfac = SAXParserFactory.newInstance();


              SAXParser sp = spfac.newSAXParser();


              KVParser handler = new KVParser();

              sp.parse("edu/berkeley/cs162/bank.xml", handler);
             
              handler.readList();

	}

	public void characters(char[] buffer, int start, int length) {
              temp = new String(buffer, start, length);
       }

	
	public void startElement(String uri, String localName,
                     String qName, Attributes attributes) throws SAXException {
              temp = "";
              if (qName.equalsIgnoreCase("KVPair")) {
                     kv = new KeyValue();

              }
       }


       public void endElement(String uri, String localName, String qName)
                     throws SAXException {

              if (qName.equalsIgnoreCase("KVPair")) {
                     // add it to the list
                     kvlist.add(kv);

              } else if (qName.equalsIgnoreCase("Key")) {
                     kv.setKey(temp);
              } else if (qName.equalsIgnoreCase("Value")) {
                     kv.setValue(temp);
              }

       }

	private void readList() {
              System.out.println("No of  the accounts in bank '" + kvlist.size()  + "'.");
              Iterator<KeyValue> it = kvlist.iterator();
              while (it.hasNext()) {
			KeyValue somekv = (KeyValue) it.next();
                     System.out.println("KVPAIR:  KEY:" + somekv.getKey() + "  VALUE:" + somekv.getValue());
              }
       }

	public ArrayList<KeyValue> kvs(String filename) throws IOException, SAXException, ParserConfigurationException {
		SAXParserFactory spfac = SAXParserFactory.newInstance();
		SAXParser sp = spfac.newSAXParser();
		sp.parse(filename, this);
		return kvlist;
		}



		
		

}