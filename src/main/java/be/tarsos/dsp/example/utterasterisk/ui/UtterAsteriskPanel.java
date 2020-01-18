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

import be.tarsos.dsp.example.utterasterisk.domain.Scoreboard;
import be.tarsos.dsp.example.utterasterisk.domain.call.detected.DetectedCall;
import be.tarsos.dsp.example.utterasterisk.domain.call.detected.DetectedNote;
import be.tarsos.dsp.example.utterasterisk.domain.call.expected.Call;
import be.tarsos.dsp.example.utterasterisk.domain.call.expected.Note;
import be.tarsos.dsp.example.utterasterisk.domain.comparison.NoteComparator;
import be.tarsos.dsp.example.utterasterisk.domain.filter.Filter;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public class UtterAsteriskPanel extends JPanel {
    private static final long serialVersionUID = -5330666476785715988L;
    private double timeOfCallVerticalBar = 0;
    private long startTimeOfCall;
    private double currentX = 0;
    private double errorToleranceInPercent;
    private CallDrawingStrategy callDrawingStrategy;
    private DetectedCallDrawingStrategy detectedCallDrawingStrategy;
    private List<Filter> filters = new ArrayList<Filter>();
    private NoteComparator noteComparator;
    private Scoreboard scoreBoard;
    private int minimumFrequency;
    private int maximumFrequency;

    private HzToPixelConverter hzToPixelConverter;
    private SecondToPixelConverter secondToPixelConverter;

    private Call animalCall;
    private DetectedCall detectedCall;

    public UtterAsteriskPanel(int minimumFrequency,
                              int maximumFrequency,
                              double errorToleranceInPercent,
                              List<Filter> filters,
                              Call call,
                              NoteComparator noteComparator,
                              Scoreboard scoreBoard) {
        this.minimumFrequency = minimumFrequency;
        this.maximumFrequency = maximumFrequency;
        this.errorToleranceInPercent = errorToleranceInPercent;
        this.filters.addAll(filters);
        this.noteComparator = noteComparator;
        this.scoreBoard = scoreBoard;
        animalCall = call;
        detectedCall = new DetectedCall(animalCall);

        this.hzToPixelConverter = new HzToPixelConverter(this, maximumFrequency);
        this.secondToPixelConverter = new SecondToPixelConverter(this, animalCall.getLengthInSeconds());

        this.callDrawingStrategy = new CallDrawingAsBars(errorToleranceInPercent, this, hzToPixelConverter, secondToPixelConverter);
        this.detectedCallDrawingStrategy = new DetectedCallDrawingAsPoints(this, hzToPixelConverter, secondToPixelConverter);

    }

    @Override
    public void paint(final Graphics g) {
        final Graphics2D graphics = (Graphics2D) g;
        initializeGraphicsContext(graphics);

        int x = calculateTimeOfCallPosition();
        if (isBeginning(x)) {
            reset();
        }

        drawScales(graphics);
        drawCurrentPointInTimeVerticalLine(graphics, x);
        drawNotesToPlay(graphics);
        drawNotesDetected(graphics);
        drawScore(graphics);
    }

    private void reset() {
        startTimeOfCall = System.currentTimeMillis();
        detectedCall.clear();
        currentX = 0;
        scoreBoard.reset();
    }

    private boolean isBeginning(int x) {
        return x < 3 && System.currentTimeMillis() - startTimeOfCall > 1000;
    }

    private int calculateTimeOfCallPosition() {
        return (int) (timeOfCallVerticalBar / (float) animalCall.getLengthInSeconds() * getWidth());
        //return (int) timeOfCallVerticalBar;
    }

    private void drawNotesDetected(Graphics2D graphics) {
        detectedCallDrawingStrategy.draw(graphics, detectedCall);
    }

    private void drawNotesToPlay(Graphics2D graphics) {
        callDrawingStrategy.draw(graphics, animalCall);
    }

    private void drawScales(Graphics2D graphics) {
        drawHzScale(graphics);
        drawSecondsScale(graphics);
    }

    private void drawSecondsScale(Graphics2D graphics) {
        double widthOfOneSecondInPixels = getWidth() / animalCall.getLengthInSeconds();
        int midCallValue = (int) (animalCall.getLengthInSeconds() / 2);
        int currentSecond = 1;
        while (currentSecond < animalCall.getLengthInSeconds()) {
            StringBuffer axisValueString = new StringBuffer();
            axisValueString.append(String.valueOf(currentSecond));
            if (currentSecond == midCallValue) {
                axisValueString.append(" (s)");
            }

            int positionXAxis = (int) (currentSecond * widthOfOneSecondInPixels);
            if (currentSecond % 5 == 0) {
                graphics.setColor(Color.BLACK);
            } else {
                graphics.setColor(Color.LIGHT_GRAY);
            }

            graphics.drawLine(positionXAxis, 0, positionXAxis, getHeight());
            graphics.drawString(axisValueString.toString(), positionXAxis, getHeight());
            currentSecond++;
        }
    }

    private void drawHzScale(Graphics2D graphics) {
        String axisName = "(Hz)";
        double heightOfOneHzInPixels = ((double) getHeight() / maximumFrequency);
        int midFrequencyValue = maximumFrequency / 2;
        int currentHz = minimumFrequency;
        while (currentHz < maximumFrequency) {
            int positionYAxis = (int) (getHeight() - (currentHz * heightOfOneHzInPixels));
            if (currentHz % 500 == 0) {
                graphics.setColor(Color.BLACK);
                graphics.drawLine(0, positionYAxis, getWidth(), positionYAxis);
                StringBuffer axisValueString = new StringBuffer();
                axisValueString.append(String.valueOf(currentHz));
                if (currentHz % midFrequencyValue == 0) {
                    axisValueString.append(" " + axisName);
                }
                graphics.drawString(axisValueString.toString(), 0, positionYAxis);
                currentHz += 100;
            } else if (currentHz % 100 == 0) {
                graphics.setColor(Color.LIGHT_GRAY);
                graphics.drawLine(0, positionYAxis, getWidth(), positionYAxis);
                graphics.drawString(String.valueOf(currentHz), 0, positionYAxis);
                currentHz += 100;
            } else {
                currentHz++;
            }
        }
    }

    private void drawScore(Graphics2D graphics) {
        graphics.setColor(Color.BLACK);
        if (startTimeOfCall != 0) {
            graphics.drawString("Score: " + String.valueOf(scoreBoard.getScore()), getWidth() / 2, 20);
        }
    }

    private void initializeGraphicsContext(Graphics2D graphics) {
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        graphics.setBackground(Color.WHITE);
        graphics.clearRect(0, 0, getWidth(), getHeight());
    }

    private void drawCurrentPointInTimeVerticalLine(Graphics2D graphics, int x) {
        graphics.setColor(Color.BLUE);
        graphics.drawLine(x, 0, x, getHeight());
    }

    public void addDetectedFrequency(double secondsFromStart, double frequency) {
        timeOfCallVerticalBar = secondsFromStart;

        boolean passesAllFilters = filters.stream().allMatch(filter -> filter.filter(frequency));
        if (passesAllFilters) {
            DetectedNote detectedNote = new DetectedNote(frequency, secondsFromStart);

            Note expectedNote = animalCall.at(secondsFromStart);
            noteComparator.compare(expectedNote, detectedNote);
            detectedCall.addNote(detectedNote);
        }
        this.repaint();
    }
}
