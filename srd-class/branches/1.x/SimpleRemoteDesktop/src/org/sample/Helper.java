/* I modified this code which is from 
 * 
 * Modifying /Editing XML Document in JAVA
 * http://www.hiteshagrawal.com/java/modifying-editing-xml-document-in-java
 * 
 */

package org.sample;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.File;
import java.io.OutputStream;
import java.io.StringWriter;
import org.w3c.dom.*;

import javax.swing.SwingUtilities;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.xml.sax.SAXException;

/*
 * This file is for parsing XML and being able to modify it
 */

public class Helper{
	
	public static String ServerAddress = null;
	public static boolean bb = false;
	
	/*public static void main(String[] args) throws IOException{
		
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				new InputForm();
			}
		});
	*/
		/*
		 * I see what's happening here.
		 * Since JFrame is on a separate thread, so before it gets the value
		 * from JTextField, modApplicationContext() starts to run.
		 * As a result, the method assumes that the text is null.
		 * 
		 */
		
		//modApplicationContext();
	//}
	
	public static void fileCheck() throws IOException{
		
		File f = new File("/Users/seiyakawashima/tomcat5-5/webapps/ROOT/");
		
		//I know this is a directory
		if(f.isDirectory()){
			System.err.println("The File object is a directory");
		}else
			System.err.println("The File object is a file");
		
		File[] fs = f.listFiles();
		File f2 = null;
		
		System.out.println("----------");
		
		String name = "JnlpRemoteServer.jnlp";
		String path = null;
		
		//This guy returns everything in the directory
		for(int i = 0; i < fs.length;i++){
			if(name.equals(fs[i].getName())){
				System.err.println("true: " + fs[i].getName());
				System.err.println("The file path is " + fs[i].getCanonicalPath());
				
				f2 = new File(fs[i].getCanonicalPath());
				//f2 = new File(fs[i].getAbsolutePath()+fs[i].getName());
			}	
			
		}
		
		//I want to get the last access time for the file and 
		// IP address	
		//System.err.println(f2.isFile() ? "It's a file" : "It's a directory");	
		
		System.err.println("It's last modified is : "+ f2.lastModified());
		
	}
	
	public static boolean modApplicationContext(){
	/*public static void main(String[] args){
		
		if(args.length != 1){
			System.err.println("Need one parameter");
			System.exit(0);
		}	
		*/
		try {	
		//File file = new File("/Users/seiyakawashima/Desktop/Client.xml");
			File file = new File("./conf/SpringRMIClient.xml");
			
		//Create instance of DocumentBuilderFactory
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	 
		//Get the DocumentBuilder
		DocumentBuilder docBuilder = factory.newDocumentBuilder();
	 
		//Using existing XML Document
		Document doc = docBuilder.parse(file);
	
		NodeList beanList = doc.getElementsByTagName("bean");
		
		Element bean = (Element)beanList.item(0);
		NodeList propList = bean.getElementsByTagName("property");
		Element prop = (Element)propList.item(0);
		NodeList valueList = prop.getElementsByTagName("value");
		Element value = (Element)valueList.item(0);
			
		System.err.println(getCharacterDataFromElement(value));	
		
		System.err.println("In there :" + ServerAddress);
		
		//this guy makes this method for main()
		value.setTextContent("rmi://" + ServerAddress + ":9000/RMISpring");
		System.err.println("You got: " + value.getTextContent());
		
		//set up a transformer
		TransformerFactory transfac = TransformerFactory.newInstance();
		Transformer trans = transfac.newTransformer();
	 
	    //create string from xml tree
	    StringWriter sw = new StringWriter();
	    StreamResult result = new StreamResult(sw);
	    DOMSource source = new DOMSource(doc);
	    trans.transform(source, result);
	    String xmlString = sw.toString();
	 
	    OutputStream f0;
		byte buf[] = xmlString.getBytes();
		//f0 = new FileOutputStream("/Users/seiyakawashima/Desktop/Client.xml");
		f0 = new FileOutputStream("./conf/SpringRMIClient.xml");	
		for(int i=0;i<buf .length;i++) {
		   f0.write(buf[i]);
		}
		f0.close();
		buf = null;
		
		bb = true;
	    
		}
	     catch(SAXException e) {
		e.printStackTrace();		
	     }
	     catch(IOException e) {
	        e.printStackTrace();		
	     }
	     catch(ParserConfigurationException e) {
	       e.printStackTrace();		
	     }
	     catch(TransformerConfigurationException e) {
	       e.printStackTrace();		
	     }
	     catch(TransformerException e) {
	       e.printStackTrace();		
	     }
	     
	     return bb;
	}
	
	public static String getCharacterDataFromElement(Element e) {
	  Node child = e.getFirstChild();
	  
	  if (child instanceof CharacterData) {
	    CharacterData cd = (CharacterData) child;
	      return cd.getData();
	  }
	  return "?";
	}
}
