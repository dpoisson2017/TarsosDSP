package be.tarsos.dsp.example.utterasterisk.ui;

import be.tarsos.dsp.example.utterasterisk.domain.call.Call;
import be.tarsos.dsp.example.utterasterisk.domain.call.Note;

import javax.swing.*;
import java.awt.*;

public class CallDrawingAsBars implements CallDrawingStrategy {

    private double errorTolerance;
    private JPanel parent;

    public CallDrawingAsBars(double errorTolerance, JPanel parent) {
        this.errorTolerance = errorTolerance;
        this.parent = parent;
    }

    @Override
    public void draw(Graphics2D graphics, Call call) {
        graphics.setColor(Color.GRAY);
        double currentXPosition = 0; // seconds of pause before start
        for (int i = 0; i < call.numberOfNotes(); i++) {
            Note note = call.getNote(i);

            double lengthInSeconds = note.getDuration();// * secondsPerQuarterNote;//seconds
            int patternWidthInPixels = (int) (lengthInSeconds / (double) call.getLengthInSeconds() * parent.getWidth());//pixels
            int patternHeightInPixels = (int) (errorTolerance * 2 / 1200.0 * parent.getHeight());
            int patternX = (int) ((currentXPosition) / (double) call.getLengthInSeconds() * parent.getWidth());
            int patternY = parent.getHeight() - (int) (note.getPitch() / 1200.0 * parent.getHeight()) - patternHeightInPixels / 2;
            graphics.drawRect(patternX, patternY, patternWidthInPixels, patternHeightInPixels);
            currentXPosition += lengthInSeconds; //in seconds
        }
    }
}
