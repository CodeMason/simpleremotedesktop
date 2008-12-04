package org.common;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.PrintWriter;
//import java.rmi.Remote;
//import java.rmi.RemoteException;
import java.net.Socket;

/*Since I use Spring RMI, I don't use Remote interface from 
 * Java RMI.
 */

public interface RemoteThings {

	void remoteMouse(int x,int y) ;
	void remoteMousePress(int buttons);
	void remoteMouseRelease(int buttons);
	void remoteMouseWheel(int amount);
	void remoteMouseDrag(int x,int y); //It's fine. 
	void remoteDragAndDrop(String name,byte[] b) ;
	void remoteKeyBoardsPress(KeyEvent ke);
	void remoteKeyBoardsRelease(KeyEvent ke);
	String remoteClipboardCopy();  //I will use this feature rather than remoteDragg() 
	void remoteClipboardPaste();
	Rectangle remoteGetScreenSize(Rectangle rect);
	void remoteSetUpClient(String target,Socket clientSock,
			BufferedReader br,PrintWriter pw,String dest, int port);
}
