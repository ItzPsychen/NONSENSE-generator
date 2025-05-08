package unipd.edids;

import com.google.cloud.language.v1.Token;

import java.util.ArrayList;
import java.util.List;

public class Sentence {
    private String sentence;				// frase originale o generata
    private String structure;				// frase con i placeholder tipo "The [NOUN] [VERB]..."
    private List<String> nouns;
    private List<String> verbs;
    private List<String> adjectives;

    // Costruttori, getter, setter

    public Sentence() {
        this.nouns = new ArrayList<>();
        this.verbs = new ArrayList<>();
        this.adjectives = new ArrayList<>();
    }


    public void analyzeFromTokens(List<Token> tokens) {
        // qui costruisci structure e riempi le liste
    }

    public String generateSentence() {
        // qui selezioni random parole dalle liste (o da dizionario extra)
        // e sostituisci in structure
    }

    public int countPlaceholders(String placeholder) {
        // conta quante volte compare [NOUN], [VERB], etc nella structure
        int count = 0;
        StringUtils.countMatches(structure, "[NOUN]");
    }
}
