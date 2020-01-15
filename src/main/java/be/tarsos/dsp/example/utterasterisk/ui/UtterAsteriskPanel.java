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


package be.tarsos.dsp.example.utterasterisk.ui;

import be.tarsos.dsp.example.utterasterisk.domain.call.Call;
import be.tarsos.dsp.example.utterasterisk.domain.call.CallFactory;
import be.tarsos.dsp.example.utterasterisk.domain.call.Note;
import be.tarsos.dsp.example.utterasterisk.domain.filter.Filter;
import be.tarsos.dsp.util.PitchConverter;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class UtterAsteriskPanel extends JPanel {
    private static final long serialVersionUID = -5330666476785715988L;
    private double timeOfCallVerticalBar = 0;
    private long lastReset;
    private int score;
    private double currentX = 0;
    private double errorToleranceInPercent;
    private CallDrawingStrategy callDrawingStrategy;
    private List<Filter> filters = new ArrayList<Filter>();
    private CallFactory callFactory;

    private Call animalCall;

    ArrayList<Double> startTimeStamps = new ArrayList<Double>();
    ArrayList<Double> pitches = new ArrayList<Double>();
    ArrayList<Boolean> matches = new ArrayList<Boolean>();

    public UtterAsteriskPanel(double errorToleranceInPercent, List<Filter> filters, CallFactory callFactory) {
        this.errorToleranceInPercent = errorToleranceInPercent;
        this.callDrawingStrategy = new CallDrawingAsBars(errorToleranceInPercent, this);
        this.filters.addAll(filters);
        this.callFactory = callFactory;
        animalCall = this.callFactory.createDefault();
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
        score = 0;
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
        callDrawingStrategy.draw(graphics, animalCall);
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
        return startTimeStamp > currentXPositionInSeconds && startTimeStamp <= currentXPositionInSeconds + lengthInSeconds && Math.abs(pitchInCents - animalCall.getNote(patternIndex).getPitch()) < errorToleranceInPercent;
    }

    public void addDetectedFrequency(double timestamp, double frequency) {
        timeOfCallVerticalBar = timestamp % animalCall.getLengthInSeconds();

        boolean passesAllFilters = filters.stream().allMatch(filter-> filter.filter(frequency));
        if (passesAllFilters) {
            double pitchInCents = PitchConverter.hertzToRelativeCent(frequency);
            pitches.add(pitchInCents);
            startTimeStamps.add(timestamp);

            Note expectedNote = animalCall.at(timestamp);
            boolean match = Math.abs(pitchInCents - expectedNote.getPitch()) < errorToleranceInPercent;

            if (match) {
                score++;
            }

            matches.add(match);
        }
        this.repaint();
    }
}
