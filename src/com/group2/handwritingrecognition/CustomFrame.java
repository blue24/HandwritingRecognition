package com.group2.handwritingrecognition;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;



public class CustomFrame extends JFrame{
	
	private static final long serialVersionUID = -5763319334266549575L;
	
	public CustomPanel pan;
	public DrawablePanel drawSubPan;
	
	public JButton btnConfirm;
	public JButton btnClear;
	
	public JLabel lblMessage;
	
	

	
	
	
	
	
	
	
	
	
	
	
	
	Font fntSansSerif;
	
	
	public CustomFrame(int defaultSize_x, int defaultSize_y){
		
		fntSansSerif = new Font("SanSerif", Font.PLAIN, 20);
		
		
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		pan = new CustomPanel();
		
		pan.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		
		
		pan.setLayout(new GridBagLayout());
		
		
		
		pan.setPreferredSize(new Dimension(defaultSize_x, defaultSize_y));
		
		//add(pan);
		
		drawSubPan = new DrawablePanel();
		drawSubPan.setPreferredSize(new Dimension(500, 150) );
		
		
		
		lblMessage = new JLabel();
		lblMessage.setText("Type \"1\" and press \"confirm\".");
		lblMessage.setFont(fntSansSerif);
		
		//lblMessage.setAlignmentX(RIGHT_ALIGNMENT);
		//?
		
		
		
		
		
		//pan.add(drawSubPan); 
		Static.addGridBagConstraintsComp(GridBagConstraints.BOTH, GridBagConstraints.PAGE_START, 1, 2, 2, 1, 0.7,  1.0, 0, 0, pan, drawSubPan);
		
		
		btnConfirm = new JButton();
		btnConfirm.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				confirmClicked();
			}//END OF actionPerformed(...)
		});
		btnConfirm.setText("Confirm");
		btnConfirm.setFont(fntSansSerif);
		
		
		btnClear = new JButton();
		btnClear.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e){
				clearClicked();
			}//END OF actionPerformed(...)
		});
		btnClear.setText("Clear");
		btnClear.setFont(fntSansSerif);
		
		
		//pan.add(drawSubPan); 
		Static.addGridBagConstraintsComp(GridBagConstraints.BOTH, GridBagConstraints.PAGE_START, 1, 1, 1, 1, 0.7, 0.0, 0, 0, pan, lblMessage);
				

		Static.addGridBagConstraintsComp(GridBagConstraints.BOTH, GridBagConstraints.PAGE_START, 1, 3, 1, 1, 0.5, 0.0, 0, 0, pan, btnClear);
		Static.addGridBagConstraintsComp(GridBagConstraints.BOTH, GridBagConstraints.PAGE_START, 2, 3, 1, 1, 0.5, 0.0, 0, 0, pan, btnConfirm);
		
		
		
		//Static.addGridBagConstraintsComp(GridBagConstraints.BOTH, GridBagConstraints.PAGE_START, 1, 0, 1, 1, 0.2, 0.0, 0, 0, panel, txtPage);
		
		
		
		this.getContentPane().add(pan);

		this.setResizable(false);
		this.pack();
		
		
		drawSubPan.createPixelsArray();
		//this.setPreferredSize(new Dimension(800, 600));
		this.setVisible(true);
		
	}
	
	
	void confirmClicked(){
		
	}
	
	void clearClicked(){
		
		drawSubPan.clearContents();
	}
	
	
	
	
	
	
	
	
	
	
}
