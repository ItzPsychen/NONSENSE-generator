package unipd.edids;

import java.io.*;
import java.lang.*;

public class App {
    public static void main(String[] args) throws IOException {
        System.out.println("\nThe window is opening...");
        Graphic G = new Graphic();
        System.out.println("Have fun :)\n");

        try {
            String text = "Federico loves coding in Java!";

            // Usa il metodo del Singleton per fare la richiesta
            String result = APIClient.requestSentence(text).toString();

            // Stampa il risultato
            System.out.println(result);

        } finally {
            // Assicurati di chiudere il client una volta terminato
            APIClient.closeClient();
        }

    }
}
