package com.group2.handwritingrecognition;

import javax.swing.JComponent;


public class CustomComponent extends JComponent {
	
	
	public CustomComponent(){
		this(false);
	}
	
	public CustomComponent(boolean useSmallerFont){
		super();
		
		if(!useSmallerFont){
			setFont(Static.fntSansSerif);
		}else{
			setFont(Static.fntSansSerifSmall);
		}
		
		this.setFocusable(false);
		
	}
	
	
}
