package org.sample;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import org.common.RemoteThings;

/* This is the implementation for the remote interface called "RemoteThings.java".
 * The implementation uses Robot class quite well to move the mouse, type characters,
 * wheel the mouse and DnD remotely. It means that this is an implementation of Robot class.
 * Once again, I use Spring RMI, so don't need to use 
 * UnicastRemoteobject here.
 */

public class RemoteThingsImpl //extends UnicastRemoteObject						
							implements RemoteThings{
	int x = 0,y = 0;
	Robot r = null;
	Rectangle rect2;
	
	public static String yourAddr;
	
	private String fileName = null;

	public RemoteThingsImpl(){
		try {
			r = new Robot();
		} catch (AWTException e) {
			System.err.println("You are stuck here about Robot");
			e.printStackTrace();
		}
	}

	public void remoteMouse(int x, int y) {

		double tempX = (double)x;
		double tempY = (double)y;
		double resX = (1024/rect2.getWidth()) * tempX;
		double resY = (768/rect2.getHeight()) * tempY;

		//r.mouseMove((int)(1024/(rect2.getWidth()))*x,((int)(768/(rect2.getHeight()))*y));

		//System.err.println("rec double X: " + resX + " " + "rec double Y: " + resY);
		r.mouseMove((int)resX, (int)resY);
		//System.err.println("rec X: " + (int)resX + "rect Y: " + (int)resY);
	}

	public void remoteMousePress(int buttons){
		r.mousePress(buttons);
		System.err.println(buttons + " " + "is pressed.");
	}

	public void remoteMouseRelease(int buttons){
		r.mouseRelease(buttons);
		System.err.println(buttons + " " + "is released.");
	}

	public void remoteMouseWheel(int amount){
		r.mouseWheel(amount);
	}

	//It's slow but it works fine
	public void remoteDragAndDrop(String name, byte[] b){
		System.out.println("You accepted : "  + name);
		System.out.println("The total amount is " + b.length);
		FileOutputStream fos = null;
		
		try {
			if((System.getProperty("os.name")).equals("Windows 2000") ||
					(System.getProperty("os.name")).equals("Windows XP") ||
						(System.getProperty("os.name")).equals("Windows Vista")){
				fos = new FileOutputStream(new File(System.getProperty("user.home")+"\\Desktop\\" + name));

				System.err.println("Now here " + fos.toString());
			}else if((System.getProperty("os.name")).equals("Mac OS X"))
				fos = new FileOutputStream(new File(System.getProperty("user.home")+"/Desktop/" + name));
		} catch (FileNotFoundException e) {
			System.err.println("Error about making FileOutputStream ");
			e.printStackTrace();
		}catch(Exception ee){
			System.err.println("Error about creating BufferedWriter");
		}

		try {
				//for(int i = 0; i < b.length;i++){
					fos.write(b,0,b.length);
				//}
				fos.close();
		} catch (IOException e) {
			System.err.println("Error about writing a file");
			e.printStackTrace();
		}
		//System.out.println(aa);
		for(int i =0; i < b.length;i++){
			System.out.print((char)b[i]);
			if(i == b.length-1)
				System.out.println();
		}	

		System.out.println("Successfuly making file!!!");
	}

	public void remoteKeyBoardsPress(KeyEvent ke){
		try{
			r.keyPress(ke.getKeyCode());
		}catch(IllegalArgumentException e){

		}
	}

	public void remoteKeyBoardsRelease(KeyEvent ke){
		r.keyRelease(ke.getKeyCode());
	}
	
	public Rectangle remoteGetScreenSize(Rectangle rect){
		 rect2 = rect;
	 	return rect2;
	 }
	
	public void remoteMouseDrag(int x,int y) {
		System.out.println("Dragging ...");
		remoteMouse(x,y);
		System.out.println(x + " " + y);
	}
	
	public String remoteClipboardCopy() {
		Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
		DataFlavor[] df = clip.getAvailableDataFlavors();
		int count = 0;
		String str = null;
		
		for(DataFlavor d: df){
			try {
				if(clip.isDataFlavorAvailable(d)){
					count++;
					//str = clip.getData(d).toString();
					System.out.println("You got : " + clip.getData(d));
					System.out.println(d);
					str = clip.getData(d).toString();
					break;
				}

			}catch (UnsupportedFlavorException e) {
					System.err.println("Unsupported Flavor issue");
				}catch(MalformedURLException e){
					//do nothing
				}catch(IOException e){
					System.err.println("IO Issue");
				}
			}
		
		System.err.println("You are :" + str);
		
		//String[] st = str.split(File.separator);
		//System.out.println(st);
		
		//System.out.println("This is  : " + st[str.length()-1]);
		
		//return st[str.length()-1];
		//Socket clientSock = null;
		//BufferedReader br = null;
		//PrintWriter pw = null;
		
		//The first parameter is now the target.
		//remoteSetUpClient("Hello World", clientSock,br, pw,"192.168.0.101",11111);
		return str;
	}

	public void remoteClipboardPaste() {
		//here, I should make a socket stream.
		Transferable trans = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
		String str = null;

		if(trans != null && trans.isDataFlavorSupported(DataFlavor.javaFileListFlavor)){
			try {
				str = trans.getTransferData(DataFlavor.javaFileListFlavor).toString();
				//System.err.println(trans.getTransferData(DataFlavor.javaFileListFlavor).toString());
				System.err.println("Object is : " + str);
			} catch (UnsupportedFlavorException e) {
				System.err.println("Unsupported");
			} catch (IOException e) {
				System.err.println("IO Error");
			}
		}else
			System.err.println("Clipboard might have some error");

		//Duplication occurred here
		StringBuilder strBuilder = new StringBuilder(str);

		strBuilder.deleteCharAt(0);
		String str2 = strBuilder.substring(0, str.length()-2);

		//System.out.println("str2 : " + str2);
		
		Socket clientSock = null;
		BufferedReader br = null;
		PrintWriter pw = null;
		
		remoteSetUpClient(str2, clientSock,br, pw,yourAddr,11111);
		
		/*try{
			System.out.println("Setting up the client socket");
			clientSock = new Socket(InetAddress.getByName("192.168.0.101"),11111);
		}catch(IOException e){
			e.printStackTrace();
		}

		try{
			//read the socket stream to get the contents of a file.
			pw = new PrintWriter(clientSock.getOutputStream(),true);
			//this doesn't work
			br = new BufferedReader(new FileReader(new File(str2)));
			
			String ss;
			int count = 0;

			while((ss = br.readLine()) != null){
				System.out.println(count++);
				pw.write(ss);
			}
		
			System.out.println("Done with writing output stream");
		}catch(UnknownHostException e){
			System.err.println("Unknown host error");
		}catch(IOException e){
			e.getStackTrace();
		}*/

		try{
			br.close();
			pw.close();
			clientSock.close();
		}catch(IOException e){
			System.err.println("Error here in the client socket");
		}
	}

	public String[] analyzeString(String str){
			String[] st = str.split("\\");
			return st;
	}

	public void remoteSetUpClient(String target, Socket clientSock,
			BufferedReader br, PrintWriter pw, String dest,int port) {
		
		try{
			System.out.println("Setting up the client socket");
			clientSock = new Socket(InetAddress.getByName(dest),port);
		}catch(IOException e){
			e.printStackTrace();
		}

		try{
			//Read a filename as a String
			File f = new File(target);
			if(!f.isFile()){
				//get a file name
				br = new BufferedReader(new StringReader(target));
				String ss;
				int count = 0;

				while((ss = br.readLine()) != null){
					System.out.println(count++);
					pw.write(ss);
				}
		
				System.out.println("Done with writing output stream");
			
			}else if(f.isFile()){
				//read the socket stream to get the contents of a file.
				pw = new PrintWriter(clientSock.getOutputStream(),true);
				br = new BufferedReader(new FileReader(f));
			
				String ss;
				int count = 0;

				while((ss = br.readLine()) != null){
					System.out.println(count++);
					pw.write(ss);
				}
		
				System.out.println("Done with writing output stream");
			}
		}
		catch(UnknownHostException e){
			System.err.println("Unknown host error");
		}catch(IOException e){
			e.getStackTrace();
		}
		
		try{
			br.close();
			pw.close();
			clientSock.close();
		}catch(IOException e){
			System.err.println("Error here in the client socket");
		}	
	}
}