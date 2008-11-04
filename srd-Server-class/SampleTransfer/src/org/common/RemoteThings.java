package org.common;

import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.io.File;

public interface RemoteThings{

	void remoteMouse(int x,int y);
	void remoteMousePress(int buttons);
	void remoteMouseRelease(int buttons);
	void remoteMouseWheel(int amount);
    void remoteDragAndDrop(String name,byte[] b);  // in progress
	void remoteKeyBoardsPress(KeyEvent ke);
	void remoteKeyBoardsRelease(KeyEvent ke);
}
