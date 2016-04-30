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
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Timer;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;



public class CustomFrame extends JFrame{
	
	boolean loadedAnyReceivedSamples = false;
	
	CustomButton buttonsForNumbers[];
	
	SingleSamplePanel samplePan;
	
	public CustomFrame thisFrameRef;
	private static final long serialVersionUID = -5763319334266549575L;
	
	int recentGuess;
	boolean[][] recentGuessDraw;
	
	boolean dueForWeightUpdate = true;
	
	boolean doTrialsForAll = false;
	
	CustomButton deleteButton;
	
	public int trialManagerBoxWidth = 50;
	public int trialManagerBoxHeight = 50;
	
	public int trialManagerTrialBoxWidth = Static.groupPixelsWidth* 2 + 6;
	public int trialManagerTrialBoxHeight = Static.groupPixelsHeight * 2 + 6;
	
	public int trialManagerTrialBoxOffX = 4;
	public int trialManagerTrialBoxOffY = 4;
	
	public int numberSelected = 0;
	
	public NeuralNetwork net;
	
	public CustomPanel pan;
	public DrawablePanel drawSubPan;
	
	public Thread trainingThread;
	
	public boolean queueSaveTrials = false;
	public boolean startWithTrial = false;

	public TextField txtOutput;
	
	public CustomButton btnConfirm;
	public CustomButton btnClear;
	
	public CustomLabel lblMessage;
	public CustomLabel lblMessage2;
	
	public CustomButton trainButton;
	
	public TrialMemory[] characterData;
	
	public String currentInstructions;
	
	public boolean drawSpecialRect = false;
	public int specialRectx1 = 0;
	public int specialRecty1 = 0;
	public int specialRectx2 = 0;
	public int specialRecty2 = 0;
	
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
	
	boolean endlessTrials = false;
	int trialsLeft = 0;
	int numberToDraw = 0;
	
	boolean loadedSomething = false;
	
	
	void interruptWithTrainingThread(){
		trainingThread = new Thread(){
			@Override
			public void run(){
				net.clear();
				net.train(characterData, Static.timesToTrainEach);
				
				
				dueForWeightUpdate = false;
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
				
				dueForWeightUpdate = false;
				writeNeuralNetworkWeights();
				setGUIUse();
				
			}
		};
		trainingThread.start();
	}
	
	public CustomFrame(int defaultSize_x, int defaultSize_y){
		super();
		
		thisFrameRef = this;
		
		characterData = new TrialMemory[10];
		for(int i = 0; i < characterData.length; i++){
			characterData[i] = new TrialMemory();
		}
		
		
		
		
		this.addKeyListener(new KeyListener(){
			@Override
			public void keyPressed(KeyEvent event) {
				
			}
			@Override
			public void keyReleased(KeyEvent event) {
				int keyCode = event.getKeyCode();
				if(keyCode == KeyEvent.VK_ENTER){
					if(drawSubPan.disableDrawing == false){
						
						if(programMode == 1){
							for(int i = 0; i < 10; i++){
								buttonsForNumbers[i].setEnabled(true);
							}
						}
						
						
						confirmClicked();
					}
					
				}
			}
			@Override
			public void keyTyped(KeyEvent event) {
				
			}
		});
		
		
		final CustomFrame frameRef = this;
		this.addComponentListener(new ComponentListener(){
			@Override
			public void componentHidden(ComponentEvent arg0) {
				
			}
			@Override
			public void componentMoved(ComponentEvent arg0) {
				
			}
			@Override
			public void componentResized(ComponentEvent arg0) {
				if(lblMessage != null){
					lblMessage.setPreferredSize(new Dimension(frameRef.getWidth() * 1, 0));
				}
			}
			@Override
			public void componentShown(ComponentEvent arg0) {
				
			}
		});
		
		drawSubPan = new DrawablePanel(this);
		drawSubPan.setPreferredSize(new Dimension(0, 0) );
		
		//credit to
		//http://stackoverflow.com/questions/1984195/close-window-event-in-java
		this.addWindowListener(new WindowAdapter(){
	        public void windowClosing(WindowEvent e){
	        	
	        	if(Static.saveOnExit && queueSaveTrials){
	        		writeTrials();
	        	}
	        	writeUpdateChargesVar();
	        	drawSubPan.endPaintUpdate = true;
	        	//cancel that thread, if it is still running.
	        }
	    });
		
		expectedDataName = "characterData" + Static.groupPixelsWidth + "x" + Static.groupPixelsHeight + ".dat";
		expectedWeightsDataName = "charges" + Static.numbOfInputNeurons+ "_" + Static.numbOfNeuronsPerHiddenLayer + "_" + Static.numberOfHiddenLayers + "_" + Static.numberOfOutputNeurons + ".dat";
		
		
		
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
		
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		pan = new CustomPanel();
		
		pan.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		pan.setLayout(new GridBagLayout());
		
		pan.setPreferredSize(new Dimension(0, 0));
		this.setPreferredSize(new Dimension(defaultSize_x, defaultSize_y)) ;
		
		this.pack();
		
		this.getContentPane().add(pan);
		this.setResizable(Static.windowIsResizable);
		
		this.setVisible(true);
		setGUIBlank();
		
		net = new NeuralNetwork(Static.numbOfInputNeurons, Static.numbOfNeuronsPerHiddenLayer, Static.numberOfHiddenLayers, Static.numberOfOutputNeurons, true, drawSubPan);
		
		readUpdateChargesVar();
		
		switch(Static.loadMode){
		case NOTHING:
			startWithTrial = true;
		break;
		case LOADTRIALS:
			attemptLoadTrials();
			if(loadedSomething == true){
				if(Static.networkdNeedsTrainPrompt){
					setGUIPrompt();
				}else if(Static.autoTrain){
					interruptWithTrainingThread();
				}else{
					dueForWeightUpdate = true;
				}
			}
		break;
		case LOADWEIGHTS:
			attemptLoadTrials();
			attemptLoadCharges();
		break;
		}
		
		if(startWithTrial){

			attemptLoadReceivedSamples();
			
			if(!loadedAnyReceivedSamples){
				doTrialsForAll = true;
				numberToDraw = 0;
				trialsLeft = Static.trialsPerNumber;
				setGUITrial();
			}else{
				setGUIUse();
			}
			
			
		}else{
			
			if(Static.networkdNeedsTrainPrompt && dueForWeightUpdate){
				setGUIPrompt();
			}else{
				setGUIUse();
			}
			
		}
		//setGUITrial();
		
	}//END OF CustomFrame(...) constructor
	
