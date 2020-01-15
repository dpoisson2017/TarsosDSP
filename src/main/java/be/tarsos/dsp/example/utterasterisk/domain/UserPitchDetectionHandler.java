package be.tarsos.dsp.example.utterasterisk.domain;

import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;

public class UserPitchDetectionHandler implements PitchDetectionHandler {

    @Override
    public void handlePitch(PitchDetectionResult pitchDetectionResult, AudioEvent audioEvent) {
        double timeStamp = audioEvent.getTimeStamp();
//        System.out.println("Pitch: " + pitchDetectionResult.getPitch() + "\tisPitched: " + pitchDetectionResult.getPitch() + "\tprobability: " + pitchDetectionResult.getProbability() + "\ttimestamp: " + timeStamp);
        float pitch = pitchDetectionResult.getPitch();
        //panel.addDetectedFrequency(timeStamp, pitch);
    }
}
