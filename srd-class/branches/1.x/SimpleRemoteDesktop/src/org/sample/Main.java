package org.sample;

import javax.swing.SwingUtilities;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import org.common.RemoteThings;

public class Main{

	public static ApplicationContext context;
	public static boolean b = false;
	private static InputForm input;
	public static String Addr;
	public static String Port = "9999";
	protected static RemoteThings rmiSpringService; 
	//This variable, rmiSpringService is supposed to be private
	// as a Java Bean and getter/setter meaning.
	
	public void setRmiSpringService(RemoteThings rmiSpringService){
		this.rmiSpringService = rmiSpringService;
	}
	
	public static void main(String[] args){

		runInputForm();
		
		
		
		//runApplicationContext();
	}
	
	private static void runInputForm(){
		
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				new InputForm();
				//new Players();
			}
		});
	}
		
	public static void runApplicationContext(){
		ApplicationContext context = 
			new FileSystemXmlApplicationContext("./conf/SpringRMIClient.xml");
	
		Main.context = context;
	}
}
