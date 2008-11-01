package org.sample;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelListener;
import java.awt.event.MouseWheelEvent;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.dnd.DnDConstants;

import java.net.URL;
import java.util.StringTokenizer;
import java.util.Arrays;

import org.common.RemoteThings;

import javax.media.Player;
import javax.media.protocol.*;
import javax.media.ControllerClosedEvent;
import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.NoPlayerException;
import javax.media.Processor;
import javax.media.Controller;

import javax.swing.JFrame;
import java.awt.Component;

import org.springframework.remoting.RemoteConnectFailureException;

public class Players implements 
				MouseMotionListener,MouseListener,MouseWheelListener{//,Runnable{
	
	JFrame frame;
	Component c;
	int x = 0, y = 0;
	String url = null;
	
	public Players(){
		frame = new JFrame("Sample");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(100,100);
		
		MediaLocator ml = new MediaLocator("rtp://" + Main.Addr + ":" + Main.Port + "/video/1");
		Player p = null;
		
		try{
			System.out.println("Listening ....");
			p = Manager.createPlayer(ml);
		}catch(NoPlayerException e){
			System.err.println("No player");
			System.exit(0);
		}catch(IOException e){
			e.printStackTrace();
			System.err.println("You got IOException here");
			System.exit(0);
		}
		
		boolean result = waitForState(p,Controller.Realized);
		
		System.out.println("set up the listener");
		
		c = p.getVisualComponent();
		c.addMouseMotionListener(this); 
		c.addMouseListener(this);
		c.addMouseWheelListener(this);
		c.addKeyListener(new KeyBoards());
		
		DropTarget dt = new DropTarget(c,new Dragging());
		
		c.setDropTarget(dt);
		
		frame.getContentPane().add(c);
		frame.addMouseMotionListener(this);
		frame.addMouseListener(this);
		frame.addMouseWheelListener(this);
		frame.addKeyListener(new KeyBoards());
		
		frame.setVisible(true);
		
		System.out.println("Here");
	}
	
	private static Integer stateLock = new Integer(0);
    private static boolean failed = false;
    
    static Integer getStateLock() {
    	return stateLock;
    }

    void setFailed() {
    	failed = true;
    }
    
    private synchronized boolean waitForState(Player p, int state) {
    	p.addControllerListener(new StateListener());
    	failed = false;

    	// Call the required method on the processor
    	//if (state == Processor.Configured) {
    	  //  p.configure();
    	/*} else */if (state == Processor.Realized) {
    	    p.realize();
			p.start();
    	}
    	
    	// Wait until we get an event that confirms the
    	// success of the method, or a failure event.
    	// See StateListener inner class
    	while (p.getState() < state && !failed) {
    	    synchronized (getStateLock()) {
    		try {
    		    getStateLock().wait();
    		} catch (InterruptedException ie) {
    		    return false;
    		}
    	    }
    	}

    	if (failed)
    	    return false;
    	else
    	    return true;
    }

    class StateListener implements ControllerListener {

    	public void controllerUpdate(ControllerEvent ce) {

    		// If there was an error during configure or
    		// realize, the processor will be closed
    		if (ce instanceof ControllerClosedEvent)
    			setFailed();

    		// All controller events, send a notification
    		// to the waiting thread in waitForState method.
    		if (ce instanceof ControllerEvent) {
    			synchronized (getStateLock()) {
    				getStateLock().notifyAll();
    			}
    		}
    	}
    }

    public void mouseMoved(MouseEvent me){	
    	this.x = me.getX();
    	this.y = me.getY();
    
    	//Spring RMI rather than Java RMI
    	try{
    		Main.rmiSpringService.remoteMouse(this.x,this.y);
    	}catch(RemoteConnectFailureException e){
    		System.err.println("The server might be down right now");
    		System.exit(1);
    	}
   
    	System.out.println(this.x + " " + this.y);
    }

    public void mouseDragged(MouseEvent me){
    	System.out.println("Dragging ....");
    }
    
    public void mousePressed(MouseEvent me){
    	
    	int buttons = me.getButton();
    	
    	if(buttons == MouseEvent.BUTTON1)
    		buttons = InputEvent.BUTTON1_MASK;
    	else if(buttons == MouseEvent.BUTTON2)
    		buttons = InputEvent.BUTTON2_MASK;
    	else if(buttons == MouseEvent.BUTTON3)
    		buttons = InputEvent.BUTTON3_MASK;
    	
    	//Spring RMI rather than Java RMI
    	try{
    		Main.rmiSpringService.remoteMousePress(buttons);
    	}catch(RemoteConnectFailureException e){
    		System.err.println("The server might be down right now");
    		System.exit(1);
    	}
    }
    
    public void mouseReleased(MouseEvent me){
    	
    	int buttons = me.getButton(); 
    	
    	if(buttons == MouseEvent.BUTTON1)
    		buttons = InputEvent.BUTTON1_MASK;
    	else if(buttons == MouseEvent.BUTTON2)
    		buttons = InputEvent.BUTTON2_MASK;
    	else if(buttons == MouseEvent.BUTTON3)
    		buttons = InputEvent.BUTTON3_MASK;
    
    	//Spring RMI rather than Java RMI
    	try{
    		Main.rmiSpringService.remoteMouseRelease(buttons);
    	}catch(RemoteConnectFailureException e){
    		System.err.println("The server might be down right now");
    		System.exit(1);
    	}
    }
    
    public void mouseEntered(MouseEvent me){
    	System.err.println("Entered the remote screen");
    }
    
    public void mouseExited(MouseEvent me){
    	System.err.println("Exited the remote screen");
    }
    
    //Click event has pressed and released events
    public void mouseClicked(MouseEvent me){
    	System.err.println("Mouse clicked");
    }
    
    public void mouseWheelMoved(MouseWheelEvent mwe){
    	
    	if(mwe.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL){
    	
    		//Spring RMI rather than Java RMI
    		try{
    			Main.rmiSpringService.remoteMouseWheel(mwe.getScrollAmount());
    		}catch(RemoteConnectFailureException e){
        		System.err.println("The server might be down right now");
        		System.exit(1);
        	}	
    	}
    }
        
    public void moveRemotely(){
    	
    	//Spring RMI rather than Java RMI
    	try{
    		Main.rmiSpringService.remoteMouse(x, y);
    	}catch(RemoteConnectFailureException e){
    		System.err.println("The server might be down right now");
    		System.exit(1);
    	}
    }
    
    //the class that receives events for drag and drop
    public class Dragging implements DropTargetListener{
    	
    	public void dragEnter(DropTargetDragEvent dtde){
    	
    		System.err.println("Entered");
    	}
    	
    	public void dragExit(DropTargetEvent dte){
    		
    		System.err.println("Exited");
    	}
    	
    	public void dragOver(DropTargetDragEvent dtde){
    		
    		System.err.println("Overed");
    	}
    	
    	//This is in progress
    	public void drop(DropTargetDropEvent dtde){
    		
    		//get the dropped object and try to figure out what it is
    		Transferable tf = dtde.getTransferable();
    		
    		//get the flavors, types of the transfered data
    		DataFlavor[] df = tf.getTransferDataFlavors();
    		
    		//This is for a custom flavor whose flavor is URL
    		DataFlavor df2 = null;
    		
    		try{
    			df2 = 
    				new DataFlavor("application/x-java-url; class=java.net.URL");
    		}catch(ClassNotFoundException e){
    			System.err.println("Stuck here about custom flavor");
    			e.printStackTrace();
    		}
    		
    		//check whether the data type fits one of the flavors
    		for(int i = 0; i < df.length;i++){
    			System.out.println(df[i].getMimeType());
    			
    			System.err.println("You are here");
    			
    			//check the data type
    			if((df[i].getMimeType()).equals(df2.getMimeType()) ||
    					df[i].isFlavorJavaFileListType()){
    				System.err.println("Comes to flavor stuff");
    				
    				//Now the type is what you want, so accept copying!
    				dtde.acceptDrop(DnDConstants.ACTION_COPY);
    				
    				try{ //output some information about it
    					System.err.println("Successful : " +
    							tf.getTransferData(df[i]));
    				
    					System.err.println("String : " + tf.getTransferData(df[i]));
    					
    					/*
    					 * If I use toString() for Transferable, in this case
    					 * tf variable, the output is like this:
    					 * 
    					 * [C:\Documents and Settings\ ......]
    					 * The reason for this is that the output is an Object.
    					 * So I have to change this format to another one 
    					 * which doesn't have '[' and ']'.
    					 */
    					
    					copyFiles(tf.getTransferData(df[i]).toString());
    					
    				}catch(UnsupportedFlavorException e){
    					System.err.println("Error about Flavors");
    					e.printStackTrace();
    				}catch(IOException e){
    					e.printStackTrace();
    				}
    			}
    		
    			System.err.println("You are done");
    			dtde.dropComplete(true);
    			return;
    		}
    	}
    	
    	public void dropActionChanged(DropTargetDragEvent dtde){
    	System.err.println("Changed");
    	
    	}
    
    }
    
    public class KeyBoards implements KeyListener{
    	
    	long rmiStart = 0l;
    	long rmiEnd = 0l;
    	long cTime = 0l;
    	long rTime = 0l;
    	
    	public void keyPressed(KeyEvent ke){
    		
    		try{
    			Main.rmiSpringService.remoteKeyBoardsPress(ke);
    		}catch(RemoteConnectFailureException e){
        		System.err.println("The server might be down right now");
        		System.exit(1);
        	}
    	}
    	
    	public void keyReleased(KeyEvent ke){
    		
    		try{
    			Main.rmiSpringService.remoteKeyBoardsRelease(ke);
    		}catch(RemoteConnectFailureException e){
        		System.err.println("The server might be down right now");
        		System.exit(1);
        	}
    	}
    	
    	public void keyTyped(KeyEvent ke){
    		System.err.println("Typed : " + ke.getKeyChar());
    	}
    }
    
    public void copyFiles(String s){
		
		if(s.startsWith("[") && s.endsWith("]"))
			System.err.println("Oh, yeah ?");
		
		String s2 = s.replace(']',' ');
		
		StringBuilder sb = new StringBuilder(s2);
		
		sb.deleteCharAt(0);
		
		String s3 = sb.toString();
		
		System.err.println(s3);
		
		File f = new File(s3);
		
		System.err.println("is File ? " + f.isFile());
		
		System.err.println("Is directory ? " + f.isDirectory());
		
		//get the file or directory name
		System.err.println("Your name : " + f.getName());
		
		if(f.isFile()){
			
			//FileInputStream fis = null;
			InputStream fis = null;
			
			try {
				fis = new FileInputStream(f);
			} catch (FileNotFoundException e1) {
				System.err.println("You stuck, here about FileInputStream");
				e1.printStackTrace();
			}

			int i = 0;
			byte[] b =null; //new byte[(int)f.length()];
			
			try{
				i = fis.available();
	            b = new byte[i];
	            
	            fis.read(b);
				
				//while(offset < b.length &&
					//(numRead = fis.read(b, offset, b.length - offset)) >= 0)
			
					//offset+= numRead;
				fis.close();
			} catch (IOException e1) {
				System.err.println("Stuck here");
				e1.printStackTrace();
			}
			
			try{
				Main.rmiSpringService.remoteDragAndDrop(f.getName(), b);
			}catch(RemoteConnectFailureException e){
	    		System.err.println("The server might be down right now");
	    		System.exit(1);
	    	}
		}
    }
}
