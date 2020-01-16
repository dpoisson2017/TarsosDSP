package be.tarsos.dsp.example.utterasterisk.domain.call.expected;

public class CallFactory {

    public Call createDefault() {
        Call call = new Call();
        call.addNote(new Note(400, 1));
        call.addNote(new Note(500, 2));
        call.addNote(new Note(600, 0.5));
        call.addNote(new Note(700, 2));
        call.addNote(new Note(800, 1));
        call.addNote(new Note(900, 2));
        call.addNote(new Note(800, 1));
        call.addNote(new Note(700, 1));
        call.addNote(new Note(600, 1));
        call.addNote(new Note(500, 1));
        call.addNote(new Note(400, 1));
        call.addNote(new Note(300, 1));

        return call;
    }
}
