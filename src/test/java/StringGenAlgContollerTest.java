import org.junit.jupiter.api.Test;
import org.stringgenalg.StringGenAlgController;

import java.util.HashSet;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class StringGenerationTest {

    @Test
    void testGenerateRandomString_Length() {
        // Test various lengths
        for (int length : new int[]{0, 1, 5, 10, 100}) {
            String result = StringGenAlgController.generateRandomString(length);
            assertEquals(length, result.length(),
                    "Generated string should have requested length");
        }
    }
    @Test
    void testGenerateRandomString_ZeroLength() {
        String result = StringGenAlgController.generateRandomString(0);
        assertEquals("", result,
                "Zero length should return empty string");
    }
    @Test
    void testGenerateRandomString_Randomness() {
        int length = 10;
        Set<String> generatedStrings = new HashSet<>();
        int sampleSize = 100;
        // Generate multiple strings and check they're not all identical
        for (int i = 0; i < sampleSize; i++) {
            generatedStrings.add(StringGenAlgController.generateRandomString(length));
        }
        assertTrue(generatedStrings.size() > 1,
                "Multiple generations should produce different strings");
    }

    @Test
    void testGenerateRandomString_AllPossibleCharacters() {
        int length = 1000; // Large enough to likely contain all characters
        String result = StringGenAlgController.generateRandomString(length);
        boolean[] charsFound = new boolean[95]; // For characters 32-126

        for (char c : result.toCharArray()) {
            charsFound[c - 32] = true;
        }

        // Check that at least 80% of possible characters appeared
        // (since with random generation we can't guarantee all will appear)
        int foundCount = 0;
        for (boolean found : charsFound) {
            if (found) foundCount++;
        }

        assertTrue(foundCount > 76, // 80% of 95
                "Most ASCII characters should appear in large enough sample");
    }
}