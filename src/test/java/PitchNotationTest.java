import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PitchNotationTest {
    @Test
    public void a4_perfect440Hz() {
        // Arrange
        float frequency = 440.0f;
        // Act
        PitchNotation pn = PitchNotation.getNotation(frequency);
        // Assert
        assertEquals("A", pn.getNote());
        assertEquals(4, pn.getOctave());
        assertEquals(0, pn.getCents());
    }

    @Test
    public void a4_1centUp() {
        // Arrange
        float frequency = 440.3f;
        // Act
        PitchNotation pn = PitchNotation.getNotation(frequency);
        // Assert
        assertEquals("A", pn.getNote());
        assertEquals(4, pn.getOctave());
        assertEquals(1, pn.getCents());
    }

    @Test
    public void a4_1centDown() {
        // Arrange
        float frequency = 439.7f;
        // Act
        PitchNotation pn = PitchNotation.getNotation(frequency);
        // Assert
        assertEquals("A", pn.getNote());
        assertEquals(4, pn.getOctave());
        assertEquals(-1, pn.getCents());
    }

    @Test
    public void middleC_perfect() {
        // Arrange
        float frequency = 261.6256f;
        // Act
        PitchNotation pn = PitchNotation.getNotation(frequency);
        // Assert
        assertEquals("C", pn.getNote());
        assertEquals(4, pn.getOctave());
        assertEquals(0, pn.getCents());
    }
}