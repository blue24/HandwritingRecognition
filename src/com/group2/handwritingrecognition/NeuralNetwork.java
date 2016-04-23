package com.group2.handwritingrecognition;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;





public class NeuralNetwork {
	
	boolean inputHasBias;
	int numberOfInputNeurons;
	int numberOfNeuronsPerHiddenLayer;
	int numberOfHiddenNeuronLayers;
	int numberOfOutputNeurons;
	
	
	public synchronized void writeWeights(java.io.ObjectOutputStream stream) throws IOException {
		//stream.defaultWriteObject( );
		
		/*
		stream.writeInt(numberOfInputNeurons);
		stream.writeInt(numberOfNeuronsPerHiddenLayer);
		stream.writeInt(numberOfHiddenNeuronLayers);
		stream.writeInt(numberOfOutputNeurons);
		
		stream.writeBoolean(inputHasBias);
		*/
		
		
		//stream.writeInt(inputLayer.neurons.size());
		
		//inputLayer = new NeuronLayer(nodesInInput, null, inputHasBias);
		writeWeights(inputLayer, stream);
		
		for(int i = 0; i < hiddenLayers.length; i++){
			writeWeights(hiddenLayers[i], stream);
		}
		
		
	}
	
	public synchronized void writeWeights(NeuronLayer someLayer, ObjectOutputStream stream) throws IOException {
		for(int i = 0; i < someLayer.neurons.size(); i++){
			Neuron thisNeuron = someLayer.neurons.get(i);
			
			//stream.writeInt(thisNeuron.connectionsOut.size());
			for(int i2 = 0; i2 < thisNeuron.connectionsOut.size(); i2++){
				stream.writeFloat(thisNeuron.connectionsOut.get(i2).weight);
				//System.out.println("wrote " + i2 + " : " + thisNeuron.connectionsOut.get(i2).weight);
			}
		}
	}
	
	public synchronized void loadWeights(NeuronLayer someLayer, ObjectInputStream stream) throws java.io.IOException {
		
		for(int i = 0; i < someLayer.neurons.size(); i++){
			Neuron thisNeuron = someLayer.neurons.get(i);
			
			//thisNeuron.connectionsOut.size() = stream.readInt();
			for(int i2 = 0; i2 < thisNeuron.connectionsOut.size(); i2++){
				thisNeuron.connectionsOut.get(i2).weight = stream.readFloat();
				//System.out.println("read " + i2 + " : " + thisNeuron.connectionsOut.get(i2).weight);
			}
		}
	}
	
	
	public synchronized void loadWeights(ObjectInputStream stream) throws java.io.IOException {
		/*
		try {
			stream.defaultReadObject( );
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		*/
		
		
		/*
		numberOfInputNeurons = stream.readInt();
		numberOfNeuronsPerHiddenLayer = stream.readInt();
		numberOfHiddenNeuronLayers = stream.readInt();
		numberOfOutputNeurons = stream.readInt();
		
		inputHasBias = stream.readBoolean();
		*/
		
		
		//inputLayer.neurons.size() = stream.readInt();
		
		//inputLayer = new NeuronLayer(nodesInInput, null, inputHasBias);
		
		
		
		loadWeights(inputLayer, stream);
		
		for(int i = 0; i < hiddenLayers.length; i++){
			loadWeights(hiddenLayers[i], stream);
		}
		
		
		
		
	}
	
	
	//Here are some inputs for the network.  You may force them here or adjust them in CustomFrame's constructor, which creates the network.
	public NeuralNetwork(int arg_numberOfInputNeurons, int arg_numberOfNeuronsPerHiddenLayer, int arg_numberOfHiddenNeuronLayers, int arg_numberOfOutputNeurons, boolean inputHasBias ){
		numberOfInputNeurons = arg_numberOfInputNeurons;
		numberOfNeuronsPerHiddenLayer = arg_numberOfNeuronsPerHiddenLayer;
		numberOfHiddenNeuronLayers = arg_numberOfHiddenNeuronLayers;
		numberOfOutputNeurons = arg_numberOfOutputNeurons;
		

		int nodesInInput = numberOfInputNeurons;
		
		if(inputHasBias){
			//extra for bias.
			nodesInInput ++;
		}
		
		inputLayer = new NeuronLayer(nodesInInput, null, inputHasBias);
		
		//most recently created layer.
		NeuronLayer prevLayer = inputLayer;
		
		hiddenLayers = new NeuronLayer[numberOfHiddenNeuronLayers];
		
		for(int i = 0; i < hiddenLayers.length; i++){
			hiddenLayers[i] = new NeuronLayer(numberOfNeuronsPerHiddenLayer, prevLayer, true);
			//Now, this is the most recently created layer:
			prevLayer = hiddenLayers[i];
		}
		
		outputLayer = new NeuronLayer(numberOfOutputNeurons, prevLayer, false);
		
		
		
		
		
	}
	
	
	
