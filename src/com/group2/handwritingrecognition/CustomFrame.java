package com.group2.handwritingrecognition;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Timer;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;



public class CustomFrame extends JFrame{
	
	private static final long serialVersionUID = -5763319334266549575L;
	

	
	
	
	public NeuralNetwork net;
	
	public CustomPanel pan;
	public DrawablePanel drawSubPan;
	
	
	public Thread trainingThread;
	
	
	
	public boolean startWithTrial = false;

	
	public TextField txtOutput;
	
	
	public JButton btnConfirm;
	public JButton btnClear;
	
	public JLabel lblMessage;
	
	
	public TrialMemory[] characterData;
	
	
	public String currentInstructions;
	
	
	TimerManager errorResetTimer;
	
	TimerManager errorFlashTimer1;
	TimerManager errorFlashTimer2;
	TimerManager errorFlashTimer3;
	TimerManager errorFlashTimer4;
	
	
	String expectedDataName = "";
	String expectedWeightsDataName = "";
	
	
	int programMode = 0;
	//0 = doing trials (insert number samples).
	//1 = test (let the user draw).
	
	
	int trialsLeft = 0;
	int numberToDraw = 0;
	
	
	Font fntSansSerif;
	
	
	boolean loadedSomething = false;
	
	
	
	void interruptWithTrainingThread(){
		trainingThread = new Thread(){
			@Override
			public void run(){
				net.clear();
				net.train(characterData, Static.timesToTrainEach);
				
				writeNeuralNetworkWeights();
				setGUIUse();
				
			}
		};
		trainingThread.run();
	}
	
	void startTrainingThread(){
		trainingThread = new Thread(){
			@Override
			public void run(){
				net.clear();
				net.train(characterData, Static.timesToTrainEach);
				
				writeNeuralNetworkWeights();
				setGUIUse();
				
			}
		};
		trainingThread.start();
	}
	
	
	
	
	public CustomFrame(int defaultSize_x, int defaultSize_y){
		super();
		
		
		drawSubPan = new DrawablePanel(this);
		drawSubPan.setPreferredSize(new Dimension(500, 150) );
		
		
		//credit to
		//http://stackoverflow.com/questions/1984195/close-window-event-in-java
		this.addWindowListener(new WindowAdapter(){
	        public void windowClosing(WindowEvent e){
	        	drawSubPan.endPaintUpdate = true;
	        	//cancel that thread, if it is still running.
	        }
	    });
		
		
		
		
		expectedDataName = "characterData" + Static.trialsPerNumber + "trials" + Static.groupPixelsWidth + "x" + Static.groupPixelsHeight + ".dat";
		expectedWeightsDataName = "charges" + Static.numbOfInputNeurons+ "_" + Static.numbOfNeuronsPerHiddenLayer + "_" + Static.numberOfHiddenLayers + "_" + Static.numberOfOutputNeurons + ".dat";

		
		System.out.println("WHAT " + expectedWeightsDataName);
		
		characterData = new TrialMemory[10];
		for(int i = 0; i < characterData.length; i++){
			characterData[i] = new TrialMemory();
		}
		
		//NOTE: neural network stats here.
		
		
		
		
		net = new NeuralNetwork(Static.numbOfInputNeurons, Static.numbOfNeuronsPerHiddenLayer, Static.numberOfHiddenLayers, Static.numberOfOutputNeurons, true, drawSubPan);
		
		
		
		
		
		errorFlashTimer1 = new TimerManager(){
			@Override public void action(){
				if(lblMessage != null){
					lblMessage.setForeground(Static.clrRed);
				}
			}
		};
		errorFlashTimer2 = new TimerManager(){
			@Override public void action(){
				if(lblMessage != null){
					lblMessage.setForeground(Static.clrBlack);
				}
			}
		};
		errorFlashTimer3 = new TimerManager(){
			@Override public void action(){
				if(lblMessage != null){
					lblMessage.setForeground(Static.clrRed);
				}
			}
		};
		errorFlashTimer4 = new TimerManager(){
			@Override public void action(){
				if(lblMessage != null){
					lblMessage.setForeground(Static.clrBlack);
				}
			}
		};
		
		errorResetTimer = new TimerManager(){
			@Override
			public void action(){
				if(lblMessage != null){
					lblMessage.setText( currentInstructions );
				}
			}
		};
		
		fntSansSerif = new Font("SanSerif", Font.PLAIN, 18);
		
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		pan = new CustomPanel();
		
		pan.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		pan.setLayout(new GridBagLayout());
		
		pan.setPreferredSize(new Dimension(defaultSize_x, defaultSize_y));
		
		//add(pan);
		
		
		
		
		this.getContentPane().add(pan);
		this.setResizable(Static.windowIsResizable);
		
		
		
		
		
		//setGUIUse();
		
		
		
		
		//this.setPreferredSize(new Dimension(800, 600));
		
		this.setVisible(true);
		
		
		
		
		
		setGUIBlank();
		

		
		
		switch(Static.loadMode){
		case NOTHING:
			startWithTrial = true;
			
		break;
		case LOADTRIALS:
			attemptLoadTrials();
			
			if(loadedSomething == true){
				//TODO: Train the neural network with the "characterData":
				
				interruptWithTrainingThread();
				//net.clear();
				//net.train(characterData, Static.timesToTrainEach);
			}
			
			
		break;
		case LOADWEIGHTS:
			
			attemptLoadTrials();
			
			
			attemptLoadCharges();
			
			
			
		break;
		}
		

		
		if(startWithTrial){
			setGUITrial();
		}else{
			setGUIUse();
		}
		//setGUITrial();
		
		
		
		if(loadedSomething && Static.debugTestLoadImage){
			
			
			for(int i = 0; i < characterData.length; i++){
				for(int i2 = 0; i2 < characterData[i].trialMem.length; i2++){
					for(int y = 0; y < characterData[i].trialMem[i2].length; y++){
						for(int x = 0; x < characterData[i].trialMem[i2][y].length; x++){
							int modx = i * 32;
							int mody = i2 * 32;
							
							if(y + mody < drawSubPan.pixelsHeight && x + modx < drawSubPan.pixelsWidth){
								this.drawSubPan.pixels[y + mody][x + modx] = characterData[i].trialMem[i2][y][x];
							}
						}		
					}
				}
			}
			
		}//END OF if(Static.debugTestLoadImage)
		
		
	}//END OF CustomFrame(...) constructor
	
	
	

