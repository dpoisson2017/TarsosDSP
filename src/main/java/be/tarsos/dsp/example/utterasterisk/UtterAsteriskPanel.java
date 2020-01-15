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


package be.tarsos.dsp.example.utterasterisk;

import be.tarsos.dsp.example.utterasterisk.domain.Call;
import be.tarsos.dsp.example.utterasterisk.domain.Note;
import be.tarsos.dsp.util.PitchConverter;

import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;

public class UtterAsteriskPanel extends JPanel {
    private static final long serialVersionUID = -5330666476785715988L;
    private static final double ALLOWED_PITCH_TOLERANCE_IN_PERCENT = 30.0;
    public static final int MINIMUM_DISPLAYED_FREQUENCY = 80;
    public static final int MAXIMUM_DISPLAYED_FREQUENCY = 2000;
    private double timeOfCallVerticalBar = 0;
    private long lastReset;
    private int score;
    private double currentX = 0;

    private Call animalCall;

    /*	double[] pattern={400,400,600,400,900,800,400,400,600,400,1100,900}; // in cents
        double[] timing ={3  ,1  ,4  ,4  ,4  ,6  ,3  ,1  ,4  ,4  ,4  ,6   }; //in eight notes
    */
/*    double[] pattern = {400, 500, 600, 700, 800, 900, 800, 700, 600, 500, 400, 300}; // in cents
    double[] timing = {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}; //in eight notes
*/
    ArrayList<Double> startTimeStamps = new ArrayList<Double>();
    ArrayList<Double> pitches = new ArrayList<Double>();
    ArrayList<Boolean> matches = new ArrayList<Boolean>();

    public UtterAsteriskPanel() {
        animalCall = createAnimalCall();
    }

    private Call createAnimalCall() {
        Call call = new Call();
        call.addNote(new Note(400, 1));
        call.addNote(new Note(500, 2));
        call.addNote(new Note(600, 0.5));
        call.addNote(new Note(700, 2));
        call.addNote(new Note(800, 1));
        call.addNote(new Note(900, 2));
        call.addNote(new Note(800, 1));
        call.addNote(new Note(700, 1));
        call.addNote(new Note(600, 1));
        call.addNote(new Note(500, 1));
        call.addNote(new Note(400, 1));
        call.addNote(new Note(300, 1));

        return call;
    }

    @Override
    public void paint(final Graphics g) {
        final Graphics2D graphics = (Graphics2D) g;
        initializeGraphicsContext(graphics);

        int x = calculateTimeOfCallPosition();
        if (isBeginning(x)) {
            reset();
        }

        drawCurrentPointInTimeVerticalLine(graphics, x);
        drawNotesToPlay(graphics);
        drawUserPitchDetected(graphics);
        drawScore(graphics);
    }

    private void reset() {
        lastReset = System.currentTimeMillis();
        pitches.clear();
        startTimeStamps.clear();
        currentX = 0;
    }

    private boolean isBeginning(int x) {
        return x < 3 && System.currentTimeMillis() - lastReset > 1000;
    }

    private int calculateTimeOfCallPosition() {
        return (int) (timeOfCallVerticalBar / (float) animalCall.getLengthInSeconds() * getWidth());
    }

    private void drawUserPitchDetected(Graphics2D graphics) {
        for (int i = 0; i < pitches.size(); i++) {
            double pitchInCents = pitches.get(i);
            double startTimeStamp = startTimeStamps.get(i) % animalCall.getLengthInSeconds();
            int patternX = (int) (startTimeStamp / (double) animalCall.getLengthInSeconds() * getWidth());
            int patternY = getHeight() - (int) (pitchInCents / 1200.0 * getHeight());
            boolean pitchMatches = matches.get(i);
            if (pitchMatches) {
                graphics.setColor(Color.GREEN);
            } else {
                graphics.setColor(Color.RED);
            }
            graphics.drawRect(patternX, patternY, 2, 2);
        }
    }

    private void drawNotesToPlay(Graphics2D graphics) {
        graphics.setColor(Color.GRAY);
        double currentXPosition = 0; // seconds of pause before start
        for (int i = 0; i < animalCall.numberOfNotes(); i++) {
            Note note = animalCall.getNote(i);

            double lengthInSeconds = note.getDuration();// * secondsPerQuarterNote;//seconds
            int patternWidthInPixels = (int) (lengthInSeconds / (double) animalCall.getLengthInSeconds() * getWidth());//pixels
            int patternHeightInPixels = (int) (ALLOWED_PITCH_TOLERANCE_IN_PERCENT * 2 / 1200.0 * getHeight());
            int patternX = (int) ((currentXPosition) / (double) animalCall.getLengthInSeconds() * getWidth());
            int patternY = getHeight() - (int) (note.getPitch() / 1200.0 * getHeight()) - patternHeightInPixels / 2;
            graphics.drawRect(patternX, patternY, patternWidthInPixels, patternHeightInPixels);
            currentXPosition += lengthInSeconds; //in seconds
        }
    }

    private void drawNotesToPlayNew(Graphics2D graphics) {
        graphics.setColor(Color.GREEN);
        double currentXPosition = 0;
        int previousXPosition = 0;
        int previousYPosition = 0;
        int currentX = 0;
        int currentY = 0;

        for (int i = 0; i < animalCall.numberOfNotes(); i++) {
            Note note = animalCall.getNote(i);
            currentX = (int) note.getDuration();
            currentY = (int) note.getPitch();
            graphics.drawLine(previousXPosition, previousYPosition, currentX, currentY);
            previousXPosition = currentX;
            previousYPosition = currentY;
            currentXPosition += currentX;
        }
    }

    private void drawScore(Graphics2D graphics) {
        graphics.setColor(Color.BLACK);
        if (lastReset != 0) {
            graphics.drawString("Score: " + String.valueOf(score), getWidth() / 2, 20);
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

    private boolean matchesExpectedPitch(double startTimeStamp, double currentXPositionInSeconds, double lengthInSeconds, double pitchInCents, int patternIndex) {
        //System.out.println(startTimeStamp + "\t" + currentXPositionInSeconds + "\t" + lengthInSeconds + "\t" + pitchInCents + "\t" + patternIndex);
        return startTimeStamp > currentXPositionInSeconds && startTimeStamp <= currentXPositionInSeconds + lengthInSeconds && Math.abs(pitchInCents - animalCall.getNote(patternIndex).getPitch()) < ALLOWED_PITCH_TOLERANCE_IN_PERCENT;
    }

    public void addDetectedFrequency(double timestamp, double frequency) {
        timeOfCallVerticalBar = timestamp % animalCall.getLengthInSeconds();

        if (allowBandPassFilter(frequency, MINIMUM_DISPLAYED_FREQUENCY, MAXIMUM_DISPLAYED_FREQUENCY)) {
            double pitchInCents = PitchConverter.hertzToRelativeCent(frequency);
            pitches.add(pitchInCents);
            startTimeStamps.add(timestamp);

            Note expectedNote = animalCall.at(timestamp);
            boolean match = Math.abs(pitchInCents - expectedNote.getPitch()) < ALLOWED_PITCH_TOLERANCE_IN_PERCENT;

            if (match) {
                score++;
            }

            matches.add(match);
        }
        this.repaint();
    }

    private boolean allowBandPassFilter(double frequency, int lowerBound, int upperBound) {
        return frequency > lowerBound && frequency < upperBound;
    }
}
