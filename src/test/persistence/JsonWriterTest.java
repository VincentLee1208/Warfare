package persistence;

import model.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JsonWriterTest extends JsonTest {

    @Test
    void testWriterInvalidFile() {
        try {
            Player p = new Player();
            JsonWriter writer = new JsonWriter("./data/my\0illegal:fileName.json");
            writer.open();
            fail("IOException was expected");
        } catch (IOException e) {
            //pass
        }
    }

    @Test
    void testWriterDefaultPlayer() {
        try {
            Player player = new Player();
            JsonWriter writer = new JsonWriter("./data/testWriterPlayerDefault.json");
            writer.open();
            writer.write(player);
            writer.close();

            JsonReader reader = new JsonReader("./data/testWriterPlayerDefault.json");
            player = reader.read();

            assertEquals(25, player.getMoney());
            assertEquals(0, player.getSoldiers().size());
            assertEquals(0, player.getArchers().size());
            assertFalse(player.getTutorial());
            assertFalse(player.getLevelOne());
        } catch (IOException e) {
            fail("IOException should not have been thrown");
        }
    }

    @Test
    void testWriterUpdatedPlayer() {
        try {
            Player player = new Player();
            player.buyUnits("Soldier");
            player.buyUnits("Archer");

            player.setTutorial(true);
            player.setLevelOne(true);

            JsonWriter writer = new JsonWriter("./data/testWriterUpdatedPlayer.json");
            writer.open();
            writer.write(player);
            writer.close();

            JsonReader reader = new JsonReader("./data/testWriterUpdatedPlayer.json");
            player = reader.read();

            assertEquals(15, player.getMoney());
            assertEquals(1, player.getSoldiers().size());
            assertEquals(1, player.getArchers().size());

            assertTrue(player.getTutorial());
            assertTrue(player.getLevelOne());
        } catch (IOException e) {
            fail("Exception should not have been thrown");
        }
    }
}