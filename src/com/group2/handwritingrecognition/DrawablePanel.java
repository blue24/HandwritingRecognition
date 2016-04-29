package com.group2.handwritingrecognition;

import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JPanel;

public class DrawablePanel extends JPanel {
	

	int[] drawTrialManagerNumberCoordsX;
	int[] drawTrialManagerNumberCoordsY;
	
	
	boolean[][] pixels;
	int pixelsWidth;
	int pixelsHeight;
	
	int trialManagerIndex = -1;
	
	long startLoadTime = 0;
	
	int totalTrials = 0;
	int trialsDone = 0;
	
	int loadTimes = 0;
	
	int connectionsToDo = 0;
	int connectionsDoneYet = 0;
	
	boolean currentlyTraining = false;
	boolean buildingNeuralNetwork = false;
	boolean disableDrawing = false;
	
	Calendar cal = Calendar.getInstance();
	//DateFormat dateFormat = new SimpleDateFormat("dd HH:mm:ss");
	
	
	boolean[][] groupPixels;
	
	
	int prevDrag_x = -1;
	int prevDrag_y = -1;
	
	
	float drawRadius = 2f;
	
	boolean drawnYet = false;
	
	
	int pixelDrawMinY = Integer.MAX_VALUE;
	int pixelDrawMaxY = Integer.MIN_VALUE;
	int pixelDrawMinX = Integer.MAX_VALUE;
	int pixelDrawMaxX = Integer.MIN_VALUE;
	
	float extraPixelDrawXPadding = 0;
	float extraPixelDrawYPadding = 0;
	
	
	//A reference back to the window.
	//NOTICE: currently unused.  Remove entirely?
	CustomFrame frameRef; 
	
	boolean resizedSince = false;

	
	
	public void attemptResizePixelArray(){
		attemptResizePixelArray(true);
	}
	
	public void attemptResizePixelArray(boolean canCallResizeActHandle){
			
			int testWidth = getWidth();
			int testHeight = getHeight();
			if(testWidth != pixelsWidth || testHeight != pixelsHeight){
				//Resize the pixels array.
				
				System.out.println("PIXELS RESIZED");
				
				
				boolean[][] newPixels = new boolean[testHeight][testWidth];
				
				
				for(int y = 0; y < pixelsHeight && y < testHeight; y++){
					for(int x = 0; x < pixelsWidth && x < testWidth; x++){
						newPixels[y][x] = pixels[y][x];
						
					}
				}
				
				pixelsWidth = testWidth;
				pixelsHeight = testHeight;
				pixels = newPixels;
				
				if(pixelDrawMaxX >= pixelsWidth){
					pixelDrawMaxX = pixelsWidth - 1;
				}
				if(pixelDrawMaxY >= pixelsHeight){
					pixelDrawMaxY = pixelsHeight - 1;
				}
				
				
				if(canCallResizeActHandle){
					frameRef.resizeActHandle();
				}
				
				repaint();
			}
		
			resizedSince = false;
			//Not resized since just now.
		
	}
	
	boolean endPaintUpdate = false;
	Thread paintUpdate;
	
