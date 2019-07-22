import be.tarsos.dsp.pitch.PitchDetectionResult;
import models.PitchNotation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PitchNotationTest {
    @Test
    public void a4_perfect440Hz() {
        // Arrange
        PitchDetectionResult result = new PitchDetectionResult();
        result.setPitched(true);
        result.setPitch(440.0f);
        result.setProbability(1);
        // Act
        PitchNotation pn = PitchNotation.getNotation(result);
        // Assert
        assertEquals("A", pn.getNote());
        assertEquals(4, pn.getOctave());
        assertEquals(0, pn.getCents());
    }

    @Test
    public void a4_1centUp() {
        // Arrange
        PitchDetectionResult result = new PitchDetectionResult();
        result.setPitched(true);
        result.setPitch(440.3f);
        result.setProbability(1);
        // Act
        PitchNotation pn = PitchNotation.getNotation(result);
        // Assert
        assertEquals("A", pn.getNote());
        assertEquals(4, pn.getOctave());
        assertEquals(1, pn.getCents());
    }

    @Test
    public void a4_1centDown() {
        // Arrange
        PitchDetectionResult result = new PitchDetectionResult();
        result.setPitched(true);
        result.setPitch(439.7f);
        result.setProbability(1);
        // Act
        PitchNotation pn = PitchNotation.getNotation(result);
        // Assert
        assertEquals("A", pn.getNote());
        assertEquals(4, pn.getOctave());
        assertEquals(-1, pn.getCents());
    }

    @Test
    public void middleC_perfect() {
        // Arrange
        PitchDetectionResult result = new PitchDetectionResult();
        result.setPitched(true);
        result.setPitch(261.6256f);
        result.setProbability(1);
        // Act
        PitchNotation pn = PitchNotation.getNotation(result);
        // Assert
        assertEquals("C", pn.getNote());
        assertEquals(4, pn.getOctave());
        assertEquals(0, pn.getCents());
    }
}