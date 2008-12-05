package org.sample;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelListener;
import java.awt.event.MouseWheelEvent;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.ClipboardOwner; // new feature
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.dnd.DnDConstants;

import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
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
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;

import org.springframework.remoting.RemoteConnectFailureException;

public class Players implements 
				MouseMotionListener,MouseListener,MouseWheelListener,ComponentListener{//,Runnable{
	
	public static Rectangle rect;
	public static String fileName;
	JFrame frame;
	Component c;
	int x = 0, y = 0;
	String url = null;
	static int counter = 0; //counter for copy & paste
	
	private final String DATAFLAVOR = "application/x-java-url; class=java.net.URL";
	
	public int originalX;
	public int originalY;
	
	public int destX;
	public int destY;
	
	public Players(){
		frame = new JFrame("Sample");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(200,200);
		
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
		//c.addComponentListener(this);
		
		DropTarget dt = new DropTarget(c,new Dragging());
		
		c.setDropTarget(dt);
		
		frame.getContentPane().add(c);
		frame.addMouseMotionListener(this);
		frame.addMouseListener(this);
		frame.addMouseWheelListener(this);
		frame.addKeyListener(new KeyBoards());
		frame.addComponentListener(this);
		
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
   
    	//System.out.println(this.x + " " + this.y);
    }

    public void mouseDragged(MouseEvent me){
    	
    	try{
    		Main.rmiSpringService.remoteMouseDrag(this.originalX+(this.destX - this.originalX),this.originalY+(this.destY-this.originalY));
       	}catch(RemoteConnectFailureException e){
    		System.err.println("The server might be down right now");
    		System.exit(1);
    	}
    }
    
    public void mousePressed(MouseEvent me){
    	
    	int buttons = me.getButton();
    	
    	if(buttons == MouseEvent.BUTTON1){
    		buttons = InputEvent.BUTTON1_MASK;
    		
    		//get the original coordinate
        	this.originalX = me.getX();
        	this.originalY = me.getY();
    	}
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
    	
    	if(buttons == MouseEvent.BUTTON1){
    		buttons = InputEvent.BUTTON1_MASK;
    		
    		//get the destination coordinate
    		destX = me.getX();
    		destY = me.getY();
    	}	
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
    	
    	//exit dragging an object!!
    	//if(me.getID() == MouseEvent.MOUSE_DRAGGED){
    		//System.out.println("Exit dragging");
    	//}
    	
    	if(MouseEvent.BUTTON1 == me.getButton())
    		System.out.println("having an object");
    	
    	System.err.println("Exited the remote screen" + me.getID());
    }
    
    /*
     * Right now mouseClicked method is the same action as 
     * mousePressed method.
     * 
     */
    public void mouseClicked(MouseEvent me){
    	
    	//get the original coordinate
    	this.originalX = me.getX();
    	this.originalY = me.getY();
    	
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
    		
    		/*
    		 * I should do somethinf here to make
    		 * the screen drag and drop from the server
    		 * to the client.
    		 * 
    		 * Something like, boolean remoteMouseDrag(int x,int y)
    		 * 
    		 * if the method returns true, which means
    		 * it's still dragging some object
    		 * and exit the JFrame. In this case, drag and drop
    		 * happens.
    		 * 
    		 * Otherwise, it's not happening.
    		 * 
    		 */
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
    				new DataFlavor(DATAFLAVOR);
    		}catch(ClassNotFoundException e){
    			System.err.println("Stuck here about custom flavor");
    			e.printStackTrace();
    		}
    		
    		//check whether the data type fits one of the flavor
    		System.out.println("Checking the mime type");
    		for(int i = 0; i < df.length;i++){
    			//System.out.println(df[i].getMimeType());
    			
    			//check the data type
    			if((df[i].getMimeType()).equals(df2.getMimeType()) ||
    					df[i].isFlavorJavaFileListType()){
    				
    				//System.out.println("data type is : " + df[i]);
    				//System.err.println("Comes to flavor stuff");
    				
    				//Now the type is what you want, so accept copying!
    				dtde.acceptDrop(DnDConstants.ACTION_COPY);
    				
    				try{ //output some information about it
    					//System.err.println("Successful : " +
    						//	tf.getTransferData(df[i]));
    				
    					//System.err.println("String : " + tf.getTransferData(df[i]));
    					
    					copyFiles(tf.getTransferData(df[i]).toString());
    					
    				}catch(UnsupportedFlavorException e){
    					System.err.println("Error about Flavors");
    					e.printStackTrace();
    				}catch(IOException e){
    					System.err.println("Error about IO");
    				}catch(Exception ee){
    					System.err.println("Error about general");
    				}
    			}else
    				df[i].getMimeType();
    			
    			dtde.dropComplete(true);
    			
    			return;
    		}
    		
    		System.err.println("You are done");
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
    	
    	//This method is just ignored
    	public void keyPressed(KeyEvent ke){
    		try{
    			Main.rmiSpringService.remoteKeyBoardsPress(ke);
    		}catch(RemoteConnectFailureException e){
        		System.err.println("The server might be down right now");
        		System.exit(1);
        	}catch(IllegalArgumentException e){
        		System.err.println("unrecognized keys");
        	}
    	}
    	
    /*	public void keyPressed(KeyEvent ke){
    		try{
    			if(ke.getKeyCode() == 0 && (counter % 2) == 0)
    				Main.rmiSpringService.remoteClipboardCopy();
    			else if(ke.getKeyCode() == 0 && (counter % 2) != 0)
    				Main.rmiSpringService.remoteClipboardPaste();
    			else
    				Main.rmiSpringService.remoteKeyBoardsPress(ke);
    			
    		}catch(RemoteConnectFailureException e){
        		System.err.println("The server might be down right now");
        		System.exit(1);
        	}catch(IllegalArgumentException e){
        		System.err.println("unrecognized keys");
        	}
    	}*/
    	
    	public void keyReleased(KeyEvent ke){
    		
    		try{
    			Main.rmiSpringService.remoteKeyBoardsRelease(ke);
    		}catch(RemoteConnectFailureException e){
        		System.err.println("The server might be down right now");
        		System.exit(1);
        	}catch(IllegalArgumentException e){
        		System.err.println("unrecognized keys");
        	}
    	}
    	
    	public void keyTyped(KeyEvent ke){
    		//System.err.println("Typed : " + ke.getKeyCode());
    		
    		Main.rmiSpringService.remoteKeyBoardsPress(ke);
    		
    		try{
    			if(ke.getKeyChar() == '|'){
    				System.out.println("remoteClipboardPaste() called");
    				Main.rmiSpringService.remoteClipboardPaste();
    			}else if(ke.getKeyChar() == '`'){
    				System.out.println("remoteClipboardCopy() called");
    				System.out.println(Helper.analyzeFileName(Helper.checkFileName(Main.rmiSpringService.remoteClipboardCopy())));
    				fileName = Helper.analyzeFileName(Helper.checkFileName(Main.rmiSpringService.remoteClipboardCopy()));
    			}
    			
    	}catch(RemoteConnectFailureException ee){
			System.err.println("The server might be down right now");
		}catch(IllegalArgumentException ee){
			//System.err.println("unrecognized keys");
			//ee.printStackTrace();
		}
    		
    		/*Transferable tra = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);	
    		
    		if(tra == null){
    			try{
    				Main.rmiSpringService.remoteClipboardCopy();
    			}catch(RemoteConnectFailureException e){
    				System.err.println("The server might be down right now");
    			}
    		}else if(tra != null){
    		
    			Socket clientSock = null;
    			BufferedReader br = null;
    			PrintWriter pw = null;
    			
    			try{
    				Main.rmiSpringService.remoteClipboardPaste();
    				
    				clientSock = new Socket("192.168.0.102",7777);
    				//read the socket stream to get the contents of a file.
    				br = new BufferedReader(new InputStreamReader(clientSock.getInputStream()));
    				String ss;
					
					while((ss = br.readLine()) != null){
						System.out.println(ss);
					}
						
					
    			}catch(RemoteConnectFailureException e){
    				System.err.println("The server might be down right now");
    			}catch(UnknownHostException e){
    				System.err.println("Unknown host error");
    			}catch(IOException e){
    				System.err.println("IO issue");
    			}
    		}
    		*/
    	}
    }
    
    public void copyFiles(String s){
    	
    	System.out.println("Enter copyFiles()");
    	
    	if(s.startsWith("[") && s.endsWith("]"))
			System.err.println("Oh, yeah ?");
		
			String s2 = s.replace(']',' ');
		
			StringBuilder sb = new StringBuilder(s2);
			//sb.deleteCharAt(0);
			sb.delete(0,16);
			String s3 = sb.toString();
			
			System.out.println("s3 : " + s3);
			
			File f = new File(s3);
		
			System.err.println("is File ? " + f.isFile());
			System.err.println("Is directory ? " + f.isDirectory());
			//get the file or directory name
			System.err.println("Your name : " + f.getName());
			
			if(f.isFile()){
				
				BufferedReader br = null;
				try {
					br = new BufferedReader(new FileReader(f));
				} catch (FileNotFoundException e1) {
					System.err.println("Error about BufferedReader");
					e1.printStackTrace();
				}
				
				String str2 = null;
				String str = null;
				
				//sample
				FileInputStream fis = null;
				byte[] by = null;	
				try{
					fis = new FileInputStream(f);
					
					by = new byte[fis.available()];
					
				    fis.read(by);
				}catch(FileNotFoundException e){
					e.printStackTrace();
				}catch(Exception e){
					e.printStackTrace();
				}
				
				/*In this case, readLine() is the problem.
				*Because it returns only the last line	
				*that it just finishes reading!!!
				*/
			
				/*try{
					while((str = br.readLine()) != null){
						str2 = str;
						System.out.println(str);
					}
					
					br.close();
				} catch (IOException e1) {
					System.err.println("Stuck here, about readLine()");
					e1.printStackTrace();
				}
			*/
				System.out.println("Total mount transferred is " + by.length);
				
				try{
					Main.rmiSpringService.remoteDragAndDrop(f.getName(), by);
				}catch(RemoteConnectFailureException e){
					System.err.println("The server might be down");
				}
			}else if(f.isDirectory()){
				System.out.println("Do something");
			}
			
			//System.out.println("End copyFiles()");
    }

	public void componentHidden(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void componentMoved(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void componentResized(ComponentEvent arg0) {
		
		rect = frame.getBounds();
		
		//System.out.println("rect : " + rect.getWidth() + " " + rect.getHeight());
		//System.out.println("rect x :" + rect.x + " " + "rect y : " + rect.y);
		
		try{
			Main.rmiSpringService.remoteGetScreenSize(rect);
			System.out.println(Main.rmiSpringService.remoteGetScreenSize(rect));
		}catch(RemoteConnectFailureException e){
			System.err.println("The server might be down");
		}catch(NullPointerException ee){
			Main.rmiSpringService.remoteGetScreenSize(frame.getBounds());
		}
	}

	public void componentShown(ComponentEvent arg0) {
		// TODO Auto-generated method stub
	}
}