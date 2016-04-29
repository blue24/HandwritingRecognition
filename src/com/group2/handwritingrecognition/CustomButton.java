package com.group2.handwritingrecognition;

import javax.swing.JButton;

public class CustomButton extends JButton{
	
	
	public CustomButton(){
		this(false);
	}
	
	public CustomButton(boolean useSmallerFont){
		super();
		
		if(!useSmallerFont){
			setFont(Static.fntSansSerif);
		}else{
			setFont(Static.fntSansSerifSmall);
		}
		
		this.setFocusable(false);
		
	}
	
}
