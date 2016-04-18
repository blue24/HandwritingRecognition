package com.group2.handwritingrecognition;

import java.util.ArrayList;

public class NeuralNetworkOLD {
	
	float learningRate = 0.35f;
	
	
	
	
	
	class Node{
		
		//Node parentNode
		ArrayList<NeuralNetworkConnectionOLD> connectionsIn;
		ArrayList<NeuralNetworkConnectionOLD> connectionsOut;
		
		int layer;
		float charge;
		
		float deltaError;
		float deltaError2;
		
		/*
		public void sendCharge(Connection arg){
			
			arg.outNode
			
		}
		*/
		
		//Change applies just to THIS node.
		public void determineCharge(){
			
			float cumulativeCharge = 0;
			for(int i = 0; i < connectionsIn.size(); i++){
				NeuralNetworkConnectionOLD con = connectionsIn.get(i);
				cumulativeCharge += con.strength * con.inNode.charge;
				
			}
			charge = cumulativeCharge;
			
		}
		
		public void determineDeltaError(float given){
			float temp = charge * (1 - charge) * (given - charge);
			deltaError2 = temp;
			
		}
		
		//Change applies to ALL nodes behind (in terms of layers, hopefully)
		public void backProp(){
			
			float cumulative = 0;
			
			for(int i = 0; i < connectionsIn.size(); i++){
				NeuralNetworkConnectionOLD con = connectionsIn.get(i);
				con.deltaError = learningRate * this.deltaError * con.inNode.charge;  
				//add delta error to connection's strength?
				
				//cumulative += learningRate * this.deltaError * connectionsIn.get(i).inNode.charge;  
				
			}
			//deltaError = cumulative;
			
		}
		
		public void backProp2(){
			
			float cumulative = 0;
			for(int i = 0; i < connectionsOut.size(); i++){
				NeuralNetworkConnectionOLD con = connectionsOut.get(i);
				
				cumulative += charge * (1 - charge) * (con.strength) * (con.outNode.deltaError2) ; 
					
			}
			deltaError2 = cumulative;
		}

		public void backProp3(){
			
			for(int i = 0; i < connectionsOut.size(); i++){
				NeuralNetworkConnectionOLD con = connectionsOut.get(i);
				
				con.deltaError2 = learningRate * con.outNode.deltaError2 * this.charge;
					
			}
		}
		
	}
	
	
	
	
	public NeuralNetworkOLD(){
		
		
		Node node1 = new Node();
		Node node2 = new Node();
		Node node3 = new Node();
		
		
		
		
	}
	
	
	static void addConnection(Node nodeIn, Node nodeOut){
		NeuralNetworkConnectionOLD conn = new NeuralNetworkConnectionOLD();
		
		conn.inNode = nodeIn;
		conn.outNode = nodeOut;
		
		nodeIn.connectionsIn.add(conn);
		nodeOut.connectionsOut.add(conn);
		
		
	}
	
	
	
	
	
}
