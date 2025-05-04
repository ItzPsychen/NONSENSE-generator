package unipd.edids;

import com.google.cloud.language.v1.AnalyzeSyntaxRequest;
import com.google.cloud.language.v1.AnalyzeSyntaxResponse;
import com.google.cloud.language.v1.Token;

import java.io.*;
import java.util.*;
import java.lang.*;

public class App {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        System.out.println("Enter any sentence in English");
        String text = scan.nextLine();

        // declaration of the response
        AnalyzeSyntaxResponse response = analyzeSentence(text);

        // check if response exists
        if (response == null) {
            System.err.println("Language analysis failed");
            return;
        }

        // creating the original sentence structure
        StringBuilder sentenceStructure = new StringBuilder();
        List<Token> tokens = response.getTokensList();

        // list of Noun/Verb/Adjective
        List<Noun> nouns = new ArrayList<>();
        List<Verb> verbs = new ArrayList<>();
        List<Adjective> adjectives = new ArrayList<>();

        // fill the above lists and choose a new sentence structure
        createLists(sentenceStructure, tokens, nouns, verbs, adjectives);
        extractNewSentenceStructure(sentenceStructure);


        scan.close();
    }

    public static AnalyzeSyntaxResponse analyzeSentence(String text) {
        AnalyzeSyntaxResponse response;

        // instantiate the Language client com.google.cloud.language.v1.LanguageServiceClient
        try (com.google.cloud.language.v1.LanguageServiceClient language =
                     com.google.cloud.language.v1.LanguageServiceClient.create()) {
            com.google.cloud.language.v1.Document doc =
                    com.google.cloud.language.v1.Document.newBuilder().setContent(text)
                            .setType(com.google.cloud.language.v1.Document.Type.PLAIN_TEXT).build();
            AnalyzeSyntaxRequest request =
                    AnalyzeSyntaxRequest.newBuilder()
                            .setDocument(doc)
                            .setEncodingType(com.google.cloud.language.v1.EncodingType.UTF16)
                            .build();
            // analyze the syntax in the given text
            response = language.analyzeSyntax(request);

            for (Token token : response.getTokensList()) {
                System.out.printf("\tText: %s\n", token.getText().getContent());
                System.out.printf("\tBeginOffset: %d\n", token.getText().getBeginOffset());
                System.out.printf("Lemma: %s\n", token.getLemma());
                System.out.printf("PartOfSpeechTag: %s\n", token.getPartOfSpeech().getTag());
                System.out.printf("\tAspect: %s\n", token.getPartOfSpeech().getAspect());
                System.out.printf("\tCase: %s\n", token.getPartOfSpeech().getCase());
                System.out.printf("\tForm: %s\n", token.getPartOfSpeech().getForm());
                System.out.printf("\tGender: %s\n", token.getPartOfSpeech().getGender());
                System.out.printf("\tMood: %s\n", token.getPartOfSpeech().getMood());
                System.out.printf("\tNumber: %s\n", token.getPartOfSpeech().getNumber());
                System.out.printf("\tPerson: %s\n", token.getPartOfSpeech().getPerson());
                System.out.printf("\tProper: %s\n", token.getPartOfSpeech().getProper());
                System.out.printf("\tReciprocity: %s\n", token.getPartOfSpeech().getReciprocity());
                System.out.printf("\tTense: %s\n", token.getPartOfSpeech().getTense());
                System.out.printf("\tVoice: %s\n", token.getPartOfSpeech().getVoice());
                System.out.println("DependencyEdge");
                System.out.printf("\tHeadTokenIndex: %d\n", token.getDependencyEdge().getHeadTokenIndex());
                System.out.printf("\tLabel: %s\n\n", token.getDependencyEdge().getLabel());
            }
            System.out.println(response.getTokensList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return response;
    }

    public static void createLists(StringBuilder sentenceStructure, List<Token> tokens,
                                   List<Noun> nouns, List<Verb> verbs, List<Adjective> adjectives) {
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

    public static void extractNewSentenceStructure(StringBuilder sentenceStructure) {
        Structure current = new Structure(sentenceStructure.toString());
        Structure newRandom = new Structure(current.getNewRandom());
        sentenceStructure.delete(0, sentenceStructure.length());
        sentenceStructure.append(newRandom.getText());
    }
}
