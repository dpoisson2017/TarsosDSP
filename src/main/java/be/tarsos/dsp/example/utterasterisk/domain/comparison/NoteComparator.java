package be.tarsos.dsp.example.utterasterisk.domain.comparison;

import be.tarsos.dsp.example.utterasterisk.domain.Scoreboard;
import be.tarsos.dsp.example.utterasterisk.domain.call.expected.Note;
import be.tarsos.dsp.example.utterasterisk.domain.call.detected.DetectedNote;

public class NoteComparator {
    private Scoreboard scoreboard;
    private double errorTolerance;

    public NoteComparator(double errorTolerance, Scoreboard scoreboard) {
        this.errorTolerance = errorTolerance;
        this.scoreboard = scoreboard;
    }

    public void compare(Note expected, DetectedNote actual) {
        boolean matches = Math.abs(actual.getPitch() - expected.getPitch()) < errorTolerance;

        if(matches) {
            actual.matches();
            scoreboard.addMatch();
        } else {
            actual.differs();
        }
    }
}
