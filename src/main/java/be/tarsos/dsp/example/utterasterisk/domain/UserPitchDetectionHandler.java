package be.tarsos.dsp.example.utterasterisk.domain;

import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.example.utterasterisk.ui.UtterAsteriskPanel;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;

public class UserPitchDetectionHandler implements PitchDetectionHandler {
    private double startTime;
    private UtterAsteriskPanel panel; // TODO domain depends on UI

    public UserPitchDetectionHandler(UtterAsteriskPanel panel, double startTime) {
        this.panel = panel;
        this.startTime = startTime;
    }

    @Override
    public void handlePitch(PitchDetectionResult pitchDetectionResult, AudioEvent audioEvent) {
        double secondsSinceStart = audioEvent.getTimeStamp();
        System.out.println("Pitch: " + pitchDetectionResult.getPitch() + "\tsecondsSinceStart: " + secondsSinceStart);
        float pitch = pitchDetectionResult.getPitch();
        panel.addDetectedFrequency(secondsSinceStart, pitch);
    }
}
