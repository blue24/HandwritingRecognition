package com.group2.handwritingrecognition;

import java.awt.Graphics;

import javax.swing.JPanel;

public class SingleSamplePanel extends JPanel{
	
	private static final long serialVersionUID = -5981963683430142506L;
	
	boolean[][] mySample = null;
	
	public SingleSamplePanel(){
		super();
		
	}
	
	
	
	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		g.clearRect(0,  0,  getWidth(), getHeight());
		
		g.setColor(Static.clrBlack);
		if(mySample != null){
			for(int y = 0; y < Static.groupPixelsHeight; y++){
				for(int x = 0; x < Static.groupPixelsWidth; x++){
					if(mySample[y][x] == true){
						g.drawLine(x, y, x, y);
					}
				}
			}
		}
		
		
	}//END OF paintComponent
	
	
	
	
	
}
