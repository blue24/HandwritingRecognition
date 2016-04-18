package com.group2.handwritingrecognition;

import java.io.IOException;
import java.io.Serializable;

public class TrialMemory implements Serializable {
	
	
	public transient boolean[][][] trialMem;
	
	
	
	private synchronized void writeObject(java.io.ObjectOutputStream stream) throws IOException {
		
		stream.defaultWriteObject( );
		
		
		int length1 = trialMem.length;
		int length2 = trialMem[0].length;
		int length3 = trialMem[0][0].length;
		
		
		stream.writeInt(trialMem.length);
		stream.writeInt(trialMem[0].length);
		stream.writeInt(trialMem[0][0].length);
		
		
		
		for(int i = 0; i < length1; i++){
			for(int y = 0; y < length2; y++){
				for(int x = 0; x < length3; x++){
					if(trialMem[i] != null && trialMem[i][y] != null){
						stream.writeBoolean(trialMem[i][y][x]);
					}
				}
			}
		}
		
		
		//for (int i=0; i<size; i++)
		//stream.writeObject(elementData[i]);
	}
	
	
	private synchronized void readObject(java.io.ObjectInputStream stream) throws java.io.IOException {
		try {
			stream.defaultReadObject( );
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		//System.out.println("READ!!?");
		
		int length1 = stream.readInt();
		int length2 = stream.readInt();
		int length3 = stream.readInt();
		
		trialMem = new boolean[Static.trialsPerNumber][][];
		
		for(int i = 0; i < length1; i++){
			trialMem[i] = new boolean[length2][length3];
			for(int y = 0; y < length2; y++){
				for(int x = 0; x < length3; x++){
					trialMem[i][y][x] = stream.readBoolean();
					
				}
			}
		}
		
	}
	
	
	public TrialMemory(){
		//one trial for each number.
		trialMem = new boolean[Static.trialsPerNumber][][];
		
	}
	
}
