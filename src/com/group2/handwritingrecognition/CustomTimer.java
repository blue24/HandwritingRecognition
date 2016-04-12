package com.group2.handwritingrecognition;

import java.util.Timer;
import java.util.TimerTask;

public class CustomTimer extends Timer{
	
	boolean active = false;
	
	
	
	
	public CustomTimer(final float delay){
		super();
		
		active = true;
		
		TimerTask t = new TimerTask(){
			@Override
			public void run(){
				
				if(active == true){
					active = false;
					action();
				}
			}
		};


		this.schedule(t, (long)(delay *1000) );
		
	}
	
	
	
	public void action(){
		
	}
	

}
