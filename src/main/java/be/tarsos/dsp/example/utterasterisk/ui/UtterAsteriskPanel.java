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
import be.tarsos.dsp.example.utterasterisk.domain.call.expected.Call;
import be.tarsos.dsp.example.utterasterisk.domain.call.expected.CallFactory;
import be.tarsos.dsp.example.utterasterisk.domain.call.expected.Note;
import be.tarsos.dsp.example.utterasterisk.domain.call.detected.DetectedNote;
import be.tarsos.dsp.example.utterasterisk.domain.comparison.NoteComparator;
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
    private double currentX = 0;
    private double errorToleranceInPercent;
    private CallDrawingStrategy callDrawingStrategy;
    private DetectedCallDrawingStrategy detectedCallDrawingStrategy;
    private List<Filter> filters = new ArrayList<Filter>();
    private CallFactory callFactory;
    private NoteComparator noteComparator;
    private Scoreboard scoreBoard;

    private Call animalCall;
    private DetectedCall detectedCall;

    public UtterAsteriskPanel(double errorToleranceInPercent,
                              List<Filter> filters,
                              CallFactory callFactory,
                              NoteComparator noteComparator,
                              Scoreboard scoreBoard) {
        this.errorToleranceInPercent = errorToleranceInPercent;
        this.callDrawingStrategy = new CallDrawingAsBars(errorToleranceInPercent, this);
        this.detectedCallDrawingStrategy = new DetectedCallDrawingAsPoints(this);
        this.filters.addAll(filters);
        this.callFactory = callFactory;
        this.noteComparator = noteComparator;
        animalCall = this.callFactory.createDefault();
        detectedCall = new DetectedCall(animalCall);
        this.scoreBoard = scoreBoard;
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
        drawNotesDetected(graphics);
        drawScore(graphics);
    }

    private void reset() {
        lastReset = System.currentTimeMillis();
        detectedCall.clear();
        currentX = 0;
        scoreBoard.reset();
    }

    private boolean isBeginning(int x) {
        return x < 3 && System.currentTimeMillis() - lastReset > 1000;
    }

    private int calculateTimeOfCallPosition() {
        return (int) (timeOfCallVerticalBar / (float) animalCall.getLengthInSeconds() * getWidth());
    }

    private void drawNotesDetected(Graphics2D graphics) {
        detectedCallDrawingStrategy.draw(graphics, detectedCall);
    }

    private void drawNotesToPlay(Graphics2D graphics) {
        callDrawingStrategy.draw(graphics, animalCall);
    }

    private void drawScore(Graphics2D graphics) {
        graphics.setColor(Color.BLACK);
        if (lastReset != 0) {
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

    public void addDetectedFrequency(double timestamp, double frequency) {
        timeOfCallVerticalBar = timestamp % animalCall.getLengthInSeconds();

        boolean passesAllFilters = filters.stream().allMatch(filter-> filter.filter(frequency));
        if (passesAllFilters) {
            DetectedNote detectedNote = new DetectedNote(PitchConverter.hertzToRelativeCent(frequency), timestamp);

            Note expectedNote = animalCall.at(timestamp);
            noteComparator.compare(expectedNote, detectedNote);
            detectedCall.addNote(detectedNote);
        }
        this.repaint();
    }
}