	//reset all the charges (connection weights, particularly)
	//This is called at startup (if not loading charges from a file), or right
	//before applying new trials.
	void clear(){
		System.out.println("CLEARED?!");
		inputLayer.clear();
		for(int i = 0; i < hiddenLayers.length; i++){
			hiddenLayers[i].clear();
		}
		outputLayer.clear();
		
		
	}
	
	//Just sends each character's trial to be treated as neural network input, with an answer in mind (i, which is 0, 1, 2, 3, ...)
	void train(TrialMemory[] characterData, int timesToTrain){
		
		
		
		for(int i3 = 0; i3 < timesToTrain; i3++){
			
			for(int i = 0; i < characterData.length; i++){
				
				for(int i2 = 0; i2 < characterData[i].trialMem.length; i2++){
					
					
					trainWithTrial(characterData[i].trialMem[i2], i);
					
					
					
				}//END OF for(int i2 = 0...)
				//break;
			}//END OF for(int i = 0...)
			
		}
		
		
		System.out.println("RESULTS:::");
		
		for(int i = 0; i < characterData.length; i++){
			
			for(int i2 = 0; i2 < characterData[i].trialMem.length; i2++){
				
				//for(int i3 = 0; i3 < timesToTrain; i3++){
					int result = attemptTrial(characterData[i].trialMem[i2]);
					
					if(result == i){
						System.out.println("CORRECT");
					}else{
						System.out.println("NO");
					}
					
				//}
				
				
			}//END OF for(int i2 = 0...)
			//break;
		}//END OF for(int i = 0...)
		
		
		
		
	}
	
	int trainingWithNumber = 0;
	
	int boolToInt(boolean arg){
		if(arg == true){
			return 1;
		}else{
			return 0;
		}
	}
	
	
	
	
	
	
	//TODO
	void sendTrialToInputNeurons(boolean[][] thisTrial){
		
		
		//System.out.println("TRIAL!!!!!!!!!!!!!!!!!!!!!");
		int inputNeuronIndex = 0;
		for(int y = 0; y < thisTrial.length; y++){
			for(int x = 0; x < thisTrial[y].length; x++){
				
				//... = boolToInt(thisTrial[y][x]);
				inputLayer.neurons.get(inputNeuronIndex).setInputCharge(boolToInt(thisTrial[y][x]));
				inputNeuronIndex++;
				
				//System.out.print(boolToInt(thisTrial[y][x]));
				
			}//END OF for(int x = 0...)
			
			//System.out.println();
			
		}//END OF for(int y = 0...)
		
		//...Or, you can count by "inputNeuronIndex" linearly in a single for loop and determine row and col positions like so:
		
		/*
		for(int i = 0; i < thisTrial.length * thisTrial[0].length; i++){
			int x = i % thisTrial[0].length;			
			int y = i / thisTrial.length;
			//... = boolToInt(thisTrial([y][x]));
			
			
		}
		*/
		
	}
	
	//TODO
	void sendTargetToOutputs(int correctNumber){
		
		//I imagine this should be, "what single output neuron's target should be 1 instead of 0?"
		//System.out.println("-------");
		for(int i = 0; i < outputLayer.neurons.size(); i++){
			int target = 0;
			
			
			if(i == correctNumber){
				target = 1;
			}
			//System.out.println("TARGET " + target);
			
			outputLayer.neurons.get(i).targetOutputCharge = target;
			
			
		}
		
		
	}
	
	
	
	//TODO
	void trainWithTrial(boolean[][] thisTrial, int correctNumber){
		
		trainingWithNumber = correctNumber;
		
		sendTrialToInputNeurons(thisTrial);
		sendTargetToOutputs(correctNumber);
		
		
		//Can measure the error and print it out.
		
		//Then, do forward & back propogation..
		train();
		
		//Finally, modify the connection weights.
		//(done in train() here)
		
		
	}
	
