package com.group2.handwritingrecognition;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

public class TrialMemory implements Serializable {
	
	
	public transient ArrayList<boolean[][]> trialMem;
	
	
	
	private synchronized void writeObject(java.io.ObjectOutputStream stream) throws IOException {
		
		stream.defaultWriteObject( );
		
		
		int length1 = trialMem.size();
		int length2 = Static.groupPixelsHeight;
		int length3 = Static.groupPixelsWidth;
		
		
		stream.writeInt(length1);
		stream.writeInt(length2);
		stream.writeInt(length3);
		
		
		
		for(int i = 0; i < length1; i++){
			for(int y = 0; y < length2; y++){
				for(int x = 0; x < length3; x++){
					if(trialMem.get(i) != null && trialMem.get(i)[y] != null){
						stream.writeBoolean(trialMem.get(i)[y][x]);
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
		
		Static.recentLoadedTrialLength1 = length1;
		Static.recentLoadedTrialLength2 = length2;
		Static.recentLoadedTrialLength3 = length3;
		
		//Static.trialsPerNumber
		trialMem = new ArrayList<boolean[][]>();
		
		for(int i = 0; i < length1; i++){
			boolean[][] thisImage = new boolean[length2][length3];
			trialMem.add(thisImage);
			for(int y = 0; y < length2; y++){
				for(int x = 0; x < length3; x++){
					thisImage[y][x] = stream.readBoolean();
					
				}
			}
		}
		
	}
	
	
	public TrialMemory(){
		//one trial for each number.
		//trialMem = new boolean[Static.trialsPerNumber][][];
		trialMem = new ArrayList<boolean[][]>();
	}
	
}
