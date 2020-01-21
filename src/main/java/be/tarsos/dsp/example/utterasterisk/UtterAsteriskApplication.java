/*
 *      _______                       _____   _____ _____
 *     |__   __|                     |  __ \ / ____|  __ \
 *        | | __ _ _ __ ___  ___  ___| |  | | (___ | |__) |
 *        | |/ _` | '__/ __|/ _ \/ __| |  | |\___ \|  ___/
 *        | | (_| | |  \__ \ (_) \__ \ |__| |____) | |
 *        |_|\__,_|_|  |___/\___/|___/_____/|_____/|_|
 *
 * -------------------------------------------------------------
 *
 * TarsosDSP is developed by Joren Six at IPEM, University Ghent
 *
 * -------------------------------------------------------------
 *
 *  Info: http://0110.be/tag/TarsosDSP
 *  Github: https://github.com/JorenSix/TarsosDSP
 *  Releases: http://0110.be/releases/TarsosDSP/
 *
 *  TarsosDSP includes modified source code by various authors,
 *  for credits and info, see README.
 *
 */


package be.tarsos.dsp.example.utterasterisk;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.example.InputPanel;
import be.tarsos.dsp.example.PitchDetectionPanel;
import be.tarsos.dsp.example.utterasterisk.domain.Scoreboard;
import be.tarsos.dsp.example.utterasterisk.domain.UserPitchDetectionHandler;
import be.tarsos.dsp.example.utterasterisk.domain.call.expected.Call;
import be.tarsos.dsp.example.utterasterisk.domain.call.expected.CallFactory;
import be.tarsos.dsp.example.utterasterisk.domain.comparison.NoteComparator;
import be.tarsos.dsp.example.utterasterisk.domain.fft.FftResult;
import be.tarsos.dsp.example.utterasterisk.domain.filter.BandPassFilter;
import be.tarsos.dsp.example.utterasterisk.ui.UtterAsteriskPanel;
import be.tarsos.dsp.io.jvm.JVMAudioInputStream;
import be.tarsos.dsp.pitch.PitchProcessor.PitchEstimationAlgorithm;
import be.tarsos.dsp.util.fft.FFT;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import javax.swing.border.TitledBorder;

public class UtterAsteriskApplication extends JFrame {

    private static final long serialVersionUID = 4787721035066991486L;

    private static final double ALLOWED_PITCH_TOLERANCE_IN_PERCENT = 30.0;
    private static final int MINIMUM_DISPLAYED_FREQUENCY = 80;
    private static final int MAXIMUM_DISPLAYED_FREQUENCY = 2000;
    private static final int SAMPLE_RATE = 44100;
    private static final int BUFFER_SIZE = 1536;
    private static final int OVERLAP = 0;

    private final UtterAsteriskPanel panel;
    private UserPitchDetectionHandler userPitchDetectionHandler;
    private AudioDispatcher dispatcher;
    private Mixer currentMixer;
    private PitchEstimationAlgorithm algo;
    private ActionListener algoChangeListener = new ActionListener() {
        @Override
        public void actionPerformed(final ActionEvent e) {
            String name = e.getActionCommand();
            PitchEstimationAlgorithm newAlgo = PitchEstimationAlgorithm.valueOf(name);
            algo = newAlgo;
            try {
                setNewMixer(currentMixer);
            } catch (Exception exception) {
                System.out.println("Cannot start mixer");
            }
        }
    };

