package be.tarsos.dsp.example.utterasterisk.domain.call.expected;

public class CallFactory {

    public Call createDefault() {
        Call call = new Call();
        call.addNote(new Note(800, 1));
        call.addNote(new Note(900, 2));
        call.addNote(new Note(1000, 0.5));
        call.addNote(new Note(1100, 1.5));
        call.addNote(new Note(1200, 1));
        call.addNote(new Note(1300, 2));
        call.addNote(new Note(1200, 1));
        call.addNote(new Note(1100, 1));
        call.addNote(new Note(1000, 1));
        call.addNote(new Note(900, 1));
        call.addNote(new Note(800, 1));
        call.addNote(new Note(700, 1));

        return call;
    }
}
