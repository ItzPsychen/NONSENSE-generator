package unipd.edids;

import com.google.cloud.language.v1.ModerateTextResponse;
import com.google.cloud.language.v1.Token;

import java.io.*;
import java.lang.*;
import java.util.List;

public class App {
    public static void main(String[] args) throws IOException {
        System.out.println("\nThe window is opening...");
        Graphic G = new Graphic();
        System.out.println("Have fun :)\n");

        String text = "This is an example sentence to analyze for syntax and moderation.";

        // Istanza dei servizi
        AnalyzeSyntaxService syntaxService = new AnalyzeSyntaxService();
        TextModerationService moderationService = new TextModerationService();

        try {
            // Analisi della sintassi
            System.out.println("=== Analisi della Sintassi ===");
            List<Token> tokens = syntaxService.analyzeSyntax(text);
            if (tokens != null) {
                tokens.forEach(token ->
                        System.out.printf("Token: %s, Parte del discorso: %s%n",
                                token.getText().getContent(),
                                token.getPartOfSpeech().getTag())
                );
            } else {
                System.out.println("Errore durante l'analisi della sintassi.");
            }

            // Moderazione del testo
            System.out.println("\n=== Moderazione del Testo ===");
            ModerateTextResponse moderateTextResponse = moderationService.moderateText(text);
            if (moderateTextResponse != null && moderateTextResponse.getModerationCategoriesList() != null) {
                moderateTextResponse.getModerationCategoriesList().forEach(category ->
                        System.out.printf("Categoria: %s, Confidenza: %.2f%n",
                                category.getName(),
                                category.getConfidence())
                );
            } else {
                System.out.println("Nessuna categoria di moderazione rilevata.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Chiudi il client singleton al termine
            APIClient.closeClient();
        }

    }
}
