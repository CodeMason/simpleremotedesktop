package org.sample;

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.AbstractButton;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.FlowLayout;

import java.util.regex.*;

public class InputForm extends JPanel implements ActionListener{
	
	JFrame frame;
	JTextField text;
	JButton button1;
	
	public InputForm(){
		
		FlowLayout fl = new FlowLayout();
		
		frame = new JFrame("Here you are");
		frame.setSize(300, 300);
		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		
		frame.setLayout(fl);
		
		text = new JTextField("Please enter address");
		text.setSize(200, 50);
		
		button1 = new JButton("Set Up");
		button1.setSize(50, 20);
		button1.setHorizontalTextPosition(AbstractButton.LEADING);
		button1.setVerticalTextPosition(AbstractButton.CENTER);
		
		frame.add(text);
		frame.add(button1);
		button1.addActionListener(this);
		
		frame.setVisible(true);
		
	}

	public void actionPerformed(ActionEvent arg0) {
		
		Helper.ServerAddress = text.getText();
		
		frame.dispose();
		
		System.out.println("Now :" + Helper.ServerAddress);
		
		Helper.modApplicationContext();

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
