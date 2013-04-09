
/**
 * XML Parsing library for the key-value store
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


import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;




/**
 * This is the object that is used to generate messages the XML based messages 
 * for communication between clients and servers. 
 */
public class KVMessage {
private String msgType = null;
private String key = null;
private String value = null;
private String status = null;
private String message = null;
//array list used to check for valid type
ArrayList<String> msgTypes= new ArrayList<String>(Arrays.asList(
"putreq",
"getreq",
"delreq",
"resp"));





public final String getKey() {
return key;
}


public final void setKey(String key) {
this.key = key;
}


public final String getValue() {
return value;
}


public final void setValue(String value) {
this.value = value;
}


public final String getStatus() {
return status;
}


public final void setStatus(String status) {
this.status = status;
}


public final String getMessage() {
return message;
}


public final void setMessage(String message) {
this.message = message;
}


public String getMsgType() {
return msgType;
}


/* Solution from http://weblogs.java.net/blog/kohsuke/archive/2005/07/socket_xml_pitf.html */
private class NoCloseInputStream extends FilterInputStream {
    public NoCloseInputStream(InputStream in) {
        super(in);
    }
   
    public void close() {} // ignore close
}

/***
* 
* @param msgType
* @throws KVException of type "resp" with message "Message format incorrect" if msgType is unknown
*/
public KVMessage(String msgType) throws KVException {
    if (msgTypes.contains(msgType)){
    this.msgType = msgType;
    }else{
    throw new KVException (new KVMessage("resp", "Message format incorrect"));
    }
}

public KVMessage(String msgType, String message) throws KVException {
        //call the KVMessage(String msgType) constructor
this(msgType);
this.message=message;
}

/***
     * Parse KVMessage from incoming network connection
     * @param sock
     * @throws KVException if there is an error in parsing the message. The exception should be of type "resp and message should be :
     * a. "XML Error: Received unparseable message" - if the received message is not valid XML.
     * b. "Network Error: Could not receive data" - if there is a network error causing an incomplete parsing of the message.
     * c. "Message format incorrect" - if there message does not conform to the required specifications. Examples include incorrect message type. 
     */
public KVMessage(InputStream input) throws KVException {
   
Document newDoc= null; 
DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    DocumentBuilder db;
    //CREATE A NEW DOCUMENT BUILDER
    try {
    db = dbf.newDocumentBuilder();
    }catch(ParserConfigurationException e){
    KVMessage errorMsg = new KVMessage("resp", "Unknown Error: DocumentBuilder");;
    throw new KVException (errorMsg);
    }
     
    //TRY TO PARSE THE INPUT STREAM
    try{
    newDoc = db.parse(new NoCloseInputStream(input));
    newDoc.setXmlStandalone(true);
    }catch(IOException e){
    KVMessage ioError = new KVMessage( "resp" , "Network Error: Could not receive data");
     
    } catch (SAXException e) {
// TODO Auto-generated catch block
KVMessage saxError = new KVMessage ("resp", "XML Error: Received unparseable message");

}
     
    //NORMALIZE THE UNDERLYING DOM TREE
    // used this website as a resource: http://sanjaal.com/java/tag/getdocumentelementnormalize/
    newDoc.getDocumentElement().normalize();
     
     
    //MAKE A NODE LIST
    NodeList listOfMessageTypes = newDoc.getElementsByTagName("KVMessage");
    //MAKE A NODE
    Node nodeType = listOfMessageTypes.item(0);
    Element elementType = (Element) nodeType;
   
     
    //expecting status to be "True" or "False"
    status= findTagsOfElement (elementType, "Status");
    //GET THE MSGTYPE FROM THE ELEMENT
    msgType = elementType.getAttribute("type");
    key = findTagsOfElement(elementType, "Key");
    value = findTagsOfElement(elementType, "Value");
    message = findTagsOfElement(elementType, "Message");
    //TODO ERROR CHECKING?
}

/*
* Returns the tag Value of an element
* 
*/
public String findTagsOfElement (Element thisElm, String tag){
NodeList thisList = thisElm.getElementsByTagName(tag);
if (thisList.getLength() == 0){
return null;

}
else{
//get the children of the first node
NodeList node1ChildList = thisList.item(0).getChildNodes();
Node firstChild = (Node) node1ChildList.item(0);
return firstChild.getNodeValue();
}
}

/**
* Generate the XML representation for this message.
* @return the XML String
* @throws KVException if not enough data is available to generate a valid KV XML message
*/
public String toXML() throws KVException {
String xmlString;
Text text;
     
DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
DocumentBuilder db = null;
Document newDoc = null; 
//try to make a new Doc builder which is the XML base
try{
db = dbf.newDocumentBuilder();
newDoc = db.newDocument();
newDoc.setXmlStandalone(true);
}catch (ParserConfigurationException e){
KVMessage dbMessage = new KVMessage("Unknown Error: toXML Document Building error");
throw new KVException(dbMessage);
}

//now to make the XML Tree! 
//MAKE THE ROOT ELEMENT the KVMessage
Element rootElement = newDoc.createElement("KVMessage");
//ADD THE ROOT TO THE XML
//SET THE TYPE
rootElement.setAttribute("type", msgType);
newDoc.appendChild(rootElement);


//HERE COMES THE FUN
//DO THE MESSAGE BUSINESS
if (!msgTypes.contains(msgType)){
throw new KVException (new KVMessage("resp", "XML Error: Received unparseable message"));
}
if (msgType == "putreq"){
if (key !=null && value !=null){
Element keyChild = newDoc.createElement("Key");
keyChild.setTextContent(key);
rootElement.appendChild(keyChild);

Element valueChild = newDoc.createElement("Value");
valueChild.setTextContent(value);
rootElement.appendChild(valueChild);
}else{
throw new KVException (new KVMessage("resp", "XML Error: Received unparseable message"));
}
}

if (msgType == "resp"){
//if resp has K&V
if (key !=null && value != null){
Element keyChild = newDoc.createElement("Key");
keyChild.setTextContent(key);
rootElement.appendChild(keyChild);

Element valueChild = newDoc.createElement("Value");
valueChild.setTextContent(value);
rootElement.appendChild(valueChild);
}
//if resp has no K&V
else if (key == null && value ==null){
Element messageChild = newDoc.createElement("Message");
messageChild.setTextContent(message);
rootElement.appendChild(messageChild);
}else{
throw new KVException (new KVMessage ("resp","XML Error: Received unparseable message" ));
}
}
if (msgType == "getreq" ){
if (key !=null){
Element keyChild = newDoc.createElement("Key");
keyChild.setTextContent(key);
rootElement.appendChild(keyChild);
}else{
throw new KVException (new KVMessage("resp", "XML Error: Received unparseable message"));
}
}

if (msgType == "delreq"){
if (key !=null){
Element keyChild = newDoc.createElement("Key");
keyChild.setTextContent(key);
rootElement.appendChild(keyChild);

}
}



//XML Tree done
// now need to transform this to output it
TransformerFactory tf = TransformerFactory.newInstance();
Transformer arnold = null;

//try to make a new transformer
try{
arnold = tf.newTransformer();
}catch (TransformerConfigurationException e){
KVMessage transformerError = new KVMessage("make new transformer error: "+ e.getMessage());
throw new KVException(transformerError);
}



// now take the string from the xml
StringWriter stringwriter = new StringWriter();
StreamResult dst = new StreamResult(stringwriter);

DOMSource src = new DOMSource(newDoc);
//try to tranfsorm xml src to dst
try{
arnold.transform(src, dst);
}catch (TransformerException e){
KVMessage transError = new KVMessage ("error converting xml src to dst" + e.getMessage());
throw new KVException(transError);
}
//get the string from the string writer
return stringwriter.toString();

}//ends method

public void sendMessage(Socket sock) throws KVException {
      // TODO: implement me
String xml = this.toXML();
Writer output;
try{
output = new OutputStreamWriter(sock.getOutputStream());
output.write(xml);
output.flush();
}catch(IOException e){
//KVMessage
KVMessage ioError = new KVMessage("error in sendMessage "+ e.getMessage());
throw new KVException (ioError);
}

try {
sock.shutdownOutput();
} catch (IOException e) {
// TODO Auto-generated catch block
KVMessage socketError = new KVMessage("socket closure error "+ e.getMessage());
throw new KVException(socketError);

}
}
}