package com.group2.handwritingrecognition;

import javax.swing.JLabel;

public class CustomLabel extends JLabel {
	
	
	public CustomLabel(){
		this(false);
	}
	
	public CustomLabel(boolean useSmallerFont){
		super();
		
		if(!useSmallerFont){
			setFont(Static.fntSansSerif);
		}else{
			setFont(Static.fntSansSerifSmall);
		}
		
		this.setFocusable(false);
		
	}

}
