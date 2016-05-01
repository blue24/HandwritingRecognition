package com.group2.handwritingrecognition;

import java.awt.Graphics;
import java.awt.Transparency;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class SingleSamplePanel extends JPanel{
	
	private static final long serialVersionUID = -5981963683430142506L;
	
	boolean[][] mySample = null;
	
	public SingleSamplePanel(){
		super();
		
	}
	
	
	public BufferedImage receiveSampleAsImage(){
		
		BufferedImage b = new BufferedImage(Static.groupPixelsWidth, Static.groupPixelsHeight, Transparency.OPAQUE);
		b.getGraphics().setColor(Static.clrWhite);
		b.getGraphics().fillRect(0,  0,  Static.groupPixelsWidth, Static.groupPixelsHeight);
		
		
		if(mySample != null){
			drawSampleTo(b.getGraphics());
		}else{
			//nothing, leave white.
		}
		return b;
	}
	
	public void drawSampleTo(Graphics g){
		g.setColor(Static.clrBlack);
		for(int y = 0; y < Static.groupPixelsHeight; y++){
			for(int x = 0; x < Static.groupPixelsWidth; x++){
				if(mySample[y][x] == true){
					g.drawLine(x, y, x, y);
				}
			}
		}
	}//END OF drawSampleTo(...)
	
	
	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		g.setColor(Static.clrWhite);
		g.clearRect(0,  0,  getWidth(), getHeight());
		
		//g.setColor(Static.clrBlack);
		if(mySample != null){
			drawSampleTo(g);
		}
		
		
	}//END OF paintComponent
	
	
	
	
	
}
