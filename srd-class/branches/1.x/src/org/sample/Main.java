package org.sample;

import javax.swing.SwingUtilities;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import org.common.RemoteThings;

public class Main{

	public static String Addr;
	public static String Port;
	protected static RemoteThings rmiSpringService; 
	//This variable, rmiSpringService is supposed to be private
	// as a Java Bean and getter/setter meaning.
	
	public void setRmiSpringService(RemoteThings rmiSpringService){
		this.rmiSpringService = rmiSpringService;
	}
	
	public static void main(String[] args){
	
		if(args.length != 2){
			System.out.println("Usage: java Main Addr Port");
			System.exit(0);
		}
		
		Addr = args[0];
		Port = args[1];
		
		ApplicationContext context = 
			new FileSystemXmlApplicationContext("./conf/SpringRMIClient.xml");
	
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				new Players();
			}
		});
	}
}
