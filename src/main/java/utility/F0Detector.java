package utility;

import be.tarsos.dsp.pitch.*;

public class F0Detector {

    private PitchDetector pitchDetector;

    public F0Detector(PitchDetector detector) {
        this.pitchDetector = detector;
    }

    public void setDetector(PitchDetector detector) {
        this.pitchDetector = detector;
    }

    public PitchDetectionResult getF0(float[] buffer) {
        return pitchDetector.getPitch(buffer);
    }
}
