package org.sample;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.AbstractButton;
import javax.swing.SwingUtilities;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.FlowLayout;

import java.util.regex.*;

public class InputForm extends JPanel implements ActionListener{
        
        JFrame frame;
        JTextField text;
        JTextField text2;
        JLabel label1;
        JLabel label2;
        JButton button1;
        
        public InputForm(){
                
                FlowLayout fl = new FlowLayout();
                
                frame = new JFrame("Here you are");
                frame.setSize(300, 300);
                frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
                
                frame.setLayout(fl);
                
                label1 = new JLabel("Server Addr: ");
                label2 = new JLabel("Server Port: ");
                
                text = new JTextField("Please enter address");
                text2 = new JTextField("Please enter port number");
                text.setSize(200, 50);
                
                button1 = new JButton("Set Up");
                button1.setSize(50, 20);
                button1.setHorizontalTextPosition(AbstractButton.LEADING);
                button1.setVerticalTextPosition(AbstractButton.CENTER);
                
                frame.add(label1);
                frame.add(text);
                frame.add(label2);
                frame.add(text2);
                frame.add(button1);
                button1.addActionListener(this);
                
                frame.setVisible(true);
                
                System.err.println("From InputForm");
                
        }

        public void actionPerformed(ActionEvent arg0) {
                
                //Analyzer should be here. Before Helper.ServerAddress gets String
                RemoteThingsImpl.yourAddr = text.getText();
                VideoTransmit5.clientPort = text2.getText();
                
                frame.dispose();
                
                System.out.println("Now :" + RemoteThingsImpl.yourAddr);
                System.out.println("Now : " + VideoTransmit5.clientPort);
                
                (new Thread(new VideoTransmit5())).start();
                
                VideoTransmit5 vt = new VideoTransmit5(RemoteThingsImpl.yourAddr,VideoTransmit5.clientPort);
            	//VideoTransmit5 = vt = new VideoTransmit5(clientAddr,clientPort);
            	
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

