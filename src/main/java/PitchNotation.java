public class PitchNotation {
    private static String[] NOTE_NAMES = {
            "C", "C#/Db", "D", "D#/Eb", "E", "F",
            "F#/Gb", "G", "G#/Ab", "A", "A#/Bb", "B"
    };

    private float frequency;
    private int octave;
    private String note;
    private int cents;

    public static PitchNotation getNotation(float frequency) {
        int refOctave = 4;
        int refA440index = 9;

        double halfStepsDiff = 12.0 * Math.log((double)frequency / 440.0) / Math.log(2);
        double noteIndexRaw = (refA440index + halfStepsDiff) % 12.0;
        int noteIndex = (int)Math.round(noteIndexRaw);
        noteIndex = noteIndex == 12 ? 0 : noteIndex;

        return new Builder()
                .frequency(frequency)
                .octave(refOctave + (int)Math.round(halfStepsDiff) / 12)
                .note(NOTE_NAMES[noteIndex < 0 ? 0 : noteIndex])
                .cents((int)(((float)(noteIndexRaw - noteIndex) % 1f) * 100))
                .build();
    }

    public float getFrequency() {
        return frequency;
    }

    public int getOctave() {
        return octave;
    }

    public String getNote() {
        return note;
    }

    public int getCents() {
        return cents;
    }

    @Override
    public String toString() {
        return String.format("%.3fHz %s%d %d", frequency, note, octave, cents);
    }

    private static final class Builder {
        private float frequency;
        private int octave;
        private String note;
        private int cents;

        public Builder frequency(float frequency) {
            this.frequency = frequency;
            return this;
        }

        public Builder octave(int octave) {
            this.octave = octave;
            return this;
        }

        public Builder note(String note) {
            this.note = note;
            return this;
        }

        public Builder cents(int cents) {
            this.cents = cents;
            return this;
        }

        public PitchNotation build() {
            PitchNotation pn = new PitchNotation();
            pn.frequency = this.frequency;
            pn.octave = this.octave;
            pn.note = this.note;
            pn.cents = this.cents;
            return pn;
        }

    }
}
