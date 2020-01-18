package be.tarsos.dsp.example.utterasterisk.ui;

import be.tarsos.dsp.example.utterasterisk.domain.call.detected.DetectedCall;
import be.tarsos.dsp.example.utterasterisk.domain.call.detected.DetectedNote;

import javax.swing.*;
import java.awt.*;

public class DetectedCallDrawingAsPoints implements DetectedCallDrawingStrategy {

    private static final double UNKNOWN_CONSTANT = 1200.0; // TODO Figure what this is for
    private static final int RECTANGLE_WIDTH = 2;
    private static final int RECTANGLE_HEIGHT = 2;

    private HzToPixelConverter hzToPixelConverter;
    private SecondToPixelConverter secondToPixelConverter;

    private JPanel parent;

    public DetectedCallDrawingAsPoints(JPanel parent, HzToPixelConverter hzToPixelConverter, SecondToPixelConverter secondToPixelConverter) {
        this.parent = parent;
        this.hzToPixelConverter = hzToPixelConverter;
        this.secondToPixelConverter = secondToPixelConverter;
    }

    @Override
    public void draw(Graphics2D graphics, DetectedCall call) {
        for (DetectedNote note: call.getNotes()) {
            double startTimeStamp = note.getSecondsFromStart() % call.getLengthInSeconds();
            int patternX = (int) (startTimeStamp / (double) call.getLengthInSeconds() * parent.getWidth());
            int patternY = parent.getHeight() - (int) (note.getPitch() / UNKNOWN_CONSTANT * parent.getHeight());

            if (note.isMatch()) {
                graphics.setColor(Color.GREEN);
            } else {
                graphics.setColor(Color.RED);
            }
            graphics.drawRect(patternX, patternY, RECTANGLE_WIDTH, RECTANGLE_HEIGHT);
        }
    }
}
