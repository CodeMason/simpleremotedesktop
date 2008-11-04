package org.sample;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

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
	
	public RemoteThingsImpl(){
		
		try {
			r = new Robot();
		} catch (AWTException e) {
			System.err.println("You are stuck here about Robot");
			e.printStackTrace();
		}
	}
	
	public void remoteMouse(int x, int y) {
		
		r.mouseMove((1024/200)*x,((int)(768/200)*y));
		
		System.err.println((1024/200)*x + " " + (768/200)*y);
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
	
	// This method is forced to be dead!!!!!!!!!!!!!!!!!!!
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
	
		r.keyPress(ke.getKeyCode());
	
	}
	
	public void remoteKeyBoardsRelease(KeyEvent ke){
		
		r.keyRelease(ke.getKeyCode());
		
	}
}
