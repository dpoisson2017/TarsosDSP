package be.tarsos.dsp.example.utterasterisk.ui;

import be.tarsos.dsp.example.utterasterisk.domain.call.detected.DetectedCall;

import java.awt.*;

public interface DetectedCallDrawingStrategy {
    void draw(Graphics2D graphics, DetectedCall call);
}