	void setGUIBlank(){
		
		clearGUI();
		
		//pan.add(drawSubPan); 
		Static.addGridBagConstraintsComp(GridBagConstraints.BOTH, GridBagConstraints.PAGE_START, 1, 1, 1, 1, 0.7,  1.0, 0, 0, pan, drawSubPan);
		this.pack();
		
		drawSubPan.createPixelsArray();
		
	}
	
	
	void setGUITrial(){
		
		clearGUI();
		
		trialsLeft = Static.trialsPerNumber;
		numberToDraw = 0;
		
		programMode = 0;
		
		
		lblMessage = new JLabel();
		lblMessage.setText(currentInstructions);
		lblMessage.setFont(fntSansSerif);
		
		
		
		
		btnConfirm = new JButton();
		btnConfirm.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				confirmClicked();
			}//END OF actionPerformed(...)
		});
		btnConfirm.setText("Confirm");
		btnConfirm.setFont(fntSansSerif);
		
		
		btnClear = new JButton();
		btnClear.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e){
				clearClicked();
			}//END OF actionPerformed(...)
		});
		btnClear.setText("Clear");
		btnClear.setFont(fntSansSerif);
		
		
		
		//pan.add(drawSubPan); 
		Static.addGridBagConstraintsComp(GridBagConstraints.BOTH, GridBagConstraints.PAGE_START, 1, 2, 2, 1, 0.7,  1.0, 0, 0, pan, drawSubPan);
				
		
		//pan.add(drawSubPan); 
		Static.addGridBagConstraintsComp(GridBagConstraints.BOTH, GridBagConstraints.PAGE_START, 1, 1, 2, 1, 0.7, 0.0, 0, 0, pan, lblMessage);
			
		
		Static.addGridBagConstraintsComp(GridBagConstraints.BOTH, GridBagConstraints.PAGE_START, 1, 3, 1, 1, 0.5, 0.0, 0, 0, pan, btnClear);
		Static.addGridBagConstraintsComp(GridBagConstraints.BOTH, GridBagConstraints.PAGE_START, 2, 3, 1, 1, 0.5, 0.0, 0, 0, pan, btnConfirm);
		
		
		//Static.addGridBagConstraintsComp(GridBagConstraints.BOTH, GridBagConstraints.PAGE_START, 1, 0, 1, 1, 0.2, 0.0, 0, 0, panel, txtPage);
		
		this.pack();
		
		drawSubPan.createPixelsArray();
		
		setupTrial(true);
		
	}
	
	
	

	void setGUIUse(){
		
		
		clearGUI();
		
		trialsLeft = Static.trialsPerNumber;
		numberToDraw = 0;
		
		programMode = 1;
		
		
		currentInstructions = "Draw one number at a time and press \"confirm\" to submit.";
		
		lblMessage = new JLabel();
		lblMessage.setText(currentInstructions);
		lblMessage.setFont(fntSansSerif);
		
		
		txtOutput = new TextField();
		txtOutput.setEditable(false);
		txtOutput.setFont(fntSansSerif);
		
		
		JButton btnTxtClear = new JButton();
		btnTxtClear.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e){
				txtOutput.setText("");
			}//END OF actionPerformed(...)
		});
		btnTxtClear.setText("Clear");
		btnTxtClear.setFont(fntSansSerif);
		
		
		
		
		
		
		
		
		
		btnConfirm = new JButton();
		btnConfirm.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				confirmClicked();
			}//END OF actionPerformed(...)
		});
		btnConfirm.setText("Confirm");
		btnConfirm.setFont(fntSansSerif);
		
		
		btnClear = new JButton();
		btnClear.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e){
				clearClicked();
			}//END OF actionPerformed(...)
		});
		btnClear.setText("Clear");
		btnClear.setFont(fntSansSerif);
		
		
		
		JButton doTrials = new JButton();
		doTrials.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				setGUITrial();
			}//END OF actionPerformed(...)
		});
		doTrials.setText("Trials");
		doTrials.setFont(fntSansSerif);
		
		

		int windowWidth = getPreferredSize().width;
		
		JPanel tempPan = new JPanel();
		tempPan.setLayout(new BoxLayout(tempPan, BoxLayout.X_AXIS));
		//This means, "I want to be as wide as the window, or as close as I can be to that".
		//Just use the minimum height necessary.
		lblMessage.setPreferredSize(new Dimension(windowWidth, 0) );
		
		//Static.addGridBagConstraintsComp(GridBagConstraints.BOTH, GridBagConstraints.PAGE_START, 1, 1, 4, 1, 1.7, 0.0, 0, 0, pan, lblMessage);
		//Static.addGridBagConstraintsComp(GridBagConstraints.BOTH, GridBagConstraints.PAGE_START, 3, 1, 2, 1, 0.0, 0.0, 0, 0, pan, doTrials);
		tempPan.add(lblMessage);
		tempPan.add(doTrials);
		Static.addGridBagConstraintsComp(GridBagConstraints.BOTH, GridBagConstraints.PAGE_START, 1, 1, 4, 1, 0.0, 0.0, 0, 0, pan, tempPan);
		
		
		
		JPanel tempPan2 = new JPanel();
		tempPan2.setLayout(new BoxLayout(tempPan2, BoxLayout.X_AXIS));
		//Static.addGridBagConstraintsComp(GridBagConstraints.BOTH, GridBagConstraints.PAGE_START, 1, 2, 4, 1, 0.7, 0.0, 0, 0, pan, txtOutput);
		txtOutput.setPreferredSize(new Dimension(windowWidth, 0) );
		tempPan2.add(txtOutput);
		tempPan2.add(btnTxtClear);
		Static.addGridBagConstraintsComp(GridBagConstraints.BOTH, GridBagConstraints.PAGE_START, 1, 2, 4, 1, 0.0, 0.0, 0, 0, pan, tempPan2);
		
		
		
		

		//pan.add(drawSubPan); 
		Static.addGridBagConstraintsComp(GridBagConstraints.BOTH, GridBagConstraints.PAGE_START, 1, 3, 4, 1, 0.7,  1.0, 0, 0, pan, drawSubPan);
		
		
		
		
		Static.addGridBagConstraintsComp(GridBagConstraints.BOTH, GridBagConstraints.PAGE_START, 1, 4, 2, 1, 0.5, 0.0, 0, 0, pan, btnClear);
		Static.addGridBagConstraintsComp(GridBagConstraints.BOTH, GridBagConstraints.PAGE_START, 3, 4, 2, 1, 0.5, 0.0, 0, 0, pan, btnConfirm);
		
		
		
		//Static.addGridBagConstraintsComp(GridBagConstraints.BOTH, GridBagConstraints.PAGE_START, 1, 0, 1, 1, 0.2, 0.0, 0, 0, panel, txtPage);
		
		
		this.pack();
		
		drawSubPan.createPixelsArray();
		
		
		
	}
	
	
	
	void clearGUI(){
		
		cancelError();
		drawSubPan.clearContents();
		
		
		this.drawSubPan.removeAll();
		this.pan.removeAll();

	}
	
	
	
	
	
	
	
	
	
	void setupTrial(boolean first){

		if(!first){
			trialsLeft --;
			
			
			
			if(trialsLeft == 0){
				trialsLeft = Static.trialsPerNumber;
				numberToDraw ++;
				
				//TODO: USED TO BE == 10, 2 for testing.
				if(numberToDraw == 10){
					
					currentInstructions = "Done.";
					lblMessage.setText(currentInstructions);
					//TODO Plug all "characterData.trialMem" 's  into
					//the neural network?
					
					//if(Static.loadMode != NOTHING){
					
						writeTrials();
						
						
						setGUIBlank();
						startTrainingThread();
						
						
						
					//}
					
					
					
					
					return;
					
					//end of trials!
				}//END OF if(numberToDraw == 10)
			}//END OF if(trialsLeft == 0)
		}//END OF if(!first)
		
		
		currentInstructions = "Write \"" + numberToDraw + "\" and press \"confirm\".  " + pluralize(trialsLeft, "trial", "trials") + " left.";
		lblMessage.setText(currentInstructions);
		
	}//END OF setupTrial(...)
	
	
	
	
	public void writeTrials(){
		
		File f = new File("memory");
		if(f.exists() == false){
			f.mkdirs();
		}

		try {
			
			//System.out.println("NULL?! " + characterData[0].trialMem[0]);
			
			FileOutputStream fileOut = new FileOutputStream("memory/" + expectedDataName);
			ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
			System.out.println("CHECK1");
			objectOut.writeObject(characterData);
			System.out.println("CHECK2");
			objectOut.close();
			fileOut.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	public void writeNeuralNetworkWeights(){
		

		try {
			
			//System.out.println("NULL?! " + characterData[0].trialMem[0]);
			
			FileOutputStream fileOut = new FileOutputStream("memory/" + expectedWeightsDataName);
			ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
			
			net.writeWeights(objectOut);
			
			//System.out.println("CHECK1");
			//objectOut.writeObject(characterData);
			//System.out.println("CHECK2");
			objectOut.close();
			fileOut.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	public void attemptLoadTrials(){
		

		if(!Static.forceNoLoad && new File("memory/" + expectedDataName).exists() ){
			
			//startWithTrial = false;
			
			try {
				FileInputStream fileIn = new FileInputStream("memory/" + expectedDataName);
				ObjectInputStream objectIn = new ObjectInputStream(fileIn);
				characterData = (TrialMemory[]) objectIn.readObject();
				objectIn.close();
				fileIn.close();
			}catch(Exception e){
				e.printStackTrace();
			}
			
			loadedSomething = true;
			
		}else{
			startWithTrial = true;
			
			//Let the user give samples, if none exist to train the network with.
		}
		
	}
	
	
	public void attemptLoadCharges(){
		
		System.out.println("expected name " + expectedWeightsDataName);
		if(!Static.forceNoLoad && new File("memory/" + expectedWeightsDataName).exists() ){
			
			System.out.println("FOUND CHARGES");
			//startWithTrial = false;
			
			
			try {
				FileInputStream fileIn = new FileInputStream("memory/" + expectedWeightsDataName);
				
				System.out.println("Load charges?");
				ObjectInputStream objectIn = new ObjectInputStream(fileIn);
				net.loadWeights(objectIn);
				
				//characterData = (TrialMemory[]) objectIn.readObject();
				
				objectIn.close();
				fileIn.close();
			}catch(Exception e){
				e.printStackTrace();
			}
			
			
		}else{
			System.out.println("DID NOT FIND CHARGES");
			
			
			if(loadedSomething){
				
				interruptWithTrainingThread();
			}
			
			//startWithTrial = true;
			//no, just train.
		}
		
		
	}
	
	
	String pluralize(int quantity, String notPlural, String plural){
		if(quantity == 1){
			return quantity + " " + notPlural;
		}else{
			return quantity + " " + plural;
		}
	}
	
	
	void showErrorMessage(String errorMessage){
		
		
		flashMessageTextError();
		
		
		lblMessage.setText(errorMessage);
		errorResetTimer.setTimer(3.2f);
		
		
		//errorResetTimer.cancel()?
	}
	
	void flashMessageTextError(){
		
		lblMessage.setForeground(Static.clrRed);
		
		errorFlashTimer1.setTimer(0, 0.14f);
		errorFlashTimer2.setTimer(0.07f, 0.21f);
		
		errorFlashTimer3.setTimer(0.65f, 0.79f);
		errorFlashTimer4.setTimer(0.72f, 0.86f);
		
		
	}
	
	
	void confirmClicked(){
		
		
		boolean[][] thisSample = drawSubPan.attemptRead();
		
		
		
		if(thisSample != null){
			
			if(Static.trialAdvancingOff == false){
				cancelError();
				drawSubPan.clearContents();
				
				int thisTrial = Static.trialsPerNumber - trialsLeft;
				
				
				
				if(programMode == 0){
					
					System.out.println("number to draw: " + numberToDraw + " Trial: " + thisTrial );
					characterData[numberToDraw].trialMem[ thisTrial ] = thisSample;
					//TODO Or, plug into neural network directly, as we receive them?
					
					setupTrial(false);
					
				}else if(programMode == 1){
					
					guess(thisSample);
				}
				
				
			}//END OF if(trialAdvancingOff == false)
			
			
			

			/*
			for(int y = 0; y < thisSample.length; y++){
				for(int x = 0; x < thisSample[0].length; x++){
					if(thisSample[y][x] == true){
						System.out.print('1');
					}else{
						System.out.print('0');
					}
					
				}
				System.out.println();
			}
			*/
			
			
			
		}//END OF if(thisSample != null)
		else{
			showErrorMessage("ERROR: Haven\'t drawn anything yet!");
			
			
		}
		
		
		
		
		
	}//END OF void confirmClicked
	
	
	
	
	void guess(boolean[][] thisSample){
		
		//TODO Plug into neural network...
		
		//...get outcome.
		
		
		int guessNumb = net.attemptTrial(thisSample);
		
		System.out.println("WHUT " + guessNumb);
		
		txtOutput.setText( txtOutput.getText() + guessNumb );
		
		
		
	}
	
	
	
	
	
	void clearClicked(){
		
		cancelError();
		
		
		drawSubPan.clearContents();
	}
	
	
	void cancelError(){
		errorResetTimer.action();
		//reset.
		
		errorFlashTimer1.cancel();
		errorFlashTimer2.cancel();
		errorFlashTimer3.cancel();
		errorFlashTimer4.cancel();
		errorResetTimer.cancel();
		
		if(lblMessage != null){
			lblMessage.setForeground(new Color(0, 0, 0));
		}
	}
	
	
	
	
	
	
	
	
}
