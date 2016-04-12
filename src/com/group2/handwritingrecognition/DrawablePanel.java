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
	

	
	
	
	boolean[][] pixels;
	int pixelsWidth;
	int pixelsHeight;
	

	boolean[][] groupPixels;
	
	
	int prevDrag_x = -1;
	int prevDrag_y = -1;
	
	
	float drawRadius = 2f;
	
	boolean drawnYet = false;
	
	
	
	
	int pixelDrawMinY = 999999;
	int pixelDrawMaxY = -1;
	int pixelDrawMinX = 999999;
	int pixelDrawMaxX = -1;
	
	
	//A reference back to the window.
	CustomFrame frameRef; 
	
	
	
	
	public DrawablePanel(CustomFrame arg_frameRef){
		super();
		
		frameRef = arg_frameRef;
		
		/*
		Thread thr = new Thread(){
			
			@Override
			public void run(){
				
				while(true){
					
					if(shouldRepaint){
						NEVER MIND
					}
					
					try {
						Thread.sleep(17);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
				}
				
			}
		};
		thr.start();
		*/
		
		
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
		g.setColor(Static.clrWhite);
		//g.drawLine(0,  0,  15,  15);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		g.setColor(Static.clrBlack);
		if(pixels != null){
			for(int y = 0; y < pixelsHeight; y++){
				for(int x = 0; x < pixelsWidth; x++){
					if(pixels[y][x] == true){
						g.drawLine(x, y, x, y);
					}
				}
			}
		}
		
		
		if(Static.drawDebug){
			drawSectors(g);
			
			g.setColor(Static.clrRed);
			drawSquare(g, pixelDrawMinX, pixelDrawMinY, pixelDrawMaxX, pixelDrawMaxY);
		}
		
		
		/*
		g.drawLine(pixelDrawMinX, pixelDrawMinY, pixelDrawMaxX, pixelDrawMinY);
		
		g.drawLine(pixelDrawMinX, pixelDrawMaxY, pixelDrawMaxX, pixelDrawMaxY);
		
		
		g.drawLine(pixelDrawMinX, pixelDrawMinY, pixelDrawMinX, pixelDrawMaxY);
		
		g.drawLine(pixelDrawMaxX, pixelDrawMinY, pixelDrawMaxX, pixelDrawMaxY);
		*/
		
		
		
		
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
	
	
	
	
	void drawSquare(Graphics g, int x1, int y1, int x2, int y2){
		
		
		
		g.drawLine(x1, y1, x2, y1);
		g.drawLine(x1, y2, x2, y2);
		
		g.drawLine(x1, y1, x1, y2);
		g.drawLine(x2, y1, x2, y2);
		
		
	}
	
	
	void drawSectors(Graphics g){
		
		/*
		if(groupPixels == null){
			//can't draw it then.
			return;
		}
		*/

		int usedBoundsWidth = pixelDrawMaxX - pixelDrawMinX;
		int usedBoundsHeight = pixelDrawMaxY - pixelDrawMinY;
		
		
		float sectorWidth = ((float)usedBoundsWidth / (float)Static.groupPixelsWidth);
		float sectorHeight = ((float)usedBoundsHeight / (float)Static.groupPixelsHeight);
		
		g.setColor(Static.clrGreen);
		
		
		
		
		int drawnPixels = 0;
		int blankPixels = 0;
		
		for(int gY = 0; gY < Static.groupPixelsHeight; gY++){
			for(int gX = 0; gX < Static.groupPixelsWidth; gX++){
				
				//Counting these per sector, reset when checking a new sector...
				drawnPixels = 0;
				blankPixels = 0;
				
				
				
				for(int y = 0; y < sectorHeight; y++){
					for(int x = 0; x < sectorWidth; x++){
						
						int trueX = pixelDrawMinX + (int) (gX*sectorWidth) + x;
						int trueY = pixelDrawMinY + (int) (gY*sectorHeight) + y;
						
						if(pixels[trueY][trueX] == true){
							drawnPixels++;
						}else{
							blankPixels++;
						}
						
					}
				}
				
				
				int offX = pixelDrawMinX + (int)(gX*sectorWidth);
				int offY = pixelDrawMinY + (int)(gY*sectorHeight);
				
				g.setColor(Static.clrGreen);
				drawSquare(g, offX, offY, offX + (int)sectorWidth, offY + (int)sectorHeight);
				
				
				float drawnRatio = (float)drawnPixels / (float)(drawnPixels + blankPixels);
				
				if(drawnPixels >= Static.pixelsRequiredInSector || drawnRatio >= Static.sectorRegisterRatio){
					//groupPixels[gY][gX] = true;
					Color eh = new Color(0, 255, 255, 185);
					g.setColor(eh);
					g.fillRect(offX, offY, (int)sectorWidth, (int)sectorHeight);
					
				}else{
					//groupPixels[gY][gX] = false;
				}
				
				
				
				
				
				
				/*
				int offX = pixelDrawMinX + (int)(gX*sectorWidth);
				int offY = pixelDrawMinY + (int)(gY*sectorHeight);
				
				g.setColor(Static.clrGreen);
				drawSquare(g, offX, offY, offX + (int)sectorWidth, offY + (int)sectorHeight);
				
				if(groupPixels[gY][gX] == true){
					
					
					Color eh = new Color(0, 255, 255, 185);
					g.setColor(eh);
					g.fillRect(offX, offY, (int)sectorWidth, (int)sectorHeight);
					
					
					//drawLine(offX, offY, offX + sectorWidth, offY + sectorHeight);
				}
				
				*/
				//groupPixels[gY][gX];
				
				
			}//END OF for(int gX = 0...)
			
		}//END OF for(int gY = 0...)
		
		
		
	}
	
	
	
	boolean[][] attemptRead(){
		
		if(drawnYet){
			groupPixels = new boolean[Static.groupPixelsHeight][Static.groupPixelsWidth];

			/*
			pixelDrawMinY = 999999;
			pixelDrawMaxY = -1;
			pixelDrawMinX = 999999;
			pixelDrawMaxX = -1;
			*/
			
			int usedBoundsWidth = pixelDrawMaxX - pixelDrawMinX;
			int usedBoundsHeight = pixelDrawMaxY - pixelDrawMinY;
			
			
			float sectorWidth = ((float)usedBoundsWidth / (float)Static.groupPixelsWidth);
			float sectorHeight = ((float)usedBoundsHeight / (float)Static.groupPixelsHeight);
			
			int blankPixels = 0;
			int drawnPixels = 0;
			
			for(int gY = 0; gY < Static.groupPixelsHeight; gY++){
				for(int gX = 0; gX < Static.groupPixelsWidth; gX++){
					
					//Counting these per sector, reset when checking a new sector...
					drawnPixels = 0;
					blankPixels = 0;
					for(int y = 0; y < sectorHeight; y++){
						for(int x = 0; x < sectorWidth; x++){
							
							int trueX = pixelDrawMinX + (int) (gX*sectorWidth) + x;
							int trueY = pixelDrawMinY + (int) (gY*sectorHeight) + y;
							
							if(pixels[trueY][trueX] == true){
								drawnPixels++;
							}else{
								blankPixels++;
							}
							
						}
					}
					
					float drawnRatio = (float)drawnPixels / (float)(drawnPixels + blankPixels);
					
					if(drawnPixels >= Static.pixelsRequiredInSector || drawnRatio >= Static.sectorRegisterRatio){
						groupPixels[gY][gX] = true;
					}else{
						groupPixels[gY][gX] = false;
					}
					
				}//END OF for(int gX = 0...)
				
			}//END OF for(int gY = 0...)
			
			

			this.repaint();
			
			return groupPixels;
		}else{
			
			frameRef.showErrorMessage("ERROR: Haven\'t drawn anything yet!");
			return null;
			
		}//END OF else OF if(drawnYet)

		
	}//END OF attemptRead()
	
	
	void onMouseClicked(int x, int y){
		//exact same logic, routing to "onMouseDrag".
		onMouseDrag(x, y);
		
		
	}
	
	
	void drawThickPoint(int targetX, int targetY){
		
		//The user has drawn since the screen was cleared.
		
		
		
		
		
		/*
		for(int y = -2; y < 2; y++){
			for(int x = -2; x < 2; x++ ){
				
				
				int currentY = targetY + y;
				int currentX = targetX + x;
				
				if(currentX >= 0 && currentY >= 0 && currentX < pixelsWidth && currentY < pixelsHeight ){
					pixels[currentY][currentX] = true;
				}
				
				
			}
		
		}
		*/
		
		


		int pointFurthestLeft = (int) Math.round(targetX - drawRadius);
		int pointFurthestRight = (int) Math.round(targetX + drawRadius);
		
		int pointFurthestUp = (int) Math.round(targetY - drawRadius);
		int pointFurthestDown = (int) Math.round(targetY + drawRadius);
		
		
		if(pointFurthestLeft >= pixelsWidth ||
			pointFurthestRight < 0 ||
			pointFurthestUp >= pixelsHeight ||
			pointFurthestDown < 0)
		{
			//The entire point is guaranteed out of bounds.  Return (no points rendered)!
			return;
		}
		
		
		

		if(pointFurthestLeft < 0){
			pointFurthestLeft = 0;
		}
		if(pointFurthestRight >= pixelsWidth){
			pointFurthestRight = pixelsWidth - 1;
		}
		if(pointFurthestUp < 0){
			pointFurthestUp = 0;
		}
		if(pointFurthestDown >= pixelsHeight){
			pointFurthestDown = pixelsHeight - 1;
		}
		
		
		
		if(pointFurthestLeft < pixelDrawMinX){
			pixelDrawMinX = pointFurthestLeft;
		}
		
		if(pointFurthestRight > pixelDrawMaxX){
			pixelDrawMaxX = pointFurthestRight;
		}
		
		if(pointFurthestUp < pixelDrawMinY){
			pixelDrawMinY = pointFurthestUp;
		}
		
		if(pointFurthestDown > pixelDrawMaxY){
			pixelDrawMaxY = pointFurthestDown;
		}
		
		
		
		
		drawnYet = true;
		
		
		float drawRadiusSquared = (float) Math.pow(drawRadius, 2);
		
		float unitSize = 1f;
		
		for(float x = -drawRadius; x <= drawRadius; x+= unitSize){
			
			float y = (float)Math.sqrt(  -Math.pow(x, 2) + drawRadiusSquared);
			
			
			if(!Float.isNaN(y)){
			
				int currentY = Math.round(targetY + y);
				int currentY2 = Math.round(targetY - y);
				int currentX = Math.round(targetX + x);
				
				if(currentX  >= 0 && currentX < pixelsWidth){
					for(int fillY = currentY2; fillY <= currentY; fillY++){
						
						if(fillY >= 0 && fillY < pixelsHeight ){
							pixels[fillY][currentX] = true;
						}
						
					}
				}
				
				/*
				if(currentY == 0){
					System.out.println("REPORT: " + y);
				}
				*/
				
			}//END OF if(!Float.isNaN(y))
			
		}//END OF for(float x = ...)
		
		//y = +- root(-x^2 + 2)
		
	}
	
	
	
	void onMouseDrag(int x, int y){
		
		//int myWidth = getWidth();
		//int myHeight = getHeight();
		
		
		if(pixels != null && x >= 0 && y >= 0 && x < pixelsWidth && y < pixelsHeight){
			//System.out.println("YES " + mouse_x + " " + mouse_y);
			
			if(prevDrag_x == -1 || prevDrag_y == -1){
				//just color the current point.
				//pixels[y][x] = true;
				//System.out.println("CALLED");
				drawThickPoint(x, y);
				
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
	    	
	    	drawThickPoint(x, y);
	    	
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
		drawnYet = false;
		//The user has not drawn since the screen was cleared as of now.
		
		
		pixelDrawMinY = 999999;
		pixelDrawMaxY = -1;
		pixelDrawMinX = 999999;
		pixelDrawMaxX = -1;
		
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
