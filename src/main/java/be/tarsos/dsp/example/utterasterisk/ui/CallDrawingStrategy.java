package be.tarsos.dsp.example.utterasterisk.ui;

import be.tarsos.dsp.example.utterasterisk.domain.call.expected.Call;

import java.awt.*;

public interface CallDrawingStrategy {
    void draw(Graphics2D graphics, Call call);
}
