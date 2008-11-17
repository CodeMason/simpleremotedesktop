                                                                     
                                                                     
                                                                     
                                             
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
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.UnknownHostException;

import org.common.RemoteThings;

/* This is the implementation for the remote interface called "RemoteThings.java".
 * The implementation uses Robot class quite well to move the mouse, type characters,
 * wheel the mouse and DnD remotely. It means that this is an implementation of Robot class.
 *
 * Once again, I use Spring RMI, so don't need to use 
 * UnicastRemoteobject here.
 */

public class RemoteThingsImpl //extends UnicastRemoteObject 
						implements RemoteThings{

	int x = 0,y = 0;
	Robot r = null;
	Rectangle rect2;
	
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
		
		System.err.println("rec double X: " + resX + " " + "rec double Y: " + resY);
		
		r.mouseMove((int)resX, (int)resY);
		
		System.err.println("rec X: " + (int)resX + "rect Y: " + (int)resY);
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
		
		//System.out.println("You accepted : " + b);
		System.out.println("You accepted : "  + name);
		System.out.println("The total amount is " + b.length);
		
		FileOutputStream fos = null;
		//FileInputStream fis = null;
		//BufferedWriter br = null;
		
		try {
			//fis = new FileInputStream(new File("C:\\" + name));
			if((System.getProperty("os.name")).equals("Windows 2000") ||
					(System.getProperty("os.name")).equals("Windows XP") ||
						(System.getProperty("os.name")).equals("Windows Vista")){
			
				fos = new FileOutputStream(new File(System.getProperty("user.home")+"\\Desktop\\" + name));
				//br = new BufferedWriter(new FileWriter(System.getProperty("user.home") + "\\Desktop\\" + name));
				
				System.err.println("Now here " + fos.toString());
			}else if((System.getProperty("os.name")).equals("Mac OS X"))
				fos = new FileOutputStream(new File(System.getProperty("user.home")+"/Desktop/" + name));
			
		} catch (FileNotFoundException e) {
			System.err.println("Error about making FileOutputStream ");
			e.printStackTrace();
		}catch(Exception ee){
			System.err.println("Error about creating BufferedWriter");
		}
		
		//String aa = null;
		
		
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

	public void remoteClipboardCopy() {
		
			Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
			DataFlavor[] df = clip.getAvailableDataFlavors();
			int count = 0;
			String str = null;
			
			for(DataFlavor d: df){
				try {
					
					if(clip.isDataFlavorAvailable(d)){
						count++;
						//str = clip.getData(d).toString();
						System.out.println(clip.getData(d));
						System.out.println(d);
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
		}
		
		Socket fileSock = null;
		PrintWriter pw = null;
		BufferedReader in = null,br = null;
		
			try {//The IP address must be the client address
				fileSock = new Socket("192.168.0.101",7777);
				pw = new PrintWriter(fileSock.getOutputStream(),true);
				in = new BufferedReader(new InputStreamReader(fileSock.getInputStream()));
				br = new BufferedReader(new FileReader(str));
				String s;
				while((s = br.readLine()) != null){
					pw.println(s);
					System.out.println("Context :" + in.readLine());
					//to see what's in the file
				}
			} catch (UnknownHostException e) {
				System.err.println("Unknow host");
			} catch (IOException e) {
				System.err.println("IO exception for socket");
			}
			
			try{
			pw.close();
			br.close();
			in.close();
			fileSock.close();
			}catch(IOException e){
				System.err.println("IO error in close() method");
			}
	}
}

