package com.group2.handwritingrecognition;

public class TimerManager {
	
	CustomTimer currentTimer = null;
	
	
	public TimerManager(){
		
	}
	
	
	
	public void setTimer(float delay1, float delay2){
		
		final float relativeDelay2 = delay2 - delay1;
		
		
		if(currentTimer != null){
			currentTimer.active = false;
		}

		final TimerManager refBack = this;
		
		currentTimer = new CustomTimer(delay1){
			@Override
			public void action(){
				refBack.action();
				setTimer(relativeDelay2);
			}
		};
		
		
		
	}
	
	public void setTimer(float delay){

		if(currentTimer != null){
			currentTimer.active = false;
		}

		final TimerManager refBack = this;
		
		currentTimer = new CustomTimer(delay){
			@Override
			public void action(){
				refBack.action();
			}
		};
		
		
	}
	
	public void cancel(){
		if(currentTimer != null){
			currentTimer.active = false;
			currentTimer = null;
		}
	}
	
	
	public void action(){
		
	}
	
	

}
