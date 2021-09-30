package game;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.io.FileReader;


public class PropertyManager {
    private static Properties whistProperty;

    public static void initProperty(String fileName) throws IOException {
        whistProperty = new Properties();

        whistProperty.getProperty("NPlayer", "4");
        whistProperty.setProperty("StartingHand", "13");

        whistProperty.setProperty("Player0Human", "true");
        whistProperty.setProperty("Player1Human", "false");
        whistProperty.setProperty("Player2Human", "false");
        whistProperty.setProperty("Player3Human", "false");

        whistProperty.setProperty("Player0Filter", "player");
        whistProperty.setProperty("Player1Filter", "none");
        whistProperty.setProperty("Player2Filter", "none");
        whistProperty.setProperty("Player3Filter", "none");

        whistProperty.setProperty("Player0Select", "player");
        whistProperty.setProperty("Player1Select", "random");
        whistProperty.setProperty("Player2Select", "random");
        whistProperty.setProperty("Player3Select", "random");
        whistProperty.setProperty("cardDown", "false");


        try (FileReader inStream = new FileReader(fileName + ".properties")) {
            whistProperty.load(inStream);
        }
    }

    public static String getWhistProperty(String prop) {
        return whistProperty.getProperty(prop);
    }
}
