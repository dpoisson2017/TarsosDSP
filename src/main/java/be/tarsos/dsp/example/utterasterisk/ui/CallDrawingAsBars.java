package be.tarsos.dsp.example.utterasterisk.ui;

import be.tarsos.dsp.example.utterasterisk.domain.call.expected.Call;
import be.tarsos.dsp.example.utterasterisk.domain.call.expected.Note;

import javax.swing.*;
import java.awt.*;

public class CallDrawingAsBars implements CallDrawingStrategy {
    private double errorTolerance;
    private JPanel parent;
    private HzToPixelConverter hzToPixelConverter;
    private SecondToPixelConverter secondToPixelConverter;

    public CallDrawingAsBars(double errorTolerance, JPanel parent, HzToPixelConverter hzToPixelConverter, SecondToPixelConverter secondToPixelConverter) {
        this.errorTolerance = errorTolerance;
        this.parent = parent;
        this.hzToPixelConverter = hzToPixelConverter;
        this.secondToPixelConverter = secondToPixelConverter;
    }

    @Override
    public void draw(Graphics2D graphics, Call call) {
        graphics.setColor(Color.BLUE);
        double currentXPosition = 0; // seconds of pause before start
        for (int i = 0; i < call.numberOfNotes(); i++) {
            Note note = call.getNote(i);

            double lengthInSeconds = note.getDuration();// * secondsPerQuarterNote;//seconds
            int barWidthInPixels = secondToPixelConverter.convert(lengthInSeconds);//(int) (lengthInSeconds / (double) call.getLengthInSeconds() * parent.getWidth());//pixels
            int barHeightInPixels = (int)(errorTolerance/2);//(int) (errorTolerance * 2 / 1200.0 * parent.getHeight());
            //int patternX = (int) ((currentXPosition) / (double) call.getLengthInSeconds() * parent.getWidth());
            int barXPosition = secondToPixelConverter.convert(currentXPosition);
            //int patternY = parent.getHeight() - (int) (note.getPitch() / 1200.0 * parent.getHeight()) - patternHeightInPixels / 2;
            int barYPosition = parent.getHeight() - hzToPixelConverter.convert(note.getPitch()) - hzToPixelConverter.convert(errorTolerance);
            System.out.println("x=" + barXPosition + " y=" + barYPosition + " width=" + barWidthInPixels + " height=" + barHeightInPixels);
            graphics.drawRect(barXPosition, barYPosition, barWidthInPixels, barHeightInPixels);
            currentXPosition += lengthInSeconds; //in seconds
        }
    }
}
