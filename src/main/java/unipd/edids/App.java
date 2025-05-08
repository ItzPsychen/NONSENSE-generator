package unipd.edids;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import java.io.*;
import java.lang.*;

public class App {
    private static final Logger logger = LogManager.getLogger(App.class);
    public static void main(String[] args) throws IOException {

        logger.info("The window is opening...");
        // Creo un'istanza di Graphic (il resto del codice relativo a Graphic rimane lo stesso)
        Graphic G = new Graphic();
        logger.info("Have fun :)");
    }
}
