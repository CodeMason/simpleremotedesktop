package org.sample;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.SwingUtilities;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import org.common.RemoteThings;

public class Main implements Runnable{

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
		try {
			RemoteThingsImpl.yourAddr = (InetAddress.getLocalHost()).getHostAddress().toString();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		runInputForm();
		(new Thread(new Main())).start();
	}
	
	private static void runInputForm(){
		
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				new InputForm();
			}
		});
	}
		
	public static void runApplicationContext(){
		ApplicationContext context = 
			new FileSystemXmlApplicationContext("./conf/SpringRMIClient.xml");
	
		Main.context = context;
	}

	public void run() {
		//this is for getting file name
		//Helper.setUpServer(11111);
		//this is for file transfer
		//Helper.setUpServer(11111);
System.out.println("Starting to set up the server ");
		
		ServerSocket sSocket = null;
		Socket fileSock = null;
		BufferedReader in = null;
		BufferedWriter out = null;
		
		try{
			sSocket = new ServerSocket(11111);
			System.out.println("Done with listening on the port ");
		}catch(IOException e){
			System.err.println("Can't listen on the port ");
		}
		
		while(true){
		
		try {//The IP address must be the client address
				System.out.println("Waiting ... ");
				fileSock = sSocket.accept();
				System.out.println("Got a client!! ");
				
				in = new BufferedReader(new InputStreamReader(fileSock.getInputStream()));
				System.out.println(Helper.setPath(Players.fileName));
				out = new BufferedWriter(new FileWriter(Helper.setPath(Players.fileName)));
				
				String s;
				while((s = in.readLine()) != null){
					System.out.println("Context :" + s);
					out.write(s);
					//to see what's in the file
				}
			}catch(NullPointerException e){
				System.err.println("Check out your io settings");
			} catch (UnknownHostException e) {
				System.err.println("Unknow host");
			} catch (IOException e) {
				System.err.println("Accept() failed");
			}
			
			try{
				in.close();
				out.close();
				fileSock.close();
				//sSocket.close();
			}catch(IOException e){
				System.err.println("Error here");
			}	
			System.out.println("Done with setting up the server ");
		}
	}
}
