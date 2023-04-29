package persistence;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

import model.Soldier;
import model.Archer;
import org.json.*;

import model.Player;

// Code taken from CPSC 210 JSONSerialization Demo
// Represents a reader that reads player from JSON data stored in file
public class JsonReader {
    private String source;

    // EFFECTS: constructs reader to read from source file
    public JsonReader(String source) {
        this.source = source;
    }

    // EFFECTS: reads player from file and returns it;
    // throws IOException if an error occurs reading data from file
    public Player read() throws IOException {
        String jsonData = readFile(source);
        JSONObject jsonObject = new JSONObject(jsonData);

        return parsePlayer(jsonObject);
    }

    // EFFECTS: reads source file as string and returns it
    private String readFile(String source) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();

        try (Stream<String> stream = Files.lines(Paths.get(source), StandardCharsets.UTF_8)) {
            stream.forEach(contentBuilder::append);
        }
        return contentBuilder.toString();
    }

    private Player parsePlayer(JSONObject jsonObject) {
        int money = jsonObject.getInt("money");
        boolean tutorial = jsonObject.getBoolean("tutorialcompleted");
        boolean levelone = jsonObject.getBoolean("levelonecompleted");
        int numofsoldiers = jsonObject.getInt("soldiers");
        int numofarchers = jsonObject.getInt("archers");

        Player player = new Player();
        player.setMoney(money);
        player.setTutorial(tutorial);
        player.setLevelOne(levelone);

        for (int i = 0; i < numofsoldiers; i++) {
            Soldier newsoldier = new Soldier(false, 0, 0);
            player.getSoldiers().add(newsoldier);
        }

        for (int j = 0; j < numofarchers; j++) {
            Archer newarcher = new Archer(false, 0, 0);
            player.getArchers().add(newarcher);
        }

        return player;
    }


}
