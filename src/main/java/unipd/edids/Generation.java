package unipd.edids;

import com.google.cloud.language.v1.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Generation {
    public String generate(String text) {
        /*
            1. Part of Speech (getPartOfSpeech().getTag())
            2. Lemma (getLemma())
            3. Morphological Features (getPartOfSpeech().get...)
            4. Dependency Tree (getDependencyEdge())
         */

        // creating the original sentence structure
        StringBuilder sentenceStructure = new StringBuilder();
        AnalyzeSyntaxService syntaxService = new AnalyzeSyntaxService();
        List<Token> tokens = syntaxService.analyzeSyntax(text);

        // list of words (by type)
        List<Noun> nouns = new ArrayList<>();
        List<Verb> verbs = new ArrayList<>();
        List<Adjective> adjectives = new ArrayList<>();
        List<Adverb> adverbs = new ArrayList<>();

        // fill the above lists and choose a new sentence structure
        createLists(sentenceStructure, tokens, nouns, verbs, adjectives, adverbs);

        // choosing a new Structure (replaced in "sentenceStructure")
        extractNewSentenceStructure(sentenceStructure);

        // making the new sentence "randomly"
        StringBuilder nonSense = new StringBuilder();
        buildNonSense(tokens, nonSense, nouns, verbs, adjectives, adverbs);

        // CHECK FOR FINAL RESULT
        // ...

        return nonSense.toString();
    }

    private void createLists(StringBuilder sentenceStructure, List<Token> tokens, List<Noun> nouns,
                                   List<Verb> verbs, List<Adjective> adjectives, List<Adverb> adverbs) {
        // check for every token (word written by user)
        for (Token token : tokens) {
            String word = token.getText().getContent();
            String posTag = token.getPartOfSpeech().getTag().name();

            // looking for the Tag of the word
            switch (posTag) {
                case "NOUN":
                case "PRON":
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

    private void extractNewSentenceStructure(StringBuilder sentenceStructure) {
        System.out.println("####################\n" + sentenceStructure.toString() + "\n##################");
        Structure current = new Structure(sentenceStructure.toString());
        Structure newRandom = new Structure(current.getNewRandom());
        sentenceStructure.delete(0, sentenceStructure.length());
        sentenceStructure.append(newRandom.getText());
    }

    private void buildNonSense(List<Token> tokens, StringBuilder nonSense, List<Noun> nouns,
                                     List<Verb> verbs, List<Adjective> adjectives, List<Adverb> adverbs) {
        Random r = new Random();
        int index = 0;
        for (Token token : tokens) {
            String word = token.getText().getContent();
            switch (word) {
                case "[noun]":
                    index = r.nextInt(0, nouns.size());
                    nonSense.append(nouns.get(index));
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
                        nonSense.append(verbs.get(index));
                        verbs.remove(index);
                        break;
                    }
                case "[adjective]":
                    index = r.nextInt(0, adjectives.size());
                    nonSense.append(adjectives.get(index));
                    adjectives.remove(index);
                    break;
                case "[adverb]":
                    index = r.nextInt(0, adverbs.size());
                    nonSense.append(adverbs.get(index));
                    adverbs.remove(index);
                    break;
                default:
                    nonSense.append(word);
                    break;
            }
            nonSense.append(" ");
        }
        nonSense.setLength(nonSense.length() - 1);
    }
}