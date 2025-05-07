package unipd.edids;

import com.google.cloud.language.v1.*;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Generation {
    public String generate(String text) {
        text = "This is a holy shit example sentence to analyze for syntax and moderation.";

        /*
            1. Part of Speech (getPartOfSpeech().getTag())
            2. Lemma (getLemma())
            3. Morphological Features (getPartOfSpeech().get...)
            4. Dependency Tree (getDependencyEdge())
         */

        // creating the original sentence structure
        StringBuilder sentenceStructure = new StringBuilder();
        List<Token> tokens = getSentenceToken(text);

        // list of words (by type)
        List<Noun> nouns = new ArrayList<>();
        List<Verb> verbs = new ArrayList<>();
        List<Adjective> adjectives = new ArrayList<>();
        List<Adverb> adverbs = new ArrayList<>();
        List<Pronoun> pronouns = new ArrayList<>();

        // fill the above lists and choose a new sentence structure
        createLists(sentenceStructure, tokens, nouns, verbs, adjectives, adverbs, pronouns);

        // choosing a new Structure (replaced in "sentenceStructure")
        String newRandom = extractNewSentenceStructure(sentenceStructure);

        // making the new sentence "randomly"
        return buildNonSense(tokens, newRandom, nouns, verbs, adjectives, adverbs, pronouns);
    }

    private List<Token> getSentenceToken(String text) {
        // Istanza dei servizi
        AnalyzeSyntaxService syntaxService = new AnalyzeSyntaxService();
        TextModerationService moderationService = new TextModerationService();

        List<Token> tokens = null;
        try {
            // Analisi della sintassi
            System.out.println("=== Analisi della Sintassi ===");
            tokens = new ArrayList<>(syntaxService.analyzeSyntax(text));
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

        return tokens;
    }

    private void createLists(StringBuilder sentenceStructure, List<Token> tokens, List<Noun> nouns,
                                   List<Verb> verbs, List<Adjective> adjectives, List<Adverb> adverbs, List<Pronoun> pronouns) {
        // check for every token (word written by user)
        for (Token token : tokens) {
            String word = token.getText().getContent();
            String posTag = token.getPartOfSpeech().getTag().name();

            // looking for the Tag of the word
            switch (posTag) {
                case "PRON":
                    pronouns.add(new Pronoun(word));
                    sentenceStructure.append("[pronoun] ");
                    break;
                case "NOUN":
                    nouns.add(new Noun(word));
                    sentenceStructure.append("[noun] ");
                    break;
                case "VERB":
                    Verb current = new Verb(word);
                    verbs.add(new Verb(word));
                    sentenceStructure.append("[verb").append(current.form()).append("] ");
                    break;
                case "ADJ":
                    adjectives.add(new Adjective(word));
                    sentenceStructure.append("[adjective] ");
                    break;
                case "ADV":
                    adverbs.add(new Adverb(word));
                    sentenceStructure.append("[adverb] ");
                    break;
                default:
                    sentenceStructure.append(word).append(" ");
                    break;
            }

            // remove the last space
            if (!sentenceStructure.isEmpty()) {
                sentenceStructure.setLength(sentenceStructure.length() - 1);
            }
        }
    }

    private String extractNewSentenceStructure(StringBuilder sentenceStructure) {
        System.out.println("\n" + sentenceStructure.toString());
        Structure current = new Structure(sentenceStructure.toString());
        Structure newRandom = new Structure(current.getNewRandom());
        sentenceStructure.delete(0, sentenceStructure.length());
        sentenceStructure.append(newRandom.getText());
        System.out.println("\n" + sentenceStructure.toString() + "\n");
        return newRandom.getText();
    }

    private String buildNonSense(List<Token> tokens, String newRandom, List<Noun> nouns,
                                 List<Verb> verbs, List<Adjective> adjectives, List<Adverb> adverbs, List<Pronoun> pronouns) {
        StringBuilder nonSense = new StringBuilder();
        Random r = new Random();
        int index = 0;
        List<String> words = Arrays.asList(newRandom.split(" "));
        for (String word : words) {
            switch (word) {
                case "[noun]":
                    index = r.nextInt(0, nouns.size());
                    nonSense.append(nouns.get(index).getText());
                    nouns.remove(index);
                    break;
                case "[verb]":
                case "[verb+ing]":
                case "[verb+ed]":
                    String currentForm = "";
                    if (word.startsWith("[verb+")) currentForm = word.substring(word.indexOf('+'), word.length() - 1);
                    while (true) {
                        index = r.nextInt(0, verbs.size());
                        if (!verbs.get(index).form().equals(currentForm)) continue;
                        nonSense.append(verbs.get(index).getText());
                        verbs.remove(index);
                        break;
                    }
                case "[adjective]":
                    index = r.nextInt(0, adjectives.size());
                    nonSense.append(adjectives.get(index).getText());
                    adjectives.remove(index);
                    break;
                case "[adverb]":
                    index = r.nextInt(0, adverbs.size());
                    nonSense.append(adverbs.get(index).getText());
                    adverbs.remove(index);
                    break;
                case "[pronoun]":
                    index = r.nextInt(0, pronouns.size());
                    nonSense.append(pronouns.get(index).getText());
                    pronouns.remove(index);
                default:
                    nonSense.append(word);
                    break;
            }
            nonSense.append(" ");
        }
        nonSense.setLength(nonSense.length() - 1);

        return nonSense.toString();
    }
}