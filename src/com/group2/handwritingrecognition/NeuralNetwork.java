package com.group2.handwritingrecognition;

public class NeuralNetwork {
	
	
	int numberOfInputNeurons;
	int numberOfNeuronsPerHiddenLayer;
	int numberOfHiddenNeuronLayers;
	int numberOfOutputNeurons;
	
		
	
		
		
		
		
	
	
	//Here are some inputs for the network.  You may force them here or adjust them in CustomFrame's constructor, which creates the network.
	public NeuralNetwork(int arg_numberOfInputNeurons, int arg_numberOfNeuronsPerHiddenLayer, int arg_numberOfHiddenNeuronLayers, int arg_numberOfOutputNeurons ){
		numberOfInputNeurons = arg_numberOfInputNeurons;
		numberOfNeuronsPerHiddenLayer = arg_numberOfNeuronsPerHiddenLayer;
		numberOfHiddenNeuronLayers = arg_numberOfHiddenNeuronLayers;
		numberOfOutputNeurons = arg_numberOfOutputNeurons;
		
		
		
		
		
	}
	
	
	
	//reset all the charges (connection weights, particularly)
	//This is called at startup (if not loading charges from a file), or right
	//before applying new trials.
	void clear(){
		
		
		
	}
	
	//Just sends each character's trial to be treated as neural network input, with an answer in mind (i, which is 0, 1, 2, 3, ...)
	void train(TrialMemory[] characterData){
		
		
		for(int i = 0; i < characterData.length; i++){
			
			for(int i2 = 0; i2 < characterData[i].trialMem.length; i2++){
				
				trainWithTrial(characterData[i].trialMem[i2], i);
			}//END OF for(int i2 = 0...)
		}//END OF for(int i = 0...)
		
		
		
	}
	
	
	
	int boolToInt(boolean arg){
		if(arg == true){
			return 1;
		}else{
			return 0;
		}
	}
	
	
	//TODO
	void sendTrialToInputNeurons(boolean[][] thisTrial){
		
		int inputNeuronIndex = 0;
		for(int y = 0; y < thisTrial.length; y++){
			for(int x = 0; x < thisTrial[y].length; x++){
				
				//... = boolToInt(thisTrial[y][x]);
				inputNeuronIndex++;
				
			}//END OF for(int x = 0...)
		}//END OF for(int y = 0...)
		
		//...Or, you can count by "inputNeuronIndex" linearly in a single for loop and determine row and col positions like so:
		
		for(int i = 0; i < thisTrial.length * thisTrial[0].length; i++){
			int x = i % thisTrial[0].length;			
			int y = i / thisTrial.length;
			//... = boolToInt(thisTrial([y][x]));
			
			
		}
		
		
	}
	
	//TODO
	void sendTargetToOutputs(int correctNumber){
		
		//I imagine this should be, "what single output neuron's target should be 1 instead of 0?"
		
	}
	
	
	//TODO
	void trainWithTrial(boolean[][] thisTrial, int correctNumber){
		
		sendTrialToInputNeurons(thisTrial);
		
		sendTargetToOutputs(correctNumber);
		
		//Can measure the error and print it out.
		
		//Then, do back propogation..
		
		//Finally, modify the connection weights.
		
	}
	
	//TODO
	int attemptTrial(boolean[][] thisTrial){
		
		sendTrialToInputNeurons(thisTrial);
		
		//...
		
		//read output neurons, come up with a "result":
		int result = 0;
		
		
		
		
		
		return result;
		
	}
	
	
	
	
	
	
	
	
	
	

}