    public UtterAsteriskApplication() {
        this.setLayout(new BorderLayout());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("UtterAsterisk");

        Scoreboard scoreboard = new Scoreboard();
        Call call = new CallFactory().createDefault();

        panel = new UtterAsteriskPanel(
            MINIMUM_DISPLAYED_FREQUENCY,
            MAXIMUM_DISPLAYED_FREQUENCY,
            ALLOWED_PITCH_TOLERANCE_IN_PERCENT,
            Collections.singletonList(new BandPassFilter(MINIMUM_DISPLAYED_FREQUENCY, MAXIMUM_DISPLAYED_FREQUENCY)),
            new CallFactory().createDefault(),
            new NoteComparator(ALLOWED_PITCH_TOLERANCE_IN_PERCENT, scoreboard),
            scoreboard
        );

        algo = PitchEstimationAlgorithm.YIN;

        JPanel pitchDetectionPanel = new PitchDetectionPanel(algoChangeListener);

        JPanel inputPanel = new InputPanel();

        inputPanel.addPropertyChangeListener("mixer",
            new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent arg0) {
                    try {
                        setNewMixer((Mixer) arg0.getNewValue());
                    } catch (Exception e) {
                        System.out.println("Cannot start mixer");
                    }
                }
            });

        JPanel containerPanel = new JPanel(new GridLayout(1, 0));
        containerPanel.add(inputPanel);
        containerPanel.add(pitchDetectionPanel);
        this.add(containerPanel, BorderLayout.NORTH);

        JPanel otherContainer = new JPanel(new BorderLayout());
        otherContainer.add(panel, BorderLayout.CENTER);
        otherContainer.setBorder(new TitledBorder("3. Utter a sound (whistling works best)"));

        this.add(otherContainer, BorderLayout.CENTER);
    }


    private void setNewMixer(Mixer mixer) throws LineUnavailableException, UnsupportedAudioFileException {

        if (dispatcher != null) {
            dispatcher.stop();
        }
        currentMixer = mixer;

        float sampleRate = SAMPLE_RATE;
        int bufferSize = BUFFER_SIZE;
        int overlap = OVERLAP;
        int fftSize = bufferSize / 2;

        //textArea.append("Started listening with " + Shared.toLocalString(mixer.getMixerInfo().getName()) + "\n\tparams: " + threshold + "dB\n");

        userPitchDetectionHandler = new UserPitchDetectionHandler(panel, System.currentTimeMillis());

        final AudioFormat format = new AudioFormat(sampleRate, 16, 1, true,
            false);
        final DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, format);
        TargetDataLine line = (TargetDataLine) mixer.getLine(dataLineInfo);

        final int numberOfSamples = bufferSize;
        line.open(format, numberOfSamples);
        line.start();
        final AudioInputStream stream = new AudioInputStream(line);

        JVMAudioInputStream audioStream = new JVMAudioInputStream(stream);
        // create a new dispatcher
        dispatcher = new AudioDispatcher(audioStream, bufferSize, overlap);


        List<FftResult> fftResults = new ArrayList<>();
        // add a processor, handle percussion event.
        //dispatcher.addAudioProcessor(new PitchProcessor(algo, sampleRate, bufferSize, userPitchDetectionHandler));
        dispatcher.addAudioProcessor(new AudioProcessor() {

            FFT fft = new FFT(bufferSize);
            final float[] amplitudes = new float[fftSize];

            @Override
            public boolean process(AudioEvent audioEvent) {
                double secondsSinceStart = audioEvent.getTimeStamp();
                List<FftResult> topFrequencies = new ArrayList<>();

                float[] audioBuffer = audioEvent.getFloatBuffer();
                fft.forwardTransform(audioBuffer);
                fft.modulus(audioBuffer, amplitudes);

                for (int i = 0; i < amplitudes.length; i++) {
                    if (amplitudes[i] > 10.0) {
                        fftResults.add(new FftResult((int) fft.binToHz(i, sampleRate), amplitudes[i]));
                        //        System.out.println(String.format("Amplitude at %3d Hz: %8.3f", (int) fft.binToHz(i, sampleRate), amplitudes[i]));
                    }
                }
                if (!fftResults.isEmpty()) {
                    Collections.sort(fftResults);
                    for (int index = 0; index < Math.min(1, fftResults.size()); index++) {
                        FftResult fftResult = fftResults.get(index);
                        topFrequencies.add(index, fftResult);
                        System.out.println(String.format("Amplitude at %3d Hz: %8.3f", fftResult.getFrequencyInHz(), fftResult.getAmplitude()));
                    }
                    System.out.println();

                }
                panel.addDetectedFrequency(secondsSinceStart, topFrequencies);
                fftResults.clear();

                return true;
            }

            @Override
            public void processingFinished() {

            }
        });

        // run the dispatcher (on a new thread).
        new Thread(dispatcher, "Audio dispatching").start();
    }

    public static void main(String... strings) throws InterruptedException,
        InvocationTargetException {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) {
                    //ignore failure to set default look en feel;
                }
                JFrame frame = new UtterAsteriskApplication();
                frame.pack();
                frame.setSize(640, 480);
                frame.setVisible(true);
            }
        });
    }
}
