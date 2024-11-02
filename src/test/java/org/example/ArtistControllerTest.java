package org.example; // Βεβαιώσου ότι το package είναι σωστό

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ArtistControllerTest {

    @Test
    void testAddArtist() {
        // Στην κλάση ArtistControllerTest
        ArtistController controller = new ArtistController();
        controller.addArtist("John Doe", "Biography of John Doe");


        String artistName = controller.getArtistName(0);
        assertEquals("John Doe", artistName);
    }
}