	//TODO
	int attemptTrial(boolean[][] thisTrial){
		
		sendTrialToInputNeurons(thisTrial);
		
		//...
		
		//read output neurons, come up with a "result":
		int result = 0;
				
		
		
		feedForward();
		
		/*
		for(int i = 0; i < outputLayer.neurons.size(); i++){
			System.out.printf("STATUS : %.2f, %.2f\n",outputLayer.neurons.get(i).myCharge, outputLayer.neurons.get(i).outputCharge );
			
		}
		System.out.println("ERROR? " + totalOutputChargeError);
		*/
		
		ArrayList<Neuron> ON = outputLayer.neurons;
		
		//If the 1st (#0) output node is greater than the others, we predicted "Iris Setosa".

		
		System.out.println("PRINTOUT: ");
		
		for(int i = 0; i < outputLayer.neurons.size(); i++){
			System.out.printf("%d : %f\n", i, outputLayer.neurons.get(i).outputCharge);
			
		}
		System.out.println("???  " + outputLayer.getGreatestOutputNeuronIndex());
		
		
		
		return outputLayer.getGreatestOutputNeuronIndex();
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	NeuronLayer inputLayer;
	NeuronLayer[] hiddenLayers;
	NeuronLayer outputLayer;
	
	
	
	//was 0.5?
	float learningRate = 0.35f;
	
	
	
	
	
	int times = 100;
	
	//Train.
	void train( ){
		
		feedForward();
		
		if(times > 0 && trainingWithNumber == 0){
			System.out.printf("WAT IS IT %f\n", totalOutputChargeError);
			
			for(int i = 0; i < outputLayer.neurons.size(); i++){
				System.out.printf("ECK %d %f %f\n", i, outputLayer.neurons.get(i).outputChargeError, outputLayer.neurons.get(i).outputCharge);
				
			}
			times--;
		}
		
		
		//...the rest of this is all back propogation.
		outputLayer.determineOutputChargeErrorDerivativesWRTOutputChargeOUT();
		outputLayer.determineOutputChargeErrorDerivativesWRTSigmoid();

		outputLayer.determineMyChargeDerivativesWRTIncomingConnections();
		
		for(int i = hiddenLayers.length - 1; i >= 0; i--){
			hiddenLayers[i].determineTotalErrorDerivativesWRTOutputCharge();
			hiddenLayers[i].determineMyChargeDerivativesWRTIncomingConnections();
		}
		
		inputLayer.applyModifiedWeightsOut();
		
		//At the end, apply the weights.
		for(int i = 0; i < hiddenLayers.length; i++){
			hiddenLayers[i].applyModifiedWeightsOut();
		}
		
		
	}//END OF train(...)
	
	
	

	
	static float determineTotalOutputChargeError(NeuronLayer layer){
		
		float cumulativeError = 0;
		
		for(int i = 0; i < layer.neurons.size(); i++){
			cumulativeError += layer.neurons.get(i).outputChargeError;
		}
		
		return cumulativeError;
		
	}//END OF determineTotalOutputChargeError(...)
	
	

	
	float totalOutputChargeError;
	
	
	void feedForward(){
		
		
		for(int i = 0; i < hiddenLayers.length; i++){
			hiddenLayers[i].receiveCharges();
		}
		outputLayer.receiveCharges();
		
		outputLayer.determineOutputChargeErrors();
		
		//There is no derivative of the total output charge error, ONLY derivatives of this with respect to a given weight.
		//Or WRT other things, perhaps.
		totalOutputChargeError = determineTotalOutputChargeError(outputLayer);
		
	}
	
	
	
	
	
	
	
	
	static int randomInRangeInt(int min, int max){
		Random r = new Random();
		return r.nextInt( max+1 - min) + min;
		
	}
	
	static float randomInRange(float min, float max){
		
		return (float) ((float)Math.random() * (max - min) + min);
		
	}
	
	
	
	
	//A connection between two neurons, generally "neuronFrom" being from a previous layer (input or previous hidden layer).
	//Also stores some other vars particular to weights (connections are associated with "weights").
	static class Connection {
		
		Neuron neuronFrom;
		Neuron neuronTo;
		
		float weight;
		float modifiedWeight;
		
		float destinationChargeWRTWeight;
		float totalErrorWRTWeight;
		
		void applyModifiedWeight(){
			weight = modifiedWeight;
			//System.out.println("NEW : " + modifiedWeight);
			
		}
		
	}
	
	
	
	
	//Note that most methods here are just for applying to all nodes in the layer.
	class NeuronLayer{
		
		ArrayList<Neuron> neurons;
		
		
		public NeuronLayer(int neuronsInLayer, NeuronLayer prevLayer, boolean lastIsBiased){
			
			neurons = new ArrayList<Neuron>();
			
			for(int i = 0; i < neuronsInLayer; i++){
				
				Neuron newNeuron = new Neuron(lastIsBiased && (i == neuronsInLayer-1) );
				neurons.add(newNeuron);
				
				if(prevLayer != null){
					
					//For all nodes in the previous layer, link to this one.
					//Except for bias nodes in THIS layer (the last one each layer)!
					//if(i != neuronsInLayer - 1){
					if(newNeuron.isBias == false){	
						for(int i2 = 0; i2 < prevLayer.neurons.size(); i2++){
							
							//isn't "this.neurons.get(i)" the same as "newNeuron" ?
							createConnection(prevLayer.neurons.get(i2), this.neurons.get(i) );
							
						}
					}
				}
			}//END OF for(int i = 0...)
			
		}//END OF CONSTRUCTOR OF NeuronLayer
		
		public void clear(){
			int neuronsInLayer = neurons.size();
			for(int i = 0; i < neuronsInLayer; i++){
				neurons.get(i).clear();
			}
			
		}
		
		
		public void applyModifiedWeightsOut(){
			
			int neuronsInLayer = neurons.size();
			
			for(int i = 0; i < neuronsInLayer; i++){
				if(neurons.get(i).isBias == false){
					neurons.get(i).applyModifiedWeightsOut();
				}
				
			}//END OF for(int i = 0...)
			
		}
		
		
		public void receiveCharges(){
			
			int neuronsInLayer = neurons.size();
			
			for(int i = 0; i < neuronsInLayer; i++){
				if(neurons.get(i).isBias == false){
					neurons.get(i).receiveCharge();
				}
				
			}//END OF for(int i = 0...)
			
		}
		
		
		public void applySigmoidToCharges(){
			int neuronsInLayer = neurons.size();
			
			for(int i = 0; i < neuronsInLayer; i++){
				if(neurons.get(i).isBias == false){
					neurons.get(i).applySigmoidToCharge();
				}
				
			}//END OF for(int i = 0...)
			
		}
		
		public void determineOutputChargeErrors(){
			int neuronsInLayer = neurons.size();
			
			for(int i = 0; i < neuronsInLayer; i++){
				neurons.get(i).determineOutputChargeError();
				
			}//END OF for(int i = 0...)
			
		}
		
		public void determineOutputChargeErrorDerivativesWRTOutputChargeOUT(){
			int neuronsInLayer = neurons.size();
			
			for(int i = 0; i < neuronsInLayer; i++){
				neurons.get(i).determineOutputChargeErrorDerivativeWRTOutputChargeOUT();
			}//END OF for(int i = 0...)
			
		}
		
		//
		public void determineOutputChargeErrorDerivativesWRTSigmoid(){
			int neuronsInLayer = neurons.size();
			for(int i = 0; i < neuronsInLayer; i++){
				neurons.get(i).determineOutputChargeErrorDerivativeWRTSigmoid();
			}//END OF for(int i = 0...)
		}
		
		
		public void determineMyChargeDerivativesWRTIncomingConnections(){
			int neuronsInLayer = neurons.size();
			for(int i = 0; i < neuronsInLayer; i++){
				neurons.get(i).determineMyChargeDerivativeWRTIncomingConnections();
			}//END OF for(int i = 0...)
		}
		
		public void determineMyChargeDerivativesWRTIncomingConnections222(){
			int neuronsInLayer = neurons.size();
			for(int i = 0; i < neuronsInLayer; i++){
				neurons.get(i).determineMyChargeDerivativeWRTIncomingConnections222();
			}//END OF for(int i = 0...)
		}
		
		
		public void determineTotalErrorDerivativesWRTOutputCharge(){
			int neuronsInLayer = neurons.size();
			for(int i = 0; i < neuronsInLayer; i++){
				neurons.get(i).determineTotalErrorDerivativeWRTOutputCharge();
			}//END OF for(int i = 0...)
			
			
		}
		
		
		int getGreatestOutputNeuronIndex(){
			
			int maxIndex = 0;
			
			Neuron currentMaxNeuron = neurons.get(maxIndex);
			
			for(int i = 1; i < neurons.size(); i++){
				
				
				
				Neuron thisNeuron = neurons.get(i);
				if(currentMaxNeuron.outputCharge <= thisNeuron.outputCharge){
					maxIndex = i;
					currentMaxNeuron = neurons.get(maxIndex);
				}
				
			}
			
			return maxIndex;
			
		}
		
		
		
		
	}//END OF class NeuronLayer
	
	
	//Methods for individual neurons.
	class Neuron{
		
		float myCharge;
		float outputCharge;
		
		float targetOutputCharge;
		
		float totalErrorDerivativeWRTOutputCharge;
		
		
		float outputChargeError;
		
		float outputChargeDerivativeWRTCharge;
		
		
		ArrayList<Connection> connectionsIn;
		ArrayList<Connection> connectionsOut;
		
		boolean isBias = false;
		
		
		public Neuron(boolean arg_isBias){
			
			targetOutputCharge = 0;
			outputChargeError = 0;
			
			connectionsIn = new ArrayList<Connection>();
			connectionsOut = new ArrayList<Connection>();
			
			isBias = arg_isBias;
			
			/*
			if(isBias){
				myCharge = 1;
			}else{
				myCharge = randomInRange(-1f, 1f);
			}
			*/
			
			//NO - a bias charge is randomized no matter what.
			//It's connection weight outwards, however, is always 1.
			
			myCharge = randomInRange(-1f, 1f);
			outputCharge = myCharge;
		}
		
		public void clear(){
			
			if(isBias == false){
				
				for(int i = 0; i < connectionsOut.size(); i++){
					connectionsOut.get(i).weight = randomInRange(-1f, 1f);
				}
				myCharge = randomInRange(-1f, 1f);
				outputCharge = myCharge;
				
			}else{
				
				//Bias outward connections always have weight 1.
				for(int i = 0; i < connectionsOut.size(); i++){
					connectionsOut.get(i).weight = 1;
				}
				myCharge = randomInRange(-1f, 1f);
				outputCharge = myCharge;
				
			}
			
			
		}
		
		public void applyModifiedWeightsOut(){
			
			for(int i = 0; i < connectionsOut.size(); i++){
				connectionsOut.get(i).applyModifiedWeight();
			}
		}
		
		public void setInputCharge(float newCharge){
			myCharge = newCharge;
			outputCharge = newCharge;
		}
		
		
		public void receiveCharge(){
			
			float cumulativeCharge = 0;
			//System.out.println("GNU");
			for(int i = 0; i < connectionsIn.size(); i++){
				Connection currentInConnection = connectionsIn.get(i);
				Neuron currentNeuronIn = currentInConnection.neuronFrom;
				
				cumulativeCharge += currentNeuronIn.outputCharge * currentInConnection.weight;
			}
			
			myCharge = cumulativeCharge;
			
			outputCharge = sigmoid(cumulativeCharge);
			
			//Not necessarily?
			//myCharge = sigmoid(cumulativeCharge);
			
		}
		
		public void applySigmoidToCharge(){
			
			myCharge = sigmoid(myCharge);
			
		}
		
		public void determineOutputChargeError(){
			outputChargeError = 0.5f * (float)Math.pow( targetOutputCharge - outputCharge, 2);
			
		}
		

		public void determineOutputChargeErrorDerivativeWRTOutputChargeOUT(){
			totalErrorDerivativeWRTOutputCharge = outputCharge - targetOutputCharge;
			
		}
		

		
		
		public void determineOutputChargeErrorDerivativeWRTSigmoid(){
			//outputChargeErrorDerivativeWRTSigmoid = sigmoidDerivative(myCharge);
			//
			//outputChargeErrorDerivativeWRTSigmoid = outputCharge * (1f - outputCharge);
			//var retired.
			
			outputChargeDerivativeWRTCharge = outputCharge * (1f - outputCharge);
			
		}
		
		
		public void determineMyChargeDerivativeWRTIncomingConnections(){
			
			for(int i = 0; i < connectionsIn.size(); i++){
				Connection c = connectionsIn.get(i);
				
				//Only do this for non-bias nodes.
				if(c.neuronFrom.isBias == false){
					determineMyChargeDerivativeWRTIncomingConnection(c);
				}
				
			}
		}
		
		
		public void determineMyChargeDerivativeWRTIncomingConnection(Connection incoming){
			incoming.destinationChargeWRTWeight = incoming.neuronFrom.outputCharge;
			incoming.totalErrorWRTWeight = totalErrorDerivativeWRTOutputCharge * outputChargeDerivativeWRTCharge * incoming.destinationChargeWRTWeight;
			
			//System.out.println("DDD " + totalErrorDerivativeWRTOutputCharge + " " + outputChargeDerivativeWRTCharge + " " + incoming.destinationChargeWRTWeight);
			//System.out.println("SO WHAT " + incoming.totalErrorWRTWeight);
			
			incoming.modifiedWeight = incoming.weight - learningRate * incoming.totalErrorWRTWeight;
			//System.out.println("LERNIN " + incoming.modifiedWeight);
		}
		
		
		
		public void determineMyChargeDerivativeWRTIncomingConnections222(){
			
			for(int i = 0; i < connectionsIn.size(); i++){
				Connection c = connectionsIn.get(i);
				
				//Only do this for non-bias nodes.
				if(c.neuronFrom.isBias == false){
					determineMyChargeDerivativeWRTIncomingConnection222(c);
				}
				
			}
			
		}
		
		public void determineMyChargeDerivativeWRTIncomingConnection222(Connection incoming){
			incoming.destinationChargeWRTWeight = incoming.neuronFrom.outputCharge;
			incoming.totalErrorWRTWeight = totalErrorDerivativeWRTOutputCharge * outputChargeDerivativeWRTCharge * incoming.destinationChargeWRTWeight;
			
			//System.out.println("DDD " + totalErrorDerivativeWRTOutputCharge + " " + outputChargeDerivativeWRTCharge + " " + incoming.destinationChargeWRTWeight);
			//System.out.println("SO WHAT " + incoming.totalErrorWRTWeight);
			
			incoming.modifiedWeight = incoming.weight - learningRate * incoming.totalErrorWRTWeight;
			//System.out.println("LERNIN " + incoming.modifiedWeight);
		}
		
		
		public void determineTotalErrorDerivativeWRTOutputCharge(){
			
			float cumulative = 0;
			for(int i = 0; i < connectionsOut.size(); i++){
				Neuron n = connectionsOut.get(i).neuronTo;
				
				//outputChargeErrorDerivativeWRTOutputCharge
				float outNeuronErrorDerivativeWRTOutputCharge = 0;
				
				//???
				//if(isAlmostLast){
					outNeuronErrorDerivativeWRTOutputCharge = n.totalErrorDerivativeWRTOutputCharge * n.outputChargeDerivativeWRTCharge;
				//}
					
				
				cumulative += outNeuronErrorDerivativeWRTOutputCharge * connectionsOut.get(i).weight;
				
			}
			//totalErrorDerivativeWRTOutputCharge should this be unique? probably not.
			totalErrorDerivativeWRTOutputCharge = cumulative;
			
			
			//Includes outputChargeDerviatve determination!
			outputChargeDerivativeWRTCharge = outputCharge * (1 - outputCharge);
			
			//System.out.println("BOOO " + outputChargeDerivativeWRTCharge);
			
		}
		
	}//END OF Class Neuron
	
	
	//Make a new connection from "neuronFrom" to "neuronTo".  Both neurons will share the same connection object for
	//the sake of efficiency.
	public static void createConnection(Neuron neuronFrom, Neuron neuronTo){
		
		Connection conn = new Connection();
		conn.neuronFrom = neuronFrom;
		conn.neuronTo = neuronTo;
		
		
		if(neuronFrom.isBias){
			conn.weight = (float) 1f;
			
		}else{
			conn.weight = (float) randomInRange(-1f, 1f);
		}
		
		neuronFrom.connectionsOut.add(conn);
		neuronTo.connectionsIn.add(conn);
		
	}
	
	
	public static float sigmoid(float x){
		return (float) ( 1f / (1 + Math.exp(-x) ) );
	}
	
	public static float sigmoidDerivative(float x){
		
		float sigmoidValue = sigmoid(x);
		
		return sigmoidValue * (1f - sigmoidValue);
	}
	
	
	
	

}
