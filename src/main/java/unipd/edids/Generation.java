//package unipd.edids;
//
//import com.google.cloud.language.v1.*;
//
//import java.util.Arrays;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Random;
//
//public class Generation {
//    public String generate(String text) {
//        text = "Because he organized his sources by theme, it was easier for his readers to follow";
/// /        text = "This is an example sentence to analyze for syntax and moderation.";
//
//        /*
//            1. Part of Speech (getPartOfSpeech().getTag())
//            2. Lemma (getLemma())
//            3. Morphological Features (getPartOfSpeech().get...)
//            4. Dependency Tree (getDependencyEdge())
//         */
//
//        // creating the original sentence structure
//        StringBuilder sentenceStructure = new StringBuilder();
//        List<Token> tokens = getSentenceToken(text);
//
//        // list of words (by type)
//        List<Noun> nouns = new ArrayList<>();
//        List<Verb> verbs = new ArrayList<>();
//        List<Adjective> adjectives = new ArrayList<>();
//        List<Adverb> adverbs = new ArrayList<>();
//
//        // fill the above lists and choose a new sentence structure
//        createLists(sentenceStructure, tokens, nouns, verbs, adjectives, adverbs);
//
//        // choosing a new Structure (replaced in "sentenceStructure")
//        String newRandom = extractNewSentenceStructure(sentenceStructure);
//
//        // making the new sentence "randomly"
//        return buildNonSense(tokens, newRandom, nouns, verbs, adjectives, adverbs);
//    }
//
//    private List<Token> getSentenceToken(String text) {
//        // Istanza dei servizi
//        AnalyzeSyntaxService syntaxService = new AnalyzeSyntaxService();
//        TextModerationService moderationService = new TextModerationService();
//
//        List<Token> tokens = null;
//        try {
//            // Analisi della sintassi
//            System.out.println("=== Analisi della Sintassi ===");
//            tokens = new ArrayList<>(syntaxService.analyzeSyntax(text));
//            if (tokens != null) {
/// /                tokens.forEach(token ->
/// /                        System.out.println(token.getAllFields()));
/// /                                System.out.printf("Token: %s, Parte del discorso: %s, dipendenza %s\n",
/// /                                        token.getText().getContent(),
/// /                                        token.getPartOfSpeech().getTag(),
/// /                                        token.getDependencyEdge())
//
/// /                );
//                for(Token token : tokens) {
//                    System.out.printf("\tText: %s\n", token.getText().getContent());
//                    System.out.printf("\tBeginOffset: %d\n", token.getText().getBeginOffset());
//                    System.out.printf("Lemma: %s\n", token.getLemma());
//                    System.out.printf("PartOfSpeechTag: %s\n", token.getPartOfSpeech().getTag());
//                    System.out.printf("\tAspect: %s\n", token.getPartOfSpeech().getAspect());
//                    System.out.printf("\tCase: %s\n", token.getPartOfSpeech().getCase());
//                    System.out.printf("\tForm: %s\n", token.getPartOfSpeech().getForm());
//                    System.out.printf("\tGender: %s\n", token.getPartOfSpeech().getGender());
//                    System.out.printf("\tMood: %s\n", token.getPartOfSpeech().getMood());
//                    System.out.printf("\tNumber: %s\n", token.getPartOfSpeech().getNumber());
//                    System.out.printf("\tPerson: %s\n", token.getPartOfSpeech().getPerson());
//                    System.out.printf("\tProper: %s\n", token.getPartOfSpeech().getProper());
//                    System.out.printf("\tReciprocity: %s\n", token.getPartOfSpeech().getReciprocity());
//                    System.out.printf("\tTense: %s\n", token.getPartOfSpeech().getTense());
//                    System.out.printf("\tVoice: %s\n", token.getPartOfSpeech().getVoice());
//                    System.out.println("DependencyEdge");
//                    System.out.printf("\tHeadTokenIndex: %d\n", token.getDependencyEdge().getHeadTokenIndex());
//                    System.out.printf("\tLabel: %s\n\n", token.getDependencyEdge().getLabel());
//                }
//            } else {
//                System.out.println("Errore durante l'analisi della sintassi.");
//            }
//
//            // Moderazione del testo
//            System.out.println("\n=== Moderazione del Testo ===");
//            ModerateTextResponse moderateTextResponse = moderationService.moderateText(text);
//            if (moderateTextResponse != null && moderateTextResponse.getModerationCategoriesList() != null) {
//                moderateTextResponse.getModerationCategoriesList().forEach(category ->
//                        System.out.printf("Categoria: %s, Confidenza: %.2f%n",
//                                category.getName(),
//                                category.getConfidence())
//                );
//            } else {
//                System.out.println("Nessuna categoria di moderazione rilevata.");
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            // Chiudi il client singleton al termine
//            APIClient.closeClient();
//        }
//
//        return tokens;
//    }
//
//    private void createLists(StringBuilder sentenceStructure, List<Token> tokens, List<Noun> nouns,
//                             List<Verb> verbs, List<Adjective> adjectives, List<Adverb> adverbs) {
//        // check for every token (word written by user)
//        for (Token token : tokens) {
//            String word = token.getText().getContent();
//            String posTag = token.getPartOfSpeech().getTag().name();
//
//            // looking for the Tag of the word
//            switch (posTag) {
//                case "PRON":
//                case "NOUN":
//                    nouns.add(new Noun(word));
//                    sentenceStructure.append("[noun] ");
//                    break;
//                case "VERB":
//                    Verb current = new Verb(word);
//                    verbs.add(new Verb(word));
//                    sentenceStructure.append("[verb").append(current.form()).append("] ");
//                    break;
//                case "ADJ":
//                    adjectives.add(new Adjective(word));
//                    sentenceStructure.append("[adjective] ");
//                    break;
//                case "ADV":
//                    adverbs.add(new Adverb(word));
//                    sentenceStructure.append("[adverb] ");
//                    break;
//                default:
//                    sentenceStructure.append(word).append(" ");
//                    break;
//            }
//
//            // remove the last space
//            if (!sentenceStructure.isEmpty()) {
//                sentenceStructure.setLength(sentenceStructure.length() - 1);
//            }
//        }
//    }
//
//    private String extractNewSentenceStructure(StringBuilder sentenceStructure) {
//        System.out.println("\n" + sentenceStructure.toString());
//        Structure current = new Structure(sentenceStructure.toString());
//        Structure newRandom = current.getNewRandom();
//        if (newRandom == null) {
//            System.err.println("Nessuna struttura corrispondente trovata. Verrà usata la struttura originale.");
//            return null; // Oppure scegli una struttura predefinita
//        }
//        sentenceStructure.delete(0, sentenceStructure.length());
//        sentenceStructure.append(newRandom.getText());
//        System.out.println("\n" + sentenceStructure.toString() + "\n");
//        return newRandom.getText();
//    }
//
//    private String buildNonSense(List<Token> tokens, String newRandom, List<Noun> nouns,
//                                 List<Verb> verbs, List<Adjective> adjectives, List<Adverb> adverbs) {
//        StringBuilder nonSense = new StringBuilder();
//        Random r = new Random();
//        int index = 0;
//        List<String> words = Arrays.asList(newRandom.split(" "));
//        for (String word : words) {
//            switch (word) {
//                case "[noun]":
//                    index = r.nextInt(0, nouns.size());
//                    nonSense.append(nouns.get(index).getText());
//                    nouns.remove(index);
//                    break;
//                case "[verb]":
//                case "[verb+ing]":
//                case "[verb+ed]":
//                    String currentForm = "";
//                    if (word.startsWith("[verb+")) currentForm = word.substring(word.indexOf('+'), word.length() - 1);
//                    while (true) {
//                        index = r.nextInt(0, verbs.size());
//                        if (!verbs.get(index).form().equals(currentForm)) continue;
//                        nonSense.append(verbs.get(index).getText());
//                        verbs.remove(index);
//                        break;
//                    }
//                case "[adjective]":
//                    index = r.nextInt(0, adjectives.size());
//                    nonSense.append(adjectives.get(index).getText());
//                    adjectives.remove(index);
//                    break;
//                case "[adverb]":
//                    index = r.nextInt(0, adverbs.size());
//                    nonSense.append(adverbs.get(index).getText());
//                    adverbs.remove(index);
//                    break;
//                default:
//                    nonSense.append(word);
//                    break;
//            }
//            nonSense.append(" ");
//        }
//        nonSense.setLength(nonSense.length() - 1);
//
//        return nonSense.toString();
//    }
//}

package unipd.edids;

import com.google.cloud.language.v1.*;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Generation {
    private static final Logger logger = LogManager.getLogger(Generation.class);
    private ArrayList<Word> sentenceWords;

    public String generate(String text) {
//        text = "Because he organized his sources by theme, it was easier for his readers to follow";
//        text = "This is a good example sentence to analyze for syntax and moderation.";
//        text = "This sentence is a good example";

        /*
            1. Part of Speech (getPartOfSpeech().getTag())
            2. Lemma (getLemma())
            3. Morphological Features (getPartOfSpeech().get...)
            4. Dependency Tree (getDependencyEdge())
         */

        List<Token> tokens = AnalyzeSyntaxService.analyzeSyntax(text);
        Sentence inputSentence = new Sentence(text);
        Sentence outputSentence = new Sentence();
        outputSentence = inputSentence;
        outputSentence.generateSentence();
        System.out.println(outputSentence.getSentence());
        List<ClassificationCategory> moderationCategories = TextModerationService.moderateText(outputSentence.getSentence());
        for(ClassificationCategory category : moderationCategories) {
            logger.info(category.getName() + " - " + Float.toString(category.getConfidence()));
        }



        return null;
    }

}