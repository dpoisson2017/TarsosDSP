/*
*      _______                       _____   _____ _____  
*     |__   __|                     |  __ \ / ____|  __ \ 
*        | | __ _ _ __ ___  ___  ___| |  | | (___ | |__) |
*        | |/ _` | '__/ __|/ _ \/ __| |  | |\___ \|  ___/ 
*        | | (_| | |  \__ \ (_) \__ \ |__| |____) | |     
*        |_|\__,_|_|  |___/\___/|___/_____/|_____/|_|     
*                                                         
* -------------------------------------------------------------
*
* TarsosDSP is developed by Joren Six at IPEM, University Ghent
*  
* -------------------------------------------------------------
*
*  Info: http://0110.be/tag/TarsosDSP
*  Github: https://github.com/JorenSix/TarsosDSP
*  Releases: http://0110.be/releases/TarsosDSP/
*  
*  TarsosDSP includes modified source code by various authors,
*  for credits and info, see README.
* 
*/


package be.tarsos.dsp.example;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;

import javax.swing.JPanel;

import be.tarsos.dsp.util.PitchConverter;

public class UtterAsteriskPanel extends JPanel {
	private static final long serialVersionUID = -5330666476785715988L;
	private static final double ALLOWED_PITCH_TOLERANCE_IN_PERCENT = 30.0;
	public static final int MINIMUM_DISPLAYED_FREQUENCY = 80;
	public static final int MAXIMUM_DISPLAYED_FREQUENCY = 2000;
	public static final double INITIAL_PAUSE_DELAY_IN_SECONDS = 0.5;
	private double patternLengthInSeconds = 12;
	private double currentMarker = 0;
	private long lastReset;
	private int score;
	private double patternLengthInQuarterNotes;


/*	double[] pattern={400,400,600,400,900,800,400,400,600,400,1100,900}; // in cents
	double[] timing ={3  ,1  ,4  ,4  ,4  ,6  ,3  ,1  ,4  ,4  ,4  ,6   }; //in eight notes
*/
	double[] pattern={400}; // in cents
	double[] timing ={20 }; //in eight notes


	ArrayList<Double> startTimeStamps = new ArrayList<Double>();
	ArrayList<Double> pitches = new ArrayList<Double>();
	
	public UtterAsteriskPanel(){
		patternLengthInQuarterNotes = calculatePatternLengthInQuarterNotes();
	}

	private int calculatePatternLengthInQuarterNotes() {
		int length = 0;
		for(double timeInQuarterNotes : timing){
			length+=timeInQuarterNotes;
		}
		return length;
	}

	
	@Override
	public void paint(final Graphics g) {
		final Graphics2D graphics = (Graphics2D) g;
		initializeGraphicsContext(graphics);

		int x = calculateCurrentPositionOnScreen();
		if(isBeginning(x)){
			reset();
		}

		drawCurrentPointInTimeVerticalLine(graphics, x);
		drawScore(graphics);
		drawNotesToPlay(graphics);
		drawUserPitchDetected(graphics);
	}

	private void reset() {
		lastReset = System.currentTimeMillis();
		calculateScore();
		pitches.clear();
		startTimeStamps.clear();
	}

	private boolean isBeginning(int x) {
		return x < 3 && System.currentTimeMillis() - lastReset > 1000;
	}

	private int calculateCurrentPositionOnScreen() {
		return (int) (currentMarker / (float) patternLengthInSeconds * getWidth());
	}

	private void drawUserPitchDetected(Graphics2D graphics) {
		graphics.setColor(Color.RED);
		for(int i = 0 ; i < pitches.size() ; i++){
			double pitchInCents = pitches.get(i);
			double startTimeStamp = startTimeStamps.get(i) % patternLengthInSeconds;
			int patternX = (int) ( startTimeStamp / (double) patternLengthInSeconds * getWidth());
			int patternY = getHeight() - (int) (pitchInCents / 1200.0 * getHeight());
			graphics.drawRect(patternX, patternY, 2,2);
		}
	}

	private void drawNotesToPlay(Graphics2D graphics) {
		graphics.setColor(Color.GRAY);
		double lengthPerQuarterNoteInSeconds = patternLengthInSeconds /patternLengthInQuarterNotes; // in seconds per quarter note
		double currentXPosition = INITIAL_PAUSE_DELAY_IN_SECONDS; // seconds of pause before start
		for(int i = 0 ; i < pattern.length ; i++){
			double lengthInSeconds = timing[i] * lengthPerQuarterNoteInSeconds;//seconds
			int patternWidth = (int) ( lengthInSeconds / (double) patternLengthInSeconds * getWidth());//pixels
			int patternHeight = (int) (ALLOWED_PITCH_TOLERANCE_IN_PERCENT / 1200.0 * getHeight());
			int patternX = (int) ( (currentXPosition) / (double) patternLengthInSeconds * getWidth());
			int patternY = getHeight() - (int) (pattern[i] / 1200.0 * getHeight()) - patternHeight/2 ;
			graphics.drawRect(patternX, patternY, patternWidth, patternHeight);
			currentXPosition += lengthInSeconds; //in seconds
		}
	}

	private void drawScore(Graphics2D graphics) {
		graphics.setColor(Color.BLACK);
		if(lastReset != 0){
			graphics.drawString("Score: " + String.valueOf(score), getWidth()/2, 20);
		}
	}

	private void initializeGraphicsContext(Graphics2D graphics) {
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
				RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		graphics.setBackground(Color.WHITE);
		graphics.clearRect(0, 0, getWidth(), getHeight());
	}

	private void drawCurrentPointInTimeVerticalLine(Graphics2D graphics, int x) {
		graphics.setColor(Color.BLUE);
		graphics.drawLine(x, 0, x, getHeight());
	}

	
	private void calculateScore(){
		score = 0;
		for(int i = 0 ; i < pitches.size() ; i++){
			double pitchInCents = pitches.get(i);
			double startTimeStamp = startTimeStamps.get(i) % patternLengthInSeconds;
			if(startTimeStamp > INITIAL_PAUSE_DELAY_IN_SECONDS && startTimeStamp <= INITIAL_PAUSE_DELAY_IN_SECONDS + INITIAL_PAUSE_DELAY_IN_SECONDS * pattern.length){
				double lengthPerQuarterNoteInSeconds = patternLengthInSeconds /patternLengthInQuarterNotes; // in seconds per quarter note
				double currentXPosition = INITIAL_PAUSE_DELAY_IN_SECONDS; // seconds of pause before start
				for(int j = 0 ; j < pattern.length ; j++){
					double lengthInSeconds = timing[j] * lengthPerQuarterNoteInSeconds;//seconds
					if(matchesExpectedPitch(startTimeStamp, currentXPosition, lengthInSeconds, pitchInCents, j)){
						score++;
					}
					currentXPosition += lengthInSeconds; //in seconds
				}
			}
		}
	}

	private boolean matchesExpectedPitch(double startTimeStamp, double currentXPositionInSeconds, double lengthInSeconds, double pitchInCents, int patternIndex) {
		return startTimeStamp > currentXPositionInSeconds && startTimeStamp <= currentXPositionInSeconds + lengthInSeconds && Math.abs(pitchInCents-pattern[patternIndex]) < ALLOWED_PITCH_TOLERANCE_IN_PERCENT;
	}
	
	public void addDetectedFrequency(double timeStamp, double frequency){
		currentMarker = timeStamp % patternLengthInSeconds;

		if(canBeDisplayed(frequency)){
			double pitchInCents = PitchConverter.hertzToRelativeCent(frequency);
			pitches.add(pitchInCents);
			startTimeStamps.add(timeStamp);
		}
		this.repaint();
	}

	private boolean canBeDisplayed(double frequency) {
		//ignore everything outside 80-2000Hz
		return frequency > MINIMUM_DISPLAYED_FREQUENCY && frequency < MAXIMUM_DISPLAYED_FREQUENCY;
	}
}