	public DrawablePanel(CustomFrame arg_frameRef){
		super();
		
		frameRef = arg_frameRef;
		drawTrialManagerNumberCoordsX = new int[10];
		drawTrialManagerNumberCoordsY = new int[10];
		
		
		final DrawablePanel refBack = this;
		
		this.addComponentListener(new ComponentListener(){
			@Override
			public void componentHidden(ComponentEvent arg0) {
				
			}
			@Override
			public void componentMoved(ComponentEvent arg0) {
				
			}
			@Override
			public void componentResized(ComponentEvent arg0) {
				resizedSince = true;
				
			}
			@Override
			public void componentShown(ComponentEvent arg0) {
				
			}
		});
		
		
		this.addMouseMotionListener(new MouseMotionListener(){
			@Override
			public void mouseDragged(MouseEvent arg0) {
				if(!disableDrawing){
				onMouseDrag(arg0.getX(), arg0.getY());
				}
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
				if(resizedSince){
					attemptResizePixelArray();
					
				}
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
	
	public void startPaintUpdateThread(){
		paintUpdate = new Thread(){
			@Override
			public void run(){
				
				while(!endPaintUpdate){
					repaint();
					try {
						Thread.sleep(17);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
				}
				
			}
		};
		paintUpdate.start();
	}
	
	@Override
	public void paintComponent (Graphics g){
		
		super.paintComponent (g);
		
		int currentWidth = getWidth();
		
		g.setFont(Static.fntSansSerif);
		
		g.setColor(Static.clrOffWhite);
		//g.drawLine(0,  0,  15,  15);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		
		if(currentlyTraining || buildingNeuralNetwork){
			
			int loadBarWidth = (int) (currentWidth * 0.8f);
			int loadBarHeight = 36;
			
			int loadBarStartX = (currentWidth - loadBarWidth) / 2 ;
			int loadBarStartY = (getHeight() - loadBarHeight) / 2;
			
			g.setColor(Static.clrRed);
			g.fillRect(loadBarStartX, loadBarStartY, loadBarWidth, loadBarHeight);
			
			String loadBarText = null;
			float fraction = 0;
			
			
			if(buildingNeuralNetwork){
				fraction = (float)connectionsDoneYet / (float)connectionsToDo;
				g.setColor(Static.clrWhite);
				loadBarText = "Connections made: " + connectionsDoneYet + " / " + connectionsToDo;
				
			}else if(currentlyTraining){
				fraction = (float)trialsDone / (float)totalTrials;
				loadBarText = "Trials done: " + trialsDone + " / " + totalTrials;
			}
			
			int loadBarDoneWidth = (int) ( (fraction) * (loadBarWidth-6)  ) ;
			
			g.setColor(Static.clrGreen);
			g.fillRect(loadBarStartX + 3, loadBarStartY + 3, loadBarDoneWidth, loadBarHeight-6);
			
			g.setColor(Static.clrWhite);
			g.drawString(loadBarText , (currentWidth - g.getFontMetrics().stringWidth(loadBarText))/2, loadBarStartY + 23);
			
			
			long timePassed = System.currentTimeMillis() - startLoadTime;
			
			String dateString = null;
			
			if(fraction == 0 || timePassed == 0){
				g.setColor(Static.clrBlack);
				dateString = "???";
				
			}else if(timePassed < 7000){
				
				g.setColor(Static.clrBlack);
				dateString = "Estimating...";
				
			}else{
				

				long ETA = (long) ((timePassed / fraction) - timePassed);

				//CREDIT: http://stackoverflow.com/questions/4863658/how-to-get-system-time-in-java-without-creating-a-new-date
				cal.setTimeInMillis(ETA);
				//Date tempDate = cal.getTime();
				int hours = 0;
				int minutes = 0;
				int seconds = 0;
				
				hours = (int)(ETA / 3600000l);
				ETA -= hours * 3600000l;
				minutes = (int)(ETA / 60000l);
				ETA -= minutes * 60000l;
				seconds = (int)(ETA / 1000l);
				ETA -= seconds * 1000l;
				
				if(hours > 0){
					dateString = hours + " hr, " + minutes + " m";
				}else if(minutes > 10){
					dateString = minutes + " m";
				}else if(minutes > 0){
					dateString = minutes + " m, " + seconds + " s";
				}else{
					dateString = seconds + " s";
				}
				
			}//END OF else statement
			
			g.setColor(Static.clrBlack);
			g.drawString("ETA: " + dateString, loadBarStartX + 40, loadBarStartY + 60 );
			
			/*
			g.setColor(Static.clrBlack);
			g.drawString("test1: " + timePassed, loadBarStartX + 40, loadBarStartY + 90 );
			g.setColor(Static.clrBlack);
			g.drawString("test2: " + ETA, loadBarStartX + 40, loadBarStartY + 120 );
			*/
			//Attempt nothing else if the loading bar is being drawn.
			return;
		}
		
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
			
			int toDrawX1 = (int)(pixelDrawMinX - extraPixelDrawXPadding);
			int toDrawX2 = (int)(pixelDrawMaxX + extraPixelDrawXPadding);
			int toDrawY1 = (int)(pixelDrawMinY - extraPixelDrawYPadding);
			int toDrawY2 = (int)(pixelDrawMaxY + extraPixelDrawYPadding);
			
			g.setColor(Static.clrRed);
			drawSquareCheap(g, toDrawX1, toDrawY1, toDrawX2, toDrawY2);
		}
		
		if(trialManagerIndex == 1){
			
			if(frameRef.drawSpecialRect == true){
				
				g.setColor(Static.clrRed);
				drawSquareCheap(g, frameRef.specialRectx1, frameRef.specialRecty1, frameRef.specialRectx2, frameRef.specialRecty2);
				drawSquareCheap(g, frameRef.specialRectx1 + 1, frameRef.specialRecty1 + 1, frameRef.specialRectx2 - 1, frameRef.specialRecty2- 1);
			}
			
		}else if(trialManagerIndex == 0){
			g.setColor(Static.clrBlack);
			g.setFont(Static.fntSansSerifBig);
			int fontHeight = g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent();
			for(int i = 0; i < 10; i++){
				String toDraw = String.valueOf(i);
				g.drawString( toDraw, drawTrialManagerNumberCoordsX[i] - g.getFontMetrics().stringWidth(toDraw)/2, drawTrialManagerNumberCoordsY[i] + (fontHeight)/2  );
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
		
		if(!disableDrawing){
		//exact same logic, routing to "onMouseDrag"
		onMouseDrag(x, y);
		}
		
		//Also, invalidate "prevDrag" x & y.  Forget about the last drag frame's position.
		prevDrag_x = -1;
		prevDrag_y = -1;
	}
	
	void drawSquare(int x1, int y1, int x2, int y2){
		
		if(x1 < 0){
			x1 = 0;
		}
		if(x2 >= pixelsWidth){
			x2 = pixelsWidth-1;
		}
		
		if(y1 < 0){
			y1 = 0;
		}
		if(y2 >= pixelsHeight){
			y2 = pixelsHeight-1;
		}
		
		drawLine(x1, y1, x2, y1);
		drawLine(x1, y2, x2, y2);
		
		drawLine(x1, y1, x1, y2);
		drawLine(x2, y1, x2, y2);
		
	}
	
	void drawSquareCheap(Graphics g, int x1, int y1, int x2, int y2){
		
		if(x1 < 0){
			x1 = 0;
		}
		if(x2 >= pixelsWidth){
			x2 = pixelsWidth-1;
		}
		
		if(y1 < 0){
			y1 = 0;
		}
		if(y2 >= pixelsHeight){
			y2 = pixelsHeight-1;
		}
		
		g.drawLine(x1, y1, x2, y1);
		g.drawLine(x1, y2, x2, y2);
		
		g.drawLine(x1, y1, x1, y2);
		g.drawLine(x2, y1, x2, y2);
		
	}
	
	boolean boundsCheck(int x, int y){
		if(x >= 0 && y >= 0 && x < pixelsWidth && y < pixelsHeight){
			return true;
		}else{
			return false;
		}
	}
	
	void drawSectors(Graphics g){
		
		if(pixels == null || drawnYet == false){
			//can't draw it then.
			return;
		}
		
		int usedBoundsWidth = pixelDrawMaxX - pixelDrawMinX + (int)(extraPixelDrawXPadding * 2);
		int usedBoundsHeight = pixelDrawMaxY - pixelDrawMinY + (int)(extraPixelDrawYPadding * 2);
		
		int leftStart = (int)(pixelDrawMinX - extraPixelDrawXPadding);
		int topStart = (int)(pixelDrawMinY - extraPixelDrawYPadding);
		
		
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
						
						int trueX = leftStart + (int) (gX*sectorWidth) + x;
						int trueY = topStart + (int) (gY*sectorHeight) + y;
						
						if(boundsCheck(trueX, trueY) && pixels[trueY][trueX] == true){
							drawnPixels++;
						}else{
							blankPixels++;
						}
						
					}
				}
				
				int offX = leftStart + (int)(gX*sectorWidth);
				int offY = topStart + (int)(gY*sectorHeight);
				
				g.setColor(Static.clrGreenTrans);
				drawSquareCheap(g, offX, offY, offX + (int)sectorWidth, offY + (int)sectorHeight);
				
				float drawnRatio = (float)drawnPixels / (float)(drawnPixels + blankPixels);
				
				if(drawnPixels >= Static.pixelsRequiredInSector || drawnRatio >= Static.sectorRegisterRatio){
					//groupPixels[gY][gX] = true;
					g.setColor(Static.clrCyanTrans);
					g.fillRect(offX, offY, (int)sectorWidth, (int)sectorHeight);
					
				}else{
					//groupPixels[gY][gX] = false;
				}
				
			}//END OF for(int gX = 0...)
		}//END OF for(int gY = 0...)
	}
	
	
	boolean[][] attemptRead(){
		
		if(drawnYet){
			groupPixels = new boolean[Static.groupPixelsHeight][Static.groupPixelsWidth];
			
			int usedBoundsWidth = pixelDrawMaxX - pixelDrawMinX + (int)(extraPixelDrawXPadding * 2);
			int usedBoundsHeight = pixelDrawMaxY - pixelDrawMinY + (int)(extraPixelDrawYPadding * 2);
			
			int leftStart = (int)(pixelDrawMinX - extraPixelDrawXPadding);
			int topStart = (int)(pixelDrawMinY - extraPixelDrawYPadding);
			
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
							
							int trueX = leftStart + (int) (gX*sectorWidth) + x;
							int trueY = topStart + (int) (gY*sectorHeight) + y;
							
							if(boundsCheck(trueX, trueY) && pixels[trueY][trueX] == true){
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
			return null;
		}//END OF else OF if(drawnYet)
	}//END OF attemptRead()
	
	
	void onMouseClicked(int x, int y){
		//exact same logic, routing to "onMouseDrag".
		if(!disableDrawing){
			onMouseDrag(x, y);
		}
		
		//System.out.println("THE PLACE " + " " + x + " " + y);
		if(!disableDrawing && trialManagerIndex != -1){
			//If in the trial manager, send this click to frameRef.
			frameRef.drawSubPanClicked(x, y);
		}
	}
	
	void drawThickPoint(int targetX, int targetY){
		
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
		
		
		int currentBoundsWidth = pixelDrawMaxX - pixelDrawMinX;
		if(currentBoundsWidth < Static.boundsMinimumWidth){
			extraPixelDrawXPadding = (Static.boundsMinimumWidth - currentBoundsWidth) / 2f;
			//pixelDrawMinX -= (Static.boundsMinimumWidth - currentBoundsWidth) / 2;
			//pixelDrawMaxX += (Static.boundsMinimumWidth - currentBoundsWidth) / 2;
			
			
		}else{
			extraPixelDrawXPadding = 0;
		}
		int currentBoundsHeight = pixelDrawMaxY - pixelDrawMinY;
		if(currentBoundsHeight < Static.boundsMinimumHeight){
			extraPixelDrawYPadding = (Static.boundsMinimumHeight - currentBoundsHeight) / 2f;
		}else{
			extraPixelDrawYPadding = 0;
		}
		
		int targetWidth = (pixelDrawMaxX - pixelDrawMinX) + (int)(2 * extraPixelDrawXPadding);
		int targetHeight = (pixelDrawMaxY - pixelDrawMinY) + (int)(2 * extraPixelDrawYPadding);
		
		
		if((float)targetWidth / (float)targetHeight < Static.boundsRatioMin){
			//Too tall!  Increase the width to make this ratio acceptable.
			float newWidth = Static.boundsRatioMin * targetHeight;
			
			extraPixelDrawXPadding = (newWidth - (pixelDrawMaxX - pixelDrawMinX) )/2;
			
		}else if((float)targetWidth / (float)targetHeight > Static.boundsRatioMax){
			//Too long! Increase the height to make this ratio acceptable.
			float newHeight = targetWidth / Static.boundsRatioMax;
			extraPixelDrawYPadding = (newHeight - (pixelDrawMaxY - pixelDrawMinY) )/2;
		}
		
		targetWidth = (pixelDrawMaxX - pixelDrawMinX) + (int)(2 * extraPixelDrawXPadding);
		targetHeight = (pixelDrawMaxY - pixelDrawMinY) + (int)(2 * extraPixelDrawYPadding);
		
		
		//System.out.println("SIZE : " + targetWidth + " " + targetHeight);
		//System.out.println("RATIO IS " + ((float)targetWidth / (float)targetHeight)  );
		
		
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
		
		if(trialManagerIndex != -1){
			//cannot draw in the trial manager.
			return;
		}
		
		if(pixels != null && x >= 0 && y >= 0 && x < pixelsWidth && y < pixelsHeight){
			//System.out.println("YES " + mouse_x + " " + mouse_y);
			
			if(prevDrag_x == -1 || prevDrag_y == -1){
				//just color the current point.
				drawThickPoint(x, y);
				
			}else{
				//Otherwise, color a line from the previously dragged-on point to here.
				drawLine(prevDrag_x, prevDrag_y, x, y, true);
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
	
	
	
	public void drawLine(int x, int y, int x2, int y2){
		drawLine(x, y, x2, y2, false);
	}
	
	

	//CREDIT TO http://tech-algorithm.com/articles/drawing-line-using-bresenham-algorithm/
	public void drawLine(int x,int y,int x2, int y2, boolean drawThickPoint) {
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
	    	
	    	if(drawThickPoint){
	    		drawThickPoint(x, y);
	    	}
	    	
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
		
		
		pixelDrawMinY = Integer.MAX_VALUE;
		pixelDrawMaxY = Integer.MIN_VALUE;
		pixelDrawMinX = Integer.MAX_VALUE;
		pixelDrawMaxX = Integer.MIN_VALUE;
		
		if(pixels != null){
			
			for(int y = 0; y < pixelsHeight; y++){
				for(int x = 0; x < pixelsWidth; x++){
					pixels[y][x] = false;
				}
			}
			repaint();
		}
	}
	
	

	
	

}
