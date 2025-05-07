package unipd.edids;

import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.language.v1.*;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;
import java.lang.*;

public class App {
    public static void main(String[] args) throws IOException {
        System.out.println("\nThe window is opening...");
        Graphic G = new Graphic();
        System.out.println("Have fun :)\n");


        // Creazione del client autenticato
        LanguageServiceClient language =
                LanguageServiceClient
                        .create(LanguageServiceSettings
                                .newBuilder()
                                .setCredentialsProvider(
                                        () -> ServiceAccountCredentials.fromStream(new FileInputStream("./src/main/resources/nonsense-generator-458709-f6e2fe62e727.json")))
                                .build()
                        );

        // Testo da analizzare
        String text = "Federico loves coding in Java!";

        // Creazione del documento
        Document doc = Document.newBuilder()
                .setContent(text)
                .setType(Document.Type.PLAIN_TEXT)
                .build();

        // Richiesta di analisi della sintassi
        AnalyzeSyntaxRequest request = AnalyzeSyntaxRequest.newBuilder()
                .setDocument(doc)
                .setEncodingType(EncodingType.UTF16)
                .build();

        // Esegui l'analisi
        AnalyzeSyntaxResponse response = language.analyzeSyntax(request);

        // Stampa i token (le parole con le etichette grammaticali)
        for (Token token : response.getTokensList()) {
            System.out.printf("Text: %s, Part of Speech: %s\n",
                    token.getText().getContent(),
                    token.getPartOfSpeech().getTag());
        }

        // Chiudi il client
        language.close();

    }
}