	void betterPack(){
		this.setPreferredSize(new Dimension(getWidth(), getHeight()));
		//this.pan.setPreferredSize(new Dimension(pan.getWidth(), pan.getHeight()));
		this.pack();
	}
	
	void setGUIBlank(){
		clearGUI();
		drawSubPan.disableDrawing = true;
		Static.addGridBagConstraintsComp(GridBagConstraints.BOTH, GridBagConstraints.PAGE_START, 1, 1, 1, 1, 0.7,  1.0, 0, 0, pan, drawSubPan);
		
		betterPack();
		drawSubPan.createPixelsArray();
	}
	
	void drawSubPanClicked(int x, int y){
		boolean failed = false;
		int width = 0;
		
		int choice = -1;
		
		switch(drawSubPan.trialManagerIndex){
		case 0:
			width = drawSubPan.getWidth() - (drawSubPan.getWidth() % trialManagerBoxWidth);
			
			if(x >= 0 && y >= 0 && x < width ){
				int row = y / trialManagerBoxHeight;
				int col = x / trialManagerBoxWidth;
				
				choice = row * (width / trialManagerBoxWidth) + col;
				if(choice < characterData.length){
					
					System.out.println("YOU CLICKED BOX NUMBER " + choice);
				}else{
					return;
				}
			}
			if(choice != -1){
				numberSelected = choice;
				setGUITrialManager(1);
			}
			
		break;
		case 1:
			width = drawSubPan.getWidth() - (drawSubPan.getWidth() % trialManagerTrialBoxWidth);
			
			if(x >= 0 && y >= 0 && x < width ){
				int row = y / trialManagerTrialBoxHeight;
				int col = x / trialManagerTrialBoxWidth;
				
				choice = row * (width / trialManagerTrialBoxWidth) + col;
				if(choice < characterData[numberSelected].trialMem.size()){
					
					setSpecialRect(choice);
					drawSubPan.repaint();
					
					System.out.println("YOU CLICKED BOX NUMBER " + choice);
				}else{
					failed = true;
				}
			}else{
				failed = true;
			}
			
			if(choice != -1){
				trialSelected = choice;
				deleteButton.setEnabled(true);
				for(int i2 = 0; i2 < 10; i2++){
					if(i2 != numberSelected){
						buttonsForNumbers[i2].setEnabled(true);
					}
				}
				
			}else{
				failed = true;
			}
			
			if(failed){
				drawSpecialRect = false;
				trialSelected = -1;
				deleteButton.setEnabled(false);
				drawSubPan.repaint();
			}
		break;
		}//END OF Switch
		
	}
	
	
	void setSpecialRect(int choice){
		
		int width = drawSubPan.getWidth() - drawSubPan.getWidth() % trialManagerTrialBoxWidth;
		
		int tempx = (trialManagerTrialBoxWidth*(choice) ) % (width);
		int xMod = tempx ;
		int yMod = ((trialManagerTrialBoxWidth*choice)/(width)) * trialManagerTrialBoxHeight;
		
		drawSpecialRect = true;
		specialRectx1 = xMod;
		specialRectx2 = xMod + trialManagerTrialBoxWidth;
		specialRecty1 = yMod;
		specialRecty2 = yMod + trialManagerTrialBoxHeight;
		
	}
	
	
	void resizeActHandle(){
		
		switch(drawSubPan.trialManagerIndex){
		case 0:
			showAvailableNumbers();
		break;
		case 1:
			showAvailableTrials();
		break;
		}
		drawSubPan.repaint();
	}
	
