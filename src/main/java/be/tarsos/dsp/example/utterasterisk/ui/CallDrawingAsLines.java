package be.tarsos.dsp.example.utterasterisk.ui;

import be.tarsos.dsp.example.utterasterisk.domain.call.expected.Call;
import be.tarsos.dsp.example.utterasterisk.domain.call.expected.Note;

import javax.swing.*;
import java.awt.*;

public class CallDrawingAsLines implements CallDrawingStrategy {

    private double errorTolerance;
    private JPanel parent;

    public CallDrawingAsLines(double errorTolerance, JPanel parent) {
        this.errorTolerance = errorTolerance;
        this.parent = parent;
    }

    @Override
    public void draw(Graphics2D graphics, Call call) {
        graphics.setColor(Color.GREEN);
        double currentXPosition = 0;
        int previousXPosition = 0;
        int previousYPosition = 0;
        int currentX = 0;
        int currentY = 0;

        for (int i = 0; i < call.numberOfNotes(); i++) {
            Note note = call.getNote(i);
            currentX = (int) note.getDuration();
            currentY = (int) note.getPitch();
            graphics.drawLine(previousXPosition, previousYPosition, currentX, currentY);
            previousXPosition = currentX;
            previousYPosition = currentY;
            currentXPosition += currentX;
        }
    }
}
