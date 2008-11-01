package org.common;

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
	void remoteDragAndDrop(String name,byte[] b) ;  // in progress
	void remoteKeyBoardsPress(KeyEvent ke);
	void remoteKeyBoardsRelease(KeyEvent ke);
}
