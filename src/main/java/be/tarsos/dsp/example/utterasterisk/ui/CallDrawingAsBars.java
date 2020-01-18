package be.tarsos.dsp.example.utterasterisk.ui;

import be.tarsos.dsp.example.utterasterisk.domain.call.expected.Call;
import be.tarsos.dsp.example.utterasterisk.domain.call.expected.Note;

import java.awt.*;
import javax.swing.*;

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

            double lengthInSeconds = note.getDuration();
            int barWidthInPixels = secondToPixelConverter.convert(lengthInSeconds);
            int barHeightInPixels = (int) (errorTolerance / 2);
            int barXPosition = secondToPixelConverter.convert(currentXPosition);
            int barYPosition = parent.getHeight() - hzToPixelConverter.convert(note.getPitch()) - hzToPixelConverter.convert(errorTolerance);

            graphics.fillRect(barXPosition, barYPosition, barWidthInPixels, barHeightInPixels);
            currentXPosition += lengthInSeconds;
        }
    }
}