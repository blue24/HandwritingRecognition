package com.group2.handwritingrecognition;

public class TrialMemory {
	
	
	public boolean[][][] trialMem;
	
	public TrialMemory(){
		//one trial for each number.
		trialMem = new boolean[Static.trialsPerNumber][][];
		
	}
	
}
