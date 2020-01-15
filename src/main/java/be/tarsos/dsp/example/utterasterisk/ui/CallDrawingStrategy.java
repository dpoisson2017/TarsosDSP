package be.tarsos.dsp.example.utterasterisk.ui;

import be.tarsos.dsp.example.utterasterisk.domain.call.Call;

import java.awt.*;

public interface CallDrawingStrategy {
    void draw(Graphics2D graphics, Call call);
}
