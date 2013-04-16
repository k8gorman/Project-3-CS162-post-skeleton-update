/**
 * Handle client connections over a socket interface
 * 
 * @author Mosharaf Chowdhury (http://www.mosharaf.com)
 * @author Prashanth Mohan (http://www.cs.berkeley.edu/~prmohan)
 * 
 * Copyright (c) 2012, University of California at Berkeley
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *  * Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *  * Neither the name of University of California, Berkeley nor the
 *    names of its contributors may be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *    
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY
 *  DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package edu.berkeley.cs162;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

import edu.berkeley.cs162.NetworkHandler;
import java.net.Socket;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
/**
 * This NetworkHandler will asynchronously handle the socket connections. 
 * It uses a threadpool to ensure that none of it's methods are blocking.
 *
 */
public class KVClientHandler implements NetworkHandler {
	private KVServer kv_Server = null;
	private ThreadPool threadpool = null;
	
	public KVClientHandler(KVServer kvServer) {
		initialize(kvServer, 1);
	}

	public KVClientHandler(KVServer kvServer, int connections) {
		initialize(kvServer, connections);
	}

	private void initialize(KVServer kvServer, int connections) {
		this.kv_Server = kvServer;
		threadpool = new ThreadPool(connections);	
	}
	

	private class ClientHandler implements Runnable {
		private KVServer kvServer = null;
		private Socket client = null;
		
		@Override
		public void run() {
		     // TODO: Implement Me!
			InputStream input = null;
			
			//Get input stream
			try {
				input = client.getInputStream();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//Setup the array's size
			byte byteArray[] = null;
			try {
				byteArray = new byte[input.available()];
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//Read Bytes from stream into Arrray
			try {
				input.read(byteArray);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//Convert ByteArray to String
			String parsedString = null;
			try {
				parsedString = new String(byteArray, "UTF8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//Create XML
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
		    DocumentBuilder builder; 
		    Document document = null;
		    try{
		        builder = factory.newDocumentBuilder();  
		        document = builder.parse( new InputSource( new StringReader( parsedString ) ) );  
		    } catch (Exception e) {  
		        e.printStackTrace();  
		    } 
		    
		    String key = document.getElementById("key").getTextContent();
		    String value = document.getElementById("value").getTextContent();
		    String msgType = document.getElementById("msgType").getTextContent();
		    
		    //Check if mesgType is valid
		    KVMessage clientMessage = null;
		    try{
		    	clientMessage = new KVMessage(msgType);
		    } catch (KVException e){
		    	//Annoying try catch
		    	//If XML is invalid, we must send a fail response
		    	KVMessage failXMLResponse = null;
		    	try {
					failXMLResponse = new KVMessage("resp", "XML Error: Received unparseable message");
				} catch (KVException e1) {
					//Why would this fail
				}
		    	
		    	try {
					failXMLResponse.sendMessage(client);
				} catch (KVException e1) {
					//Don't really care
				}
		    	return;
		    }
		    
		    //Finally if everything passes, we process the requests
		    /* ************* Put Request ******************* */
		    if(clientMessage.getMsgType() == "putreq"){
		    	try {
					kv_Server.put(key, value);
				} catch (KVException e) {
					//Send fail response
					try {
						e.getMsg().sendMessage(client);
					} catch (KVException e1) {
						//Do nothing
					}
				}
		    	//Send success response
		    	KVMessage msg = null;
				try {
					msg = new KVMessage("resp","Success");
				} catch (KVException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		    	try {
					msg.sendMessage(client);
				} catch (KVException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    /* ************* Get Request ******************* */
		    } else if(clientMessage.getMsgType() == "getreq"){
		    	String valueReturned = null;
		    	
		    	try {
					valueReturned = kv_Server.get(key);
				} catch (KVException e) {
					try {
						e.getMsg().sendMessage(client);
					} catch (KVException e1) {
						//Do nothing if send fails
					}
				}
		    	//Send success response
		    	KVMessage msg = null;
				try {
					msg = new KVMessage("resp","Success");
				} catch (KVException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		    	msg.setKey(key);
		    	msg.setValue(valueReturned);
		    	try {
					msg.sendMessage(client);
				} catch (KVException e) {
					
				}
		    /* ************* Del Request ******************* */
		    } else if(clientMessage.getMsgType() == "delreq"){
		    	try {
					kv_Server.del(key);
				} catch (KVException e) {
					try {
						e.getMsg().sendMessage(client);
					} catch (KVException e1) {
						
					}
				}
		    	
		    	KVMessage msg = null;
				try {
					msg = new KVMessage("resp","Success");
				} catch (KVException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		    	try {
					msg.sendMessage(client);
				} catch (KVException e) {
					
				}
		    }
		}
		
		public ClientHandler(KVServer kvServer, Socket client) {
			this.kvServer = kvServer;
			this.client = client;
		}
	}
	
	/* (non-Javadoc)
	 * @see edu.berkeley.cs162.NetworkHandler#handle(java.net.Socket)
	 */
	@Override
	public void handle(Socket client) throws IOException {
		Runnable r = new ClientHandler(kv_Server, client);
		try {
			threadpool.addToQueue(r);
		} catch (InterruptedException e) {
			// Ignore this error
			return;
		}
	}
}
