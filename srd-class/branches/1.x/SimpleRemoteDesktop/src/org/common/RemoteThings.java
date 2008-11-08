package org.common;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
//import java.rmi.Remote;
//import java.rmi.RemoteException;

/*Since I use Spring RMI, I don't use Remote interface from 
 * Java RMI.
 */

public interface RemoteThings {

	void remoteMouse(int x,int y) ;
	void remoteMousePress(int buttons);
	void remoteMouseRelease(int buttons);
	void remoteMouseWheel(int amount);
	void remoteMouseDrag(int x,int y); //in process
	void remoteDragAndDrop(String name,byte[] b) ;
	void remoteKeyBoardsPress(KeyEvent ke);
	void remoteKeyBoardsRelease(KeyEvent ke);
	Rectangle remoteGetScreenSize(Rectangle rect);
}
