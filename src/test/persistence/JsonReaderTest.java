package persistence;

import model.*;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JsonReaderTest extends JsonTest {

    @Test
    void testReaderNonExistentFile() {
        JsonReader reader = new JsonReader("./data/illegalfile.json");
        try {
            Player p = reader.read();
            fail("IOException expected");
        } catch (IOException e) {
            //pass
        }
    }

    @Test
    void testReaderDefaultPlayer() {
        JsonReader reader = new JsonReader("./data/testReaderPlayerDefault.json");
        try {
            Player player = reader.read();

            assertEquals(25, player.getMoney());
            assertEquals(0, player.getSoldiers().size());
            assertEquals(0, player.getArchers().size());
            assertFalse(player.getTutorial());
            assertFalse(player.getLevelOne());
        } catch (IOException e) {
            fail("Couldn't read from file");
        }
    }

    @Test
    void testReaderUpdatedPlayer() {
        JsonReader reader = new JsonReader("./data/testReaderUpdatedPlayer.json");
        try {
            Player player = reader.read();

            assertEquals(15, player.getMoney());
            assertEquals(1, player.getSoldiers().size());
            assertEquals(1, player.getArchers().size());

            assertTrue(player.getTutorial());
            assertTrue(player.getLevelOne());
        } catch (IOException e) {
            fail("Couldn't read from file");
        }
    }
}