package org.sample;

/*
 * @(#)VideoTransmit.java	1.7 01/03/13
 *
 * Copyright (c) 1999-2001 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Sun grants you ("Licensee") a non-exclusive, royalty free, license to use,
 * modify and redistribute this software in source and binary code form,
 * provided that i) this copyright notice and license appear on all copies of
 * the software; and ii) Licensee does not utilize the software in a manner
 * which is disparaging to Sun.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
 * OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS
 * LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF
 * OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 *
 * This software is not designed or intended for use in on-line control of
 * aircraft, air traffic, aircraft navigation or aircraft communications; or in
 * the design, construction, operation or maintenance of any nuclear
 * facility. Licensee represents and warrants that it will not use or
 * redistribute the Software for such purposes.
 */

import java.awt.*;
import javax.media.*;
import javax.media.protocol.*;
import javax.media.format.*;
import javax.media.control.TrackControl;
import javax.media.control.QualityControl;

import java.io.*;

import org.sample.media.protocol.screen.DataSource;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class VideoTransmit5 implements Runnable{

    // Input MediaLocator
    // Can be a file or http or capture source
    private MediaLocator locator;
    private String ipAddress;
    private String port;

    private Processor processor = null;
    private DataSink  rtptransmitter = null;
    private static org.sample.media.protocol.screen.DataSource ds = null;
    private static javax.media.protocol.DataSource dataOutput = null;

    public static String height;
    public static String width;
    public static Dimension screen;
    
    public VideoTransmit5(){
    	
    }
    
    //I use this constructor
    public VideoTransmit5(String ipAddress,String port){	
    	
    	screen = Toolkit.getDefaultToolkit().getScreenSize();
    	String tempWidth = (new Integer(screen.width)).toString();
    	String tempHeight = (new Integer(screen.height)).toString();
    	
    	ds = new DataSource();
    	
    	try {
    		ds.connect();
    	} catch (IOException e1) {
    		System.out.println("ERRRORRR");
    		e1.printStackTrace();
    	}
    	
    	if(ds == null){
    		System.out.println("Fuck");
    		System.exit(-1);
    	}
    	
    	this.ipAddress = ipAddress;
    	this.port = port;
    	this.width = tempWidth;
    	this.height = tempHeight;
    }
    
    public VideoTransmit5(String ipAddress,String port,String width,String height){
    	
    	ds = new DataSource();
    	
    	try {
    		ds.connect();
    	} catch (IOException e1) {
    		System.out.println("ERRRORRR");
    		e1.printStackTrace();
    	}
    	
    	if(ds == null){
    		System.out.println("Fuck");
    		System.exit(-1);
    	}
    	
    	this.ipAddress = ipAddress;
    	this.port = port;
    	this.width = width;
    	this.height = height;
    }
    
    /**
     * Starts the transmission. Returns null if transmission started ok.
     * Otherwise it returns a string with the reason why the setup failed.
     */
    public synchronized String start() {
	String result;

	// Create a processor for the specified media locator
	// and program it to output JPEG/RTP
	result = createProcessor();
	if (result != null)
	    return result;

	// Create an RTP session to transmit the output of the
	// processor to the specified IP address and port no.
	result = createTransmitter();
	if (result != null) {
	    processor.close();
	    processor = null;
	    return result;
	}

	// Start the transmission
	processor.start();
	
	return null;
    }
   
    /**
     * Stops the transmission if already started
     */
    public void stop() {
	synchronized (this) {
	    if (processor != null) {
		processor.stop();
		processor.close();
		processor = null;
		rtptransmitter.close();
		rtptransmitter = null;
	    }
	}
    }

    private String createProcessor() {
	
	if(ds == null){
		System.err.println("Fuck you here");
	}
	

	// Try to create a processor to handle the input media locator
	try {
	    processor = Manager.createProcessor(ds);
	    
	    processor.setSource(ds);
		System.out.println(processor.getRate());
	} catch (NoProcessorException npe) {
	    return "Couldn't create processor";
	} catch (IOException ioe) {
	    return "IOException creating processor";
	} catch (IncompatibleSourceException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}   
		
	
	
	// Wait for it to configure
	boolean result = waitForState(processor, Processor.Configured);
	if (result == false)
	    return "Couldn't configure processor";

	// Get the tracks from the processor
	TrackControl [] tracks = processor.getTrackControls();

	// Do we have at least one track?
	if (tracks == null || tracks.length < 1)
	    return "Couldn't find tracks in processor";

	boolean programmed = false;

	// Search through the tracks for a video track
	for (int i = 0; i < tracks.length; i++) {
	    Format format = tracks[i].getFormat();
	    if (  tracks[i].isEnabled() &&
		  format instanceof VideoFormat &&
		  !programmed) {
		
		// Found a video track. Try to program it to output JPEG/RTP
		// Make sure the sizes are multiple of 8's.
		Dimension size = ((VideoFormat)format).getSize();
		float frameRate = ((VideoFormat)format).getFrameRate();
		int w = (size.width % 8 == 0 ? size.width :
				(int)(size.width / 8) * 8);
		int h = (size.height % 8 == 0 ? size.height :
				(int)(size.height / 8) * 8);
		VideoFormat jpegFormat = new VideoFormat(VideoFormat.JPEG_RTP,
							 new Dimension(w, h),
							 Format.NOT_SPECIFIED,
							 Format.byteArray,
							 frameRate);
		tracks[i].setFormat(jpegFormat);
		System.err.println("Video transmitted as:");
		System.err.println("  " + jpegFormat);
		// Assume succesful
		programmed = true;
	    } else
		tracks[i].setEnabled(false);
	}

	if (!programmed)
	    return "Couldn't find video track";

	// Set the output content descriptor to RAW_RTP
	ContentDescriptor cd = new ContentDescriptor(ContentDescriptor.RAW_RTP);
	processor.setContentDescriptor(cd);

	// Realize the processor. This will internally create a flow
	// graph and attempt to create an output datasource for JPEG/RTP
	// video frames.
	result = waitForState(processor, Controller.Realized);
	if (result == false)
	    return "Couldn't realize processor";

	// Set the JPEG quality to .5.
	setJPEGQuality(processor, 0.5f);
	
	// Get the output data source of the processor
	//dataOutput = new DataSource();
	dataOutput = processor.getDataOutput();
	
	return null;
    }

    // Creates an RTP transmit data sink. This is the easiest way to create
    // an RTP transmitter. The other way is to use the RTPSessionManager API.
    // Using an RTP session manager gives you more control if you wish to
    // fine tune your transmission and set other parameters.
    private String createTransmitter() {
	// Create a media locator for the RTP data sink.
	// For example:
	//    rtp://129.130.131.132:42050/video
	
    String rtpURL = "rtp://" + ipAddress + ":" + port + "/video/1";
	MediaLocator outputLocator = new MediaLocator(rtpURL);

	// Create a data sink, open it and start transmission. It will wait
	// for the processor to start sending data. So we need to start the
	// output data source of the processor. We also need to start the
	// processor itself, which is done after this method returns.
	
	if(processor.getDataOutput() == null)
		System.err.println("Die");
	else
		System.out.println(processor.getDataOutput());
	
	try {
	    rtptransmitter = Manager.createDataSink(dataOutput,outputLocator);
	    
	    if(rtptransmitter == null){
	    	System.err.println("Error");
	    }else{
	    	System.out.println("------------------");
	    	System.out.println(rtptransmitter.toString());
	    	System.out.println(rtptransmitter.getContentType());
	    	System.out.println(rtptransmitter.getOutputLocator());
	    }
	    
	    rtptransmitter.open();
	    rtptransmitter.start();
	    
	    ds.start();
	    
	} catch (MediaException me) {
	    return "Couldn't create RTP data sink";
	} catch (IOException ioe) {
	    return "Couldn't create RTP data sink";
	}
	
	return null;
    }


    /**
     * Setting the encoding quality to the specified value on the JPEG encoder.
     * 0.5 is a good default.
     */
    void setJPEGQuality(Player p, float val) {

	Control cs[] = p.getControls();
	QualityControl qc = null;
	VideoFormat jpegFmt = new VideoFormat(VideoFormat.JPEG);

	// Loop through the controls to find the Quality control for
 	// the JPEG encoder.
	for (int i = 0; i < cs.length; i++) {

	    if (cs[i] instanceof QualityControl &&
		cs[i] instanceof Owned) {
		Object owner = ((Owned)cs[i]).getOwner();

		// Check to see if the owner is a Codec.
		// Then check for the output format.
		if (owner instanceof Codec) {
		    Format fmts[] = ((Codec)owner).getSupportedOutputFormats(null);
		    for (int j = 0; j < fmts.length; j++) {
			if (fmts[j].matches(jpegFmt)) {
			    qc = (QualityControl)cs[i];
	    		    qc.setQuality(val);
			    System.err.println("- Setting quality to " + 
					val + " on " + qc);
			    break;
			}
		    }
		}
		if (qc != null)
		    break;
	    }
	}
    }


    /****************************************************************
     * Convenience methods to handle processor's state changes.
     ****************************************************************/
    
    private Integer stateLock = new Integer(0);
    private boolean failed = false;
    
    Integer getStateLock() {
	return stateLock;
    }

    void setFailed() {
	failed = true;
    }
    
    private synchronized boolean waitForState(Processor p, int state) {
	p.addControllerListener(new StateListener());
	failed = false;

	// Call the required method on the processor
	if (state == Processor.Configured) {
	    p.configure();
	} else if (state == Processor.Realized) {
	    p.realize();
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

    /****************************************************************
     * Inner Classes
     ****************************************************************/

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
      
    public void run(){
    	
    	//Rather than binding using JNDI, I use Spring RMI which uses
    	//Dependency Injection.
    	
    	ApplicationContext context = 
    		new FileSystemXmlApplicationContext("./conf/SpringRMIServer.xml");
    		//new FileSystemXmlApplicationContext("SpringRMIServer.xml");
    }
    

    /****************************************************************
     * Sample Usage for VideoTransmit class
     ****************************************************************/
    
    public static void main(String [] args) {
    	
    	System.out.println("Starting ...");
    	
    	(new Thread(new VideoTransmit5())).start();
    	
    	/* 
    	 * Either constructor is fine. The two argument one is
    	 * automatically detect the screen size.
    	 */
    	
    	//VideoTransmit5 vt = new VideoTransmit5(args[0],args[1],args[2],args[3]);
    	
    	VideoTransmit5 vt = new VideoTransmit5(args[0],args[1]);
    	
    	// Start the transmission
    	String result = vt.start();
	
    	// result will be non-null if there was an error. The return
    	// value is a String describing the possible error. Print it.
    	if (result != null) {
    		System.err.println("Error : " + result);
    		System.exit(0);
    	}

    	System.err.println("Start transmission");
	}
}
