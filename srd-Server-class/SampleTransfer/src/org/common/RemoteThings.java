package org.common;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.io.File;

public interface RemoteThings{

	void remoteMouse(int x,int y);
	void remoteMousePress(int buttons);
	void remoteMouseRelease(int buttons);
	void remoteMouseWheel(int amount);
	void remoteMouseDrag(int x,int y); // in progress
    void remoteDragAndDrop(String name,byte[] b);  
	void remoteKeyBoardsPress(KeyEvent ke);
	void remoteKeyBoardsRelease(KeyEvent ke);
	void remoteClipboardPaste();
	Rectangle remoteGetScreenSize(Rectangle rect);
}
