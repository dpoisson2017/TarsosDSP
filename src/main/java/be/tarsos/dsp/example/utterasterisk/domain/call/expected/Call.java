package be.tarsos.dsp.example.utterasterisk.domain.call.expected;

import java.util.ArrayList;
import java.util.List;

public class Call {
    private List<Note> notes = new ArrayList<>();
    private double lengthInSeconds = 0;
    private CallId id;

    public void addNote(Note note) {
        notes.add(note);
        lengthInSeconds += note.getDuration();
    }

    public Note getNote(int index) {
        return notes.get(index);
    }

    public Note at(double secondsFromStart) {
        double cumulativeLength = 0;
        for (Note note : notes) {
            double previousNoteEnding = cumulativeLength;
            cumulativeLength += note.getDuration();
            if (secondsFromStart > previousNoteEnding && secondsFromStart <= cumulativeLength) {
                return note;
            }
        }
        return notes.get(0);
    }

    public int numberOfNotes() {
        return notes.size();
    }

    public double getLengthInSeconds() {
        return lengthInSeconds;
    }

    public CallId getId() {
        return id;
    }
}
