package com.group2.handwritingrecognition;

import java.awt.Component;
import java.awt.GridBagConstraints;

import javax.swing.JComponent;

public class Static {
	
	static GridBagConstraints gbc = new GridBagConstraints();
	
	
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
