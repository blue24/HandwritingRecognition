package com.group2.handwritingrecognition;

import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;

public class DrawablePanel extends JPanel implements ComponentListener {
	
	Color clrWhite;
	Color clrBlack;
	boolean[][] pixels;
	int pixelsWidth;
	int pixelsHeight;
	
	
	int prevDrag_x = -1;
	int prevDrag_y = -1;
	
	
	public DrawablePanel(){
		clrWhite = new Color(255, 255, 255);
		clrBlack = new Color(0, 0, 0);
		
		
		this.addMouseMotionListener(new MouseMotionListener(){

			@Override
			public void mouseDragged(MouseEvent arg0) {
				
				onMouseDrag(arg0.getX(), arg0.getY());
				
			}

			@Override
			public void mouseMoved(MouseEvent arg0) {
				
			}
			
		}); 
		
		
		this.addMouseListener(new MouseListener(){

			@Override
			public void mouseClicked(MouseEvent arg0) {
				
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				onMouseClicked(arg0.getX(), arg0.getY());
				
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				onMouseReleased(arg0.getX(), arg0.getY());
			}
			
			
		});

		
		
	}
	
	
	@Override
	public void paint(Graphics g){
		g.setColor(clrWhite);
		//g.drawLine(0,  0,  15,  15);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		g.setColor(clrBlack);
		if(pixels != null){
			for(int y = 0; y < pixelsHeight; y++){
				for(int x = 0; x < pixelsWidth; x++){
					if(pixels[y][x] == true){
						g.drawLine(x, y, x, y);
					}
				}
			}
		}
		
		
	}
	
	void createPixelsArray(){
		
		int targetWidth = getWidth();
		int targetHeight = getHeight();
		
		pixelsWidth = targetWidth;
		pixelsHeight = targetHeight;
		
		pixels = new boolean[pixelsHeight][pixelsWidth];
		
	}
	
	void onMouseReleased(int x, int y){
		
		//exact same logic, routing to "onMouseDrag"
		onMouseDrag(x, y);
		
		//Also, invalidate "prevDrag" x & y.  Forget about the last drag frame's position.
		prevDrag_x = -1;
		prevDrag_y = -1;
		
		
		
		
	}
	
	
	void onMouseClicked(int x, int y){
		//exact same logic, routing to "onMouseDrag".
		onMouseDrag(x, y);
		
		
	}
	
	
	void onMouseDrag(int x, int y){
		
		//int myWidth = getWidth();
		//int myHeight = getHeight();
		
		
		if(pixels != null && x >= 0 && y >= 0 && x < pixelsWidth && y < pixelsHeight){
			//System.out.println("YES " + mouse_x + " " + mouse_y);
			
			if(prevDrag_x == -1 || prevDrag_y == -1){
				//just color the current point.
				pixels[y][x] = true;
				
			}else{
				//Otherwise, color a line from the previously dragged-on point to here.
				drawLine(prevDrag_x, prevDrag_y, x, y);
			}
			repaint();
			
			prevDrag_x = x;
			prevDrag_y = y;
			
		}//END OF if(pixels != null && x >= 0 && ...)
		else{
			//Dragging outside of the bounds of the window (at least the size of the "pixels"
			//array) counts as "releasing" the mouse for all intents and purposes:
			if(prevDrag_x != -1 && prevDrag_y != -1){
				onMouseDrag(prevDrag_x, prevDrag_y);
				prevDrag_x = -1;
				prevDrag_y = -1;
			}
		}
		
		
	}
	
	

	//CREDIT TO http://tech-algorithm.com/articles/drawing-line-using-bresenham-algorithm/
	public void drawLine(int x,int y,int x2, int y2) {
	    int w = x2 - x ;
	    int h = y2 - y ;
	    int dx1 = 0, dy1 = 0, dx2 = 0, dy2 = 0 ;
	    if (w<0) dx1 = -1 ; else if (w>0) dx1 = 1 ;
	    if (h<0) dy1 = -1 ; else if (h>0) dy1 = 1 ;
	    if (w<0) dx2 = -1 ; else if (w>0) dx2 = 1 ;
	    int longest = Math.abs(w) ;
	    int shortest = Math.abs(h) ;
	    if (!(longest>shortest)) {
	        longest = Math.abs(h) ;
	        shortest = Math.abs(w) ;
	        if (h<0) dy2 = -1 ; else if (h>0) dy2 = 1 ;
	        dx2 = 0 ;            
	    }
	    int numerator = longest >> 1 ;
	    for (int i=0;i<=longest;i++) {
	        //g.drawLine(x,y, x, y) ;
	    	pixels[y][x] = true;
	    	
	    	numerator += shortest ;
	        if (!(numerator<longest)) {
	            numerator -= longest ;
	            x += dx1 ;
	            y += dy1 ;
	        } else {
	            x += dx2 ;
	            y += dy2 ;
	        }
	    }
	}
	
	
	
	void clearContents(){
		if(pixels != null){
			
			for(int y = 0; y < pixelsHeight; y++){
				for(int x = 0; x < pixelsWidth; x++){
					pixels[y][x] = false;
				}
			}
			repaint();
		}
	}
	
	

	@Override
	public void componentHidden(ComponentEvent arg0) {
		
	}

	@Override
	public void componentMoved(ComponentEvent arg0) {
		
	}

	@Override
	public void componentResized(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		//Do if we plan on supporting window resizes.
		
	}

	@Override
	public void componentShown(ComponentEvent arg0) {
		
	}
	
	

}
