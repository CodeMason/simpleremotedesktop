package org.common;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.PrintWriter;
import java.net.Socket;

public interface RemoteThings{

	void remoteMouse(int x,int y);
	void remoteMousePress(int buttons);
	void remoteMouseRelease(int buttons);
	void remoteMouseWheel(int amount);
	void remoteMouseDrag(int x,int y); // in progress
    void remoteDragAndDrop(String name,byte[] b);  
	void remoteKeyBoardsPress(KeyEvent ke);
	void remoteKeyBoardsRelease(KeyEvent ke);
	String remoteClipboardCopy();
	void remoteClipboardPaste();
	Rectangle remoteGetScreenSize(Rectangle rect);
	 void remoteSetUpClient(String target, Socket clientSock,
				BufferedReader br, PrintWriter pw,String dest,int port);
}
