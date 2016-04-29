package com.group2.handwritingrecognition;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;

import javax.swing.JComponent;

public class Static {
	
	public enum LoadMode{
		NOTHING, LOADTRIALS, LOADWEIGHTS
		
		
	}
	
	
	//DEBUG PROPERTIES
	///////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	
	
	
	
	static boolean networkdNeedsTrainPrompt = false;
	
	static int groupPixelsWidth = 16;
	static int groupPixelsHeight = 24;
	
	static int boundsMinimumWidth = 20;
	static int boundsMinimumHeight = 30;

	static float boundsRatioMin = 0.28f;  //how tall can I be?
	static float boundsRatioMax = 1.8f;    //how short can I be?
	//note: less is taller, value 0 is infinitely tall (no width),
	//value 1 is square,
	//value "infinity" is infinitely long (no height).
	
	
	
	static boolean autoTrain = false;

	//How many trials for each number?
	static int trialsPerNumber = 5;
	
	//was 0.5?
	static float learningRateMax = 0.45f;
	static float learningRateMin = 0.18f;
	
	static int timesToTrainEach = 2800;
	
	
	static int numbOfInputNeurons = Static.groupPixelsWidth * Static.groupPixelsHeight;
	
	static int numbOfNeuronsPerHiddenLayer = 100;  //same? double it? unsure.
	//static int numbOfNeuronsPerHiddenLayer = 50;  //same? double it? unsure.
	static int numberOfHiddenLayers = 1;
	static int numberOfOutputNeurons = 10;
	

	
	static boolean forceNoLoad = false;
	
	static int defaultWindowWidth = 800;
	static int defaultWindowHeight = 600;
	
	//Can the window be resized by the user?
	static boolean windowIsResizable = true;
	
	
	static LoadMode loadMode = LoadMode.LOADWEIGHTS;
			
	
	/*
	These two conditions are used to tell whether a sector (one of the 16ths, as of yet)
	will be considered written or not.  This is an OR case; either lets this sector count.
	*/
	//If there are this many pixels in a sector, it counts.
	static int pixelsRequiredInSector = 10;
	//If this great of a portion (0 = none, 1 = solid) are shaded, it counts.
	static float sectorRegisterRatio = 0.25f;
	
	//If turned off, trials will not progress the state.  Debugging only.
	static boolean trialAdvancingOff = false;

	
	//Draw debug-things (the bounding rect of a number, sector-divisions, etc.)
	static boolean drawDebug = true;
	static boolean saveOnExit = true;
	
	//By what factor will the bounded region be split?  Default is groups of 16x16.
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	
	static Font fntSansSerif = new Font("SanSerif", Font.PLAIN, 18);
	static Font fntSansSerifBig = new Font("SanSerif", Font.PLAIN, 48);
	
	
	static GridBagConstraints gbc = new GridBagConstraints();
	
	
	static Color clrWhite = new Color(255, 255, 255);
	static Color clrOffWhite = new Color(238, 238, 238);
	static Color clrBlack = new Color(0, 0, 0);
	static Color clrRed = new Color(225, 5, 5);
	static Color clrGreen = new Color(12, 195, 12);
	static Color clrGreenTrans = new Color(12, 215, 12, 175);
	static Color clrCyanTrans = new Color(0, 240, 225, 155);
	
	
	
	public static void addGridBagConstraintsComp(int fill, int anchor, int gridx, int gridy, int gridwidth, int gridheight, double weightx, double weighty, int ipadx, int ipady, JComponent addTo, Component toAdd  ){
		
		gbc.fill = fill;
		gbc.anchor = anchor;
		gbc.gridx = gridx;
		gbc.gridy = gridy;
		gbc.gridwidth = gridwidth;
		gbc.gridheight = gridheight;
		gbc.weightx = weightx;
		gbc.weighty = weighty;
		gbc.ipadx = ipadx;
		gbc.ipady = ipady;
		addTo.add(toAdd, gbc);
		
	}
	
	

}