	void showAvailableNumbers(){
		drawSubPan.clearContents();
		int width = drawSubPan.getWidth() - (drawSubPan.getWidth() % trialManagerBoxWidth);
		
		int rows = (int)Math.ceil( ( ((characterData.length) * trialManagerBoxWidth ) / ((float)width) ));
		int desiredHeight = rows   *trialManagerBoxHeight ;
		
		drawSubPan.setPreferredSize(new Dimension(0, desiredHeight) );
		drawSubPan.setSize(0, desiredHeight);
		

		betterPack();
		
		drawSubPan.attemptResizePixelArray(false);
		
		int height = drawSubPan.getHeight();
		
		for(int i = 0; i < characterData.length; i++){
			int tempx = (trialManagerBoxWidth*(i) ) % (width);
			
			int xMod = tempx ;
			int yMod = ((trialManagerBoxWidth*i)/(width)) * trialManagerBoxHeight;
			
			drawSubPan.drawTrialManagerNumberCoordsX[i] = xMod + trialManagerBoxWidth/2;
			drawSubPan.drawTrialManagerNumberCoordsY[i] = yMod + trialManagerBoxHeight/2;
			
			//System.out.println("BOX # " + i + " COORDS : " + xMod + " " + yMod + " : " + (xMod + trialManagerBoxWidth) + " " +  (yMod + trialManagerBoxHeight) );
			
			drawSubPan.drawSquare(xMod, yMod, xMod + trialManagerBoxWidth, yMod + trialManagerBoxHeight);
			
		}
	}
	
	void showAvailableTrials(){
		drawSubPan.clearContents();
		int width = drawSubPan.getWidth() - (drawSubPan.getWidth() % trialManagerTrialBoxWidth);
		
		int rows = (int)Math.ceil( ( ((characterData[numberSelected].trialMem.size()) * trialManagerTrialBoxWidth ) / ((float)width) ));
		
		int desiredHeight = rows   *trialManagerTrialBoxHeight ;

		drawSubPan.setPreferredSize(new Dimension(0, desiredHeight) );
		drawSubPan.setSize(0, desiredHeight);
		
		betterPack();
		
		drawSubPan.attemptResizePixelArray(false);
		
		int height = drawSubPan.getHeight();
		
		for(int i = 0; i < characterData[numberSelected].trialMem.size(); i++){
			
			int tempx = (trialManagerTrialBoxWidth*(i) ) % (width);
			int xMod = tempx ;
			int yMod = ((trialManagerTrialBoxWidth*i)/(width)) * trialManagerTrialBoxHeight;
			
			//System.out.println("BOX # " + i + " COORDS : " + xMod + " " + yMod + " : " + (xMod + trialManagerBoxWidth) + " " +  (yMod + trialManagerBoxHeight) );
			
			drawSubPan.drawSquare(xMod, yMod, xMod + trialManagerTrialBoxWidth, yMod + trialManagerTrialBoxHeight);
			
			//if(null == null)
			//	continue;
				
			for(int y = 0; y < characterData[numberSelected].trialMem.get(i).length; y++){
				for(int x = 0; x < characterData[numberSelected].trialMem.get(i)[y].length; x++){
					
					int xModd = xMod + trialManagerTrialBoxOffX;
					int yModd = yMod + trialManagerTrialBoxOffY;
					
					drawSubPan.pixels[yModd + y*2][xModd + x*2] = characterData[numberSelected].trialMem.get(i)[y][x];
					drawSubPan.pixels[yModd + y*2+1][xModd + x*2] = characterData[numberSelected].trialMem.get(i)[y][x];
					drawSubPan.pixels[yModd + y*2][xModd + x*2+1] = characterData[numberSelected].trialMem.get(i)[y][x];
					drawSubPan.pixels[yModd + y*2+1][xModd + x*2+1] = characterData[numberSelected].trialMem.get(i)[y][x];
					
				}		
			}
		}//END OF for(int i = 0...)
	}
	
	void deleteTrial(int numberOfTrial, int trialIndex){
		characterData[numberOfTrial].trialMem.remove(trialIndex);
		
		showAvailableTrials();
	}
	
	int trialSelected = -1;
	
	void setGUITrialManager(int actionIndex){
		
		clearGUI();
		drawSubPan.disableDrawing = true;
		
		trialsLeft = Static.trialsPerNumber;
		numberToDraw = 0;
		
		programMode = 2;
		
		
		lblMessage = new CustomLabel();
		
		CustomButton backButton = new CustomButton();
		backButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				
				switch(drawSubPan.trialManagerIndex){
				case 0:
					drawSubPan.trialManagerIndex = -1;
					numberSelected = -1;
					trialSelected = -1;
					
					if(queueSaveTrials){
						writeTrials();
						if(Static.autoTrain){
							setGUIBlank();
							startTrainingThread();
						}else{
							setGUIUse();
						}
					}else{
						setGUIUse();
					}
				break;
				case 1:
					drawSpecialRect = false;
					trialSelected = -1;
					setGUITrialManager(0);
				break;
				};
				
			}//END OF actionPerformed(...)
		});
		
		CustomLabel moveTrial = new CustomLabel(true);
		moveTrial.setText("   Move to: ");
		
		buttonsForNumbers = new CustomButton[10];
		
		for(int i = 0; i < 10; i++){
			final int index = i;
			buttonsForNumbers[i] = new CustomButton(true);
			buttonsForNumbers[i].addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e){
					if(trialSelected != -1){
						alterTrial(numberSelected, trialSelected, index);
						queueSaveTrials = true;
						dueForWeightUpdate = true;
					}//END OF if(trialSelected != -1)
					
				}//END OF actionPerformed(...)
			});
			buttonsForNumbers[i].setText(String.valueOf(i));
			buttonsForNumbers[i].setEnabled(false);
			
		}
		
		
		
		
		deleteButton = new CustomButton(true);
		deleteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				alterTrial(numberSelected, trialSelected, -1);
			}//END OF actionPerformed(...)
		});
		deleteButton.setText("Delete");
		deleteButton.setEnabled(false);
		
		CustomButton add1Trial = new CustomButton(true);
		add1Trial.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				
				trialsLeft = 1;
				numberToDraw = numberSelected;
				setGUITrial(true);
				queueSaveTrials = true;
				dueForWeightUpdate = true;
				
			}//END OF actionPerformed(...)
		});
		add1Trial.setText("Add trial");
		add1Trial.setEnabled(false);
		
		CustomButton add5Trial = new CustomButton(true);
		add5Trial.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				
				trialsLeft = 5;
				numberToDraw = numberSelected;
				setGUITrial(true);
				queueSaveTrials = true;
				dueForWeightUpdate = true;
				
				
			}//END OF actionPerformed(...)
		});
		add5Trial.setText("Add 5");
		add5Trial.setEnabled(false);
		
		CustomButton add10Trial = new CustomButton(true);
		add10Trial.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				
				trialsLeft = 10;
				numberToDraw = numberSelected;
				setGUITrial(true);
				queueSaveTrials = true;
				dueForWeightUpdate = true;
				
			}//END OF actionPerformed(...)
		});
		add10Trial.setText("Add 10");
		add10Trial.setEnabled(false);
		
		CustomButton addEndlessTrial = new CustomButton(true);
		addEndlessTrial.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				
				trialsLeft = 0;
				numberToDraw = numberSelected;
				endlessTrials = true;
				setGUITrial(true);
				queueSaveTrials = true;
				dueForWeightUpdate = true;
				
			}//END OF actionPerformed(...)
		});
		addEndlessTrial.setText("Add Endless");
		addEndlessTrial.setEnabled(false);
		
		if(actionIndex == 0 && queueSaveTrials){
			backButton.setText("Save / Back");
		}else{
			backButton.setText("Back");
		}
		
		
		JPanel tempPan = new JPanel();
		tempPan.setLayout(new BoxLayout(tempPan, BoxLayout.X_AXIS));
		//This means, "I want to be as wide as the window, or as close as I can be to that".
		//Just use the minimum height necessary.
		lblMessage.setPreferredSize(new Dimension(getWidth(), 0) );
		
		tempPan.add(lblMessage);
		tempPan.add(backButton);
		Static.addGridBagConstraintsComp(GridBagConstraints.BOTH, GridBagConstraints.PAGE_START, 1, 1, 2, 1, 0.0, 0.0, 0, 0, pan, tempPan);
		
		JPanel tempPan2 = new JPanel();
		tempPan2.setLayout(new BoxLayout(tempPan2, BoxLayout.X_AXIS));
		tempPan2.add(add1Trial);
		tempPan2.add(add5Trial);
		tempPan2.add(add10Trial);
		tempPan2.add(addEndlessTrial);
		
		tempPan2.add(deleteButton);
		tempPan2.add(moveTrial);
		for(int i = 0; i < 10; i++){
			tempPan2.add(buttonsForNumbers[i]);
		}
		
		Static.addGridBagConstraintsComp(GridBagConstraints.BOTH, GridBagConstraints.PAGE_START, 1, 2, 2, 1, 0.0, 0.0, 0, 0, pan, tempPan2);
		
		JScrollPane scr = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		
		
		//thanks,
		//http://stackoverflow.com/questions/5583495/how-do-i-speed-up-the-scroll-speed-in-a-jscrollpane-when-using-the-mouse-wheel
		scr.getHorizontalScrollBar().setUnitIncrement(16);
		scr.getVerticalScrollBar().setUnitIncrement(16);
		
		scr.getViewport().add(drawSubPan);
		
		Static.addGridBagConstraintsComp(GridBagConstraints.BOTH, GridBagConstraints.PAGE_START, 1, 3, 2, 1, 0.7,  1.0, 0, 0, pan, scr);
		
		betterPack();
		
		drawSubPan.trialManagerIndex = actionIndex;
		switch(drawSubPan.trialManagerIndex){
		case 0:
			currentInstructions = "Pick a number.";
			betterPack();
			drawSubPan.resizedSince = false;
			showAvailableNumbers();
			
		break;
		case 1:
			add1Trial.setEnabled(true);
			add5Trial.setEnabled(true);
			add10Trial.setEnabled(true);
			addEndlessTrial.setEnabled(true);
			
			currentInstructions = "Remove or add trials.";
			betterPack();
			
			drawSubPan.resizedSince = false;
			showAvailableTrials();
		break;
		}
		lblMessage.setText(currentInstructions);
		
		drawSubPan.repaint();
		
	}
	
	
	
	void setGUIPrompt(){
		clearGUI();
		
		currentInstructions = "The network needs to be trained.  Do that now?";
		lblMessage = new CustomLabel();
		lblMessage.setText(currentInstructions);
		
		drawSubPan.disableDrawing = true;

		CustomButton btnYes = new CustomButton();
		btnYes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				setGUIBlank();
				startTrainingThread();
				drawSubPan.disableDrawing = false;
				
			}//END OF actionPerformed(...)
		});
		btnYes.setText("Yes");
		
		
		CustomButton btnNo = new CustomButton();
		btnNo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e){
				setGUIUse();
				drawSubPan.disableDrawing = false;
			}//END OF actionPerformed(...)
		});
		btnNo.setText("No");
		
		Static.addGridBagConstraintsComp(GridBagConstraints.BOTH, GridBagConstraints.PAGE_START, 1, 1, 2, 1, 0.7, 0.0, 0, 0, pan, lblMessage);
		
		Static.addGridBagConstraintsComp(GridBagConstraints.BOTH, GridBagConstraints.PAGE_START, 1, 2, 2, 1, 0.7,  1.0, 0, 0, pan, drawSubPan);
		
		
		Static.addGridBagConstraintsComp(GridBagConstraints.BOTH, GridBagConstraints.PAGE_START, 1, 3, 1, 1, 0.5, 0.0, 0, 0, pan, btnYes);
		Static.addGridBagConstraintsComp(GridBagConstraints.BOTH, GridBagConstraints.PAGE_START, 2, 3, 1, 1, 0.5, 0.0, 0, 0, pan, btnNo);
		

		betterPack();
		
		drawSubPan.createPixelsArray();
	}
	
	
	void setGUITrial(){
		setGUITrial(false);
	}
	
	void setGUITrial(boolean canExit){
		
		clearGUI();
		
		drawSubPan.disableDrawing = false;
		
		
		programMode = 0;
		
		
		lblMessage = new CustomLabel();
		lblMessage.setText(currentInstructions);
		
		drawSubPan.trialManagerIndex = -1;
		
		
		btnConfirm = new CustomButton();
		btnConfirm.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				confirmClicked();
			}//END OF actionPerformed(...)
		});
		btnConfirm.setText("Confirm");
		
		
		btnClear = new CustomButton();
		btnClear.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e){
				clearClicked();
			}//END OF actionPerformed(...)
		});
		btnClear.setText("Clear");
		
		
		//JPanel tempPan = new JPanel();
		//tempPan.setLayout(new BoxLayout(tempPan, BoxLayout.X_AXIS));
		
		//A GUI Offset for the gridbag.  If the exit button is added, everything else gets an offset.
		int guix = 1;
		if(canExit){
			
			CustomButton btnBack = new CustomButton();
			btnBack.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e){
					endlessTrials = false;
					setGUITrialManager(1);
				}//END OF actionPerformed(...)
			});
			btnBack.setText("Back");
			
			Static.addGridBagConstraintsComp(GridBagConstraints.BOTH, GridBagConstraints.PAGE_START, guix++, 3, 1, 1, 0.5, 0.0, 0, 0, pan, btnBack);
			//tempPan.add(btnBack);
		}
		
		Static.addGridBagConstraintsComp(GridBagConstraints.BOTH, GridBagConstraints.PAGE_START, ++guix, 3, 1, 1, 0.5, 0.0, 0, 0, pan, btnClear);
		Static.addGridBagConstraintsComp(GridBagConstraints.BOTH, GridBagConstraints.PAGE_START, ++guix, 3, 1, 1, 0.5, 0.0, 0, 0, pan, btnConfirm);
		//tempPan.add(btnClear);
		//tempPan.add(btnConfirm);
		Static.addGridBagConstraintsComp(GridBagConstraints.BOTH, GridBagConstraints.PAGE_START, 1, 2, guix, 1, 0.7,  1.0, 0, 0, pan, drawSubPan);
		
		Static.addGridBagConstraintsComp(GridBagConstraints.BOTH, GridBagConstraints.PAGE_START, 1, 1, guix, 1, 0.7, 0.0, 0, 0, pan, lblMessage);
		
		
		betterPack();
		drawSubPan.createPixelsArray();
		setupTrial();
	}
	
	
	void setGUIUse(){
		
		clearGUI();
		
		drawSubPan.disableDrawing = false;
		
		trialsLeft = Static.trialsPerNumber;
		numberToDraw = 0;
		
		programMode = 1;
		
		
		if(dueForWeightUpdate && !Static.networkdNeedsTrainPrompt){
			currentInstructions = "NOTICE: samples altered, training required!";
		}else{
			currentInstructions = "Draw one number at a time and press \"confirm\" to submit.";
			
		}
		
		lblMessage = new CustomLabel();
		lblMessage.setText(currentInstructions);
		
		
		txtOutput = new TextField();
		txtOutput.setEditable(true);
		txtOutput.setFont(Static.fntSansSerif);
		//txtOutput.setFocusable(false);
		
		
		CustomButton btnTxtClear = new CustomButton();
		btnTxtClear.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e){
				txtOutput.setText("");
			}//END OF actionPerformed(...)
		});
		btnTxtClear.setText("Clear");
		
		
		CustomLabel lblMessage2a = new CustomLabel(true); 
		lblMessage2a.setText("Trial: " );
		
		samplePan = new SingleSamplePanel();
		samplePan.setPreferredSize(new Dimension(Static.groupPixelsWidth, Static.groupPixelsHeight));
		samplePan.setSize(new Dimension(Static.groupPixelsWidth, Static.groupPixelsHeight));
		samplePan.setMaximumSize(new Dimension(Static.groupPixelsWidth, Static.groupPixelsHeight));
		samplePan.setFocusable(false);
		
		
		lblMessage2 = new CustomLabel(true);
		lblMessage2.setText("   Guess:    " );
		
		CustomLabel lblMessage3 = new CustomLabel(true);
		lblMessage3.setText("    Intention? :  " );
		
		CustomLabel lblSpacer = new CustomLabel();
		lblSpacer.setText("     " );
		
		
		trainButton = new CustomButton(true);
		trainButton.setText("Train");
		trainButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e){
				//dueForWeightUpdate = false;
				queueSaveTrials = false;
				setGUIBlank();
				startTrainingThread();
				
				
			}//END OF actionPerformed(...)
		});
		trainButton.setEnabled(dueForWeightUpdate);
		

		buttonsForNumbers = new CustomButton[10];
		
		for(int i = 0; i < 10; i++){
			final int index = i;
			buttonsForNumbers[i] = new CustomButton(true);
			buttonsForNumbers[i].addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e){
					for(int i2 = 0; i2 < 10; i2++){
						buttonsForNumbers[i2].setEnabled(false);
					}
					characterData[index].trialMem.add(recentGuessDraw);
					queueSaveTrials = true;
					dueForWeightUpdate = true;
					trainButton.setEnabled(true);
				}//END OF actionPerformed(...)
			});
			buttonsForNumbers[i].setText(String.valueOf(i));
			buttonsForNumbers[i].setEnabled(false);
			
		}
		
		
		
		btnConfirm = new CustomButton();
		btnConfirm.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				
				for(int i = 0; i < 10; i++){
					buttonsForNumbers[i].setEnabled(true);
				}
				
				confirmClicked();
				
			}//END OF actionPerformed(...)
		});
		btnConfirm.setText("Confirm");
		btnConfirm.setFocusable(false);
		
		btnClear = new CustomButton();
		btnClear.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e){
				clearClicked();
			}//END OF actionPerformed(...)
		});
		btnClear.setText("Clear");
		
		
		CustomButton doTrials = new CustomButton();
		doTrials.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				setGUITrialManager(0);
			}//END OF actionPerformed(...)
		});
		doTrials.setText("Trials");
		
		
		
		
		
		int windowWidth = getPreferredSize().width;
		
		JPanel tempPan = new JPanel();
		tempPan.setLayout(new BoxLayout(tempPan, BoxLayout.X_AXIS));
		//This means, "I want to be as wide as the window, or as close as I can be to that".
		//Just use the minimum height necessary.
		lblMessage.setPreferredSize(new Dimension(getWidth(), 0) );
		
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
		
		
		JPanel tempPan3 = new JPanel();
		tempPan3.setLayout(new BoxLayout(tempPan3, BoxLayout.X_AXIS));
		//Static.addGridBagConstraintsComp(GridBagConstraints.BOTH, GridBagConstraints.PAGE_START, 1, 2, 4, 1, 0.7, 0.0, 0, 0, pan, txtOutput);
		
		tempPan3.add(lblMessage2a);
		tempPan3.add(samplePan);
		tempPan3.add(lblMessage2);
		tempPan3.add(lblMessage3);
		
		for(int i = 0; i < 10; i++){
			tempPan3.add(buttonsForNumbers[i]);
		}
		tempPan3.add(lblSpacer);
		tempPan3.add(trainButton);
		
		Static.addGridBagConstraintsComp(GridBagConstraints.BOTH, GridBagConstraints.PAGE_START, 1, 3, 4, 1, 0.0, 0.0, 0, 0, pan, tempPan3);
		
		Static.addGridBagConstraintsComp(GridBagConstraints.BOTH, GridBagConstraints.PAGE_START, 1, 4, 4, 1, 0.7,  1.0, 0, 0, pan, drawSubPan);

		Static.addGridBagConstraintsComp(GridBagConstraints.BOTH, GridBagConstraints.PAGE_START, 1, 5, 2, 1, 0.5, 0.0, 0, 0, pan, btnClear);
		Static.addGridBagConstraintsComp(GridBagConstraints.BOTH, GridBagConstraints.PAGE_START, 3, 5, 2, 1, 0.5, 0.0, 0, 0, pan, btnConfirm);
		
		betterPack();
		drawSubPan.createPixelsArray();
	}
	
	
	void clearGUI(){
		cancelError();
		drawSubPan.clearContents();
		
		this.drawSubPan.removeAll();
		this.pan.removeAll();

	}
	
	
	void setupTrial(){

		trialSelected = -1;
		drawSpecialRect = false;
		
		if(!endlessTrials){
			if(trialsLeft == 0){
				if(doTrialsForAll){
					//To the next.
					trialsLeft = Static.trialsPerNumber;
					numberToDraw ++;
					
					if(numberToDraw == 10){
						currentInstructions = "Done.";
						lblMessage.setText(currentInstructions);
					
						doTrialsForAll = false;
						writeTrials();
						
						
						if(Static.autoTrain){
							setGUIBlank();
							startTrainingThread();
						}else{
							dueForWeightUpdate = true;
							setGUIUse();
						}
						return;
						//end of trials!
					}//END OF if(numberToDraw == 10)
					
				}else{
					queueSaveTrials = true;
					dueForWeightUpdate = true;
					setGUITrialManager(1);
					return;
				}//END OF else statement.
				
			}//END OF if(trialsLeft == 0)
		
			currentInstructions = "Write \"" + numberToDraw + "\" and press \"confirm\".  " + pluralize(trialsLeft, "trial", "trials") + " left.";
			trialsLeft --;
		
		}else{
			queueSaveTrials = true;
			dueForWeightUpdate = true;
			currentInstructions = "Write \"" + numberToDraw + "\" and press \"confirm\".  " + pluralize(trialsLeft, "trial", "trials") + " drawn so far.";
			trialsLeft++;
		}
		
		lblMessage.setText(currentInstructions);
		
	}//END OF setupTrial(...)
	
	
	public void readUpdateChargesVar(){

		if(new File("memory").exists() == false){
			new File("memory").mkdir();
		}
		
		if(new File("memory/dueForCharges.dat").exists()){
			try {
				FileInputStream fileIn = new FileInputStream("memory/dueForCharges.dat");
				ObjectInputStream objectIn = new ObjectInputStream(fileIn);
				dueForWeightUpdate = objectIn.readBoolean();
				objectIn.close();
				fileIn.close();
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}//END OF if(new File("memory/dueForCharges.dat").exists())
		
	}
	
	
	public void writeUpdateChargesVar(){

		try {
			FileOutputStream fileOut = new FileOutputStream("memory/dueForCharges.dat");
			ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
			objectOut.writeBoolean(dueForWeightUpdate);
			objectOut.close();
			fileOut.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	public void writeTrials(){
		queueSaveTrials = false;
		
		File f = new File("memory");
		if(f.exists() == false){
			f.mkdirs();
		}

		try {
			
			FileOutputStream fileOut = new FileOutputStream("memory/" + expectedDataName);
			ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
			objectOut.writeObject(characterData);
			objectOut.close();
			fileOut.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	public void writeNeuralNetworkWeights(){
		try {
			
			FileOutputStream fileOut = new FileOutputStream("memory/" + expectedWeightsDataName);
			ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
			
			net.writeWeights(objectOut);
			
			objectOut.close();
			fileOut.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	
	public void attemptLoadReceivedSamples(){
		
		File ff = new File("SAMPLE RECEIVER");
		if(ff.exists() == false){
			ff.mkdir();
		}
		File[] files = ff.listFiles();
		
		for(int i = 0; i < files.length; i++){
			attemptLoadReceivedSample(files[i].toString());
			
		}
		
	}
	public void attemptLoadReceivedSample(String path){
		
		boolean fail = false;
		
		if(path.endsWith(".dat")){
			try {
				FileInputStream fileIn = new FileInputStream(path);
				ObjectInputStream objectIn = new ObjectInputStream(fileIn);
				TrialMemory[] temp = (TrialMemory[]) objectIn.readObject();
				objectIn.close();
				fileIn.close();
				
				if(Static.recentLoadedTrialLength2 != Static.groupPixelsHeight || Static.recentLoadedTrialLength3 != Static.groupPixelsWidth){
					System.out.println("WARNING: Could not load " + path + " due to bad dimensions.");
					System.out.println("Expected: " + Static.groupPixelsWidth + "x" + Static.groupPixelsHeight + " Received: " + Static.recentLoadedTrialLength3 + "x" + Static.recentLoadedTrialLength2);
					fail = true;
				}else{
					System.out.println("~loaded " + path);
				}
				
				if(!fail){
					loadedAnyReceivedSamples = true;
					dueForWeightUpdate = true;
					queueSaveTrials = true;
					for(int i = 0; i < characterData.length; i++){
						characterData[i].trialMem.addAll(temp[i].trialMem);
					}
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if(!fail){
				
				File f = new File("SAMPLE PROCESSED");
				if(f.exists() == false){
					f.mkdir();
				}
				
				//File f = new File(path);
				//f.delete();
				File from = new File(path);
				String name = from.getName();
				
				int attemptedNewNameNumber = 1;
				
				String destPath = "SAMPLE PROCESSED" + Static.fileSep + name.substring(0, name.length() -4 );
				//System.out.println("DEST PATH " +destPath);
				String destPathTry = destPath + ".dat";
				while(true){
					File dest = new File(destPathTry);
					if(!dest.exists()){
						from.renameTo(dest);
						break;
					}
					attemptedNewNameNumber++;
					destPathTry = destPath + " (" + attemptedNewNameNumber + ").dat";
				}
				
				
				
			}//END OF if(!fail)
		}
		
	}
	
	public void attemptLoadTrials(){
		
		if(!Static.forceNoLoad && new File("memory/" + expectedDataName).exists() ){
			
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
			
			attemptLoadReceivedSamples();
			if(loadedAnyReceivedSamples){
				queueSaveTrials = true;
			}
			
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
				
				objectIn.close();
				fileIn.close();
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}else{
			System.out.println("DID NOT FIND CHARGES");
			
			if(loadedSomething){
				if(Static.networkdNeedsTrainPrompt){
					setGUIPrompt();
				}else if(Static.autoTrain){
					interruptWithTrainingThread();
				}else{
					dueForWeightUpdate = true;
				}
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
					characterData[numberToDraw].trialMem.add(thisSample);
					
					setupTrial();
				}else if(programMode == 1){
					guess(thisSample);
					
				}
			}//END OF if(trialAdvancingOff == false)
			
		}//END OF if(thisSample != null)
		else{
			showErrorMessage("ERROR: Haven\'t drawn anything yet!");
		}
		
	}//END OF void confirmClicked
	
	
	void guess(boolean[][] thisSample){
		recentGuessDraw = thisSample;
		int guessNumb = net.attemptTrial(thisSample);
		
		System.out.println("guessNumb " + guessNumb);
		
		txtOutput.setText( txtOutput.getText() + guessNumb );
		recentGuess = guessNumb;
		lblMessage2.setText("   Guess : " + guessNumb);
		samplePan.mySample = recentGuessDraw;
		samplePan.repaint();
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
	
	void moveTrial(int numb, int trial, int destChar){
		
		
		boolean[][] someTrial = characterData[numb].trialMem.get(trial);
		characterData[numb].trialMem.remove(trial);
		
		characterData[destChar].trialMem.add(someTrial);
		showAvailableTrials();
	}
	
	
	void alterTrial(int numb, int trial, int destChar){
		
		if(trialSelected != -1){
			if(destChar == -1){
				deleteTrial(numb, trial);
			}
			else{
				moveTrial(numb, trial, destChar);
				
			}
			queueSaveTrials = true;
			dueForWeightUpdate = true;
			
			if(trialSelected >= characterData[numberSelected].trialMem.size()){
				trialSelected--;
				if(trialSelected != -1){
					setSpecialRect(trialSelected);
				}else{
					drawSpecialRect = false;
					deleteButton.setEnabled(false);
					for(int i2 = 0; i2 < 10; i2++){
						buttonsForNumbers[i2].setEnabled(false);
					}
				}
			}
		
		}//END OF if(trialSelected != -1) (outtermost)
		
		
		
	}
	
	
	
	
	
	
}
