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
		
		//Helper.ServerAddress = text.getText();
		//Main.Port = text2.getText();
		Helper.ServerAddress = text.getText();
		Main.Port = text2.getText();
		Main.Addr = Helper.ServerAddress;
		
		if(getTrue(Main.b)){
			frame.dispose();
			
			System.err.println(Helper.modApplicationContext());
				
			Main.runApplicationContext();
			
			SwingUtilities.invokeLater(new Runnable(){
				public void run(){
					//new InputForm();
					new Players();
				}
			});
		}
		
		System.out.println("Now :" + Main.Addr);
		System.out.println("Now : InputForm : " + Helper.ServerAddress);
		System.out.println("Now : " + Main.Port);
		
		//Main.runApplicationContext();
		//Helper.modApplicationContext();
		
		//if(Helper.modApplicationContext())
			//Main.runApplicationContext();
	}
	
	public static boolean getTrue(boolean b){
		b = true;
		
		return b;
	}
	
	/*
	 * One more method:
	 * 
	 * check if the input value is the correct format following
	 * the IP address format such as XXX.XXX.XXX and so on.
	 * 
	 * using regular express
	 */

	private boolean inputAnalyzer(){
		
		return true;
	}
}
