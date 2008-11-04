package org.sample.media.protocol.screen;

 
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.media.*;
import javax.media.format.*;
import javax.media.protocol.*;
import java.io.IOException;
import java.util.StringTokenizer;

public class LiveStream implements PushBufferStream, Runnable {

    protected ContentDescriptor cd = new ContentDescriptor(ContentDescriptor.RAW);
    protected int maxDataLength;
    protected int [] data;
    //protected Dimension size;
    private Dimension size;
    protected RGBFormat rgbFormat;
    protected boolean started;
    protected Thread thread;
    protected float frameRate = 1f;
    protected BufferTransferHandler transferHandler;
    protected Control [] controls = new Control[0];
    protected int x, y, width, height;
    public Processor p = null;
    public static MediaLocator ml2;
    public MediaLocator locator;
    protected Robot robot = null;

    public LiveStream(MediaLocator locator) {
	//public LiveStream(){
    try {
    	//locator = new MediaLocator("screen://0,0,1024,768/10");
	    //parseLocator(locator);
    	parseLocator(locator);
	} catch (Exception e) {
		System.err.println("Stuck here");
	    System.err.println(e);
	}
	//size = Toolkit.getDefaultToolkit().getScreenSize();
	size = new Dimension(width, height);
	try {
	    robot = new Robot();
	} catch (AWTException awe) {
		System.err.println("Robot got error");
	    throw new RuntimeException("");
	}
	maxDataLength = size.width * size.height * 3;
	rgbFormat = new RGBFormat(size, maxDataLength,
				  Format.intArray,
				  frameRate,
				  32,
				  0xFF0000, 0xFF00, 0xFF,
				  1, size.width,
				  VideoFormat.FALSE,
				  Format.NOT_SPECIFIED);
	
	// generate the data
	data = new int[maxDataLength];
	thread = new Thread(this, "Screen Grabber");
    }

    protected void parseLocator(MediaLocator locator) {
	String rem = locator.getRemainder();
	// Strip off starting slashes
	while (rem.startsWith("/") && rem.length() > 1)
	    rem = rem.substring(1);
	StringTokenizer st = new StringTokenizer(rem, "/");
	if (st.hasMoreTokens()) {
	    // Parse the position
	    String position = st.nextToken();
	    StringTokenizer nums = new StringTokenizer(position, ",");
	    String stX = nums.nextToken();
	    String stY = nums.nextToken();
	    String stW = nums.nextToken();
	    String stH = nums.nextToken();
	    x = Integer.parseInt(stX);
	    y = Integer.parseInt(stY);
	    width = Integer.parseInt(stW);
	    height = Integer.parseInt(stH);
	}
	if (st.hasMoreTokens()) {
	    // Parse the frame rate
	    String stFPS = st.nextToken();
	    frameRate = (Double.valueOf(stFPS)).floatValue();
	}
    }
    
    /***************************************************************************
     * SourceStream
     ***************************************************************************/
    
    public ContentDescriptor getContentDescriptor() {
	return cd;
    }

    public long getContentLength() {
	return LENGTH_UNKNOWN;
    }

    public boolean endOfStream() {
	return false;
    }

    /***************************************************************************
     * PushBufferStream
     ***************************************************************************/

    int seqNo = 0;
    
    public Format getFormat() {
	return rgbFormat;
    }

    public void read(Buffer buffer)throws IOException{
    	synchronized (this) {
    		Object outdata = buffer.getData();
    		if (outdata == null || !(outdata.getClass() == Format.intArray) ||
    				((int[])outdata).length < maxDataLength) {
    			outdata = new int[maxDataLength];
    			buffer.setData(outdata);
    		}
    		
    		buffer.setFormat( rgbFormat );
    		buffer.setTimeStamp( (long) (seqNo * (1000 / frameRate) * 1000000) );
    		BufferedImage bi = robot.createScreenCapture(
    				new Rectangle(x, y, width, height));
    		bi.getRGB(0, 0, width, height,
    				(int[])outdata, 0, width);
	    
    		buffer.setSequenceNumber( seqNo );
    		buffer.setLength(maxDataLength);
    		buffer.setFlags(Buffer.FLAG_KEY_FRAME);
    		buffer.setHeader( null );
    		seqNo++;
    	}
    }	

    public void setTransferHandler(BufferTransferHandler transferHandler) {
	synchronized (this) {
	    this.transferHandler = transferHandler;
	    notifyAll();
	}
    }

    void start(boolean started) {
	synchronized ( this ) {
	    this.started = started;
	    if (started && !thread.isAlive()) {
		thread = new Thread(this);
		thread.start();
	    }
	    notifyAll();
	}
    }

    /***************************************************************************
     * Runnable
     ***************************************************************************/

    public void run() {
	while (started) {
	    synchronized (this) {
		while (transferHandler == null && started) {
		    try {
			wait(1000);
		    } catch (InterruptedException ie) {
		    }
		} // while
	    }

	    if (started && transferHandler != null) {
		transferHandler.transferData(this);
		try {
		    Thread.currentThread().sleep( 10 );
		} catch (InterruptedException ise) {
		}
	    }
	} // while (started)
    } // run

    // Controls
    
    public Object [] getControls() {
	return controls;
    }

    public Object getControl(String controlType) {
       try {
          Class  cls = Class.forName(controlType);
          Object cs[] = getControls();
          for (int i = 0; i < cs.length; i++) {
             if (cls.isInstance(cs[i]))
                return cs[i];
          }
          return null;

       } catch (Exception e) {   // no such controlType or such control
         return null;
       }
    }
    
    public void gatherInfo(){
    	
    }
}

