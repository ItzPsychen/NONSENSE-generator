package unipd.edids;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import edu.stanford.nlp.trees.Tree;

import java.nio.file.Paths;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

public class FormController {
    private AppManager appManager;
    private boolean save;

    private Map<String, String> treeTags;

    public FormController() {
        this.appManager = new AppManager();
        this.save = false;
        this.treeTags = loadSyntaxTags(getFilePathTags());
    }

    @FXML
    private TextField inputText;
    @FXML
    private TextArea syntaxArea;
    @FXML
    private CheckBox checkSyntax;
    @FXML
    private TextFlow generateArea;
    @FXML
    private CheckBox checkSaveSentence;

    public void analyzeClick() {
        Sentence analyzeResult = appManager.analyzeSentence(inputText.getText(), checkSaveSentence.isSelected());
        syntaxArea.setText(analyzeResult.getStructure().toString() +"\n");
        String analysis = "";
        if (checkSyntax.isSelected()) {
            analysis = prettyTree(analyzeResult.getSyntaxTree());
            syntaxArea.appendText(analysis);
        }
        syntaxArea.appendText(String.valueOf(analyzeResult.getToxicity()));
        syntaxArea.appendText("\n");
        syntaxArea.appendText(String.valueOf(analyzeResult.getProfanity()));
        syntaxArea.appendText("\n");
        syntaxArea.appendText(String.valueOf(analyzeResult.getInsult()));
        syntaxArea.appendText("\n");
        syntaxArea.appendText(String.valueOf(analyzeResult.getThreat()));
        syntaxArea.appendText("\n");
        syntaxArea.appendText(String.valueOf(analyzeResult.getIdentityThreat()));
        syntaxArea.appendText("\n");
    }

    private String prettyTree(Tree tree) {
        return "\nTREE\n" + prettyTreeHelper(tree.children()[0], "", true);
    }

    private String prettyTreeHelper(Tree tree, String prefix, boolean isLast) {
        StringBuilder builder = new StringBuilder();

        // Add the prefix and the current node's value
        builder.append(prefix);
        builder.append(isLast ? "└── " : "├── ");
        builder.append(treeTags.getOrDefault(tree.value(), tree.value())).append("\n");

        // Prepare prefix for children
        Tree[] children = tree.children();
        for (int i = 0; i < children.length; i++) {
            boolean last = (i == children.length - 1);
            String newPrefix = prefix + (isLast ? "    " : "│   ");
            builder.append(prettyTreeHelper(children[i], newPrefix, last));
        }

        return builder.toString();
    }

    public void generateClick() {
        Sentence generateResult = appManager.generateSentence(checkSaveSentence.isSelected());
        if (generateResult != null) {
            // Pulisce il contenuto precedente nel TextFlow
            generateArea.getChildren().clear();

            // Formatta la struttura della frase: font più piccolo e categorie grammaticali in grassetto
            String structure = generateResult.getStructure().toString();
            TextFlow structureTextFlow = formatStructure(structure);

            // Formatta la frase: font più grande e tutto sottolineato
            String sentence = generateResult.getSentence().toString();
            Text sentenceText = new Text(sentence);
            sentenceText.setStyle("-fx-font-size: 20px; -fx-underline: true;");

            // Aggiunge entrambi al TextFlow
            generateArea.getChildren().addAll(structureTextFlow, new Text("\n\n"), sentenceText);
        }
    }

    // Metodo helper per formattare la struttura
    private TextFlow formatStructure(String structure) {
        TextFlow textFlow = new TextFlow();
        String[] tokens = structure.split(" "); // Dividiamo la struttura in sezioni basate sugli spazi

        for (String token : tokens) {
            Text text;
            // Verifica delle categorie grammaticali e applica lo stile
            if (token.equals("[noun]") || token.equals("[verb]") || token.equals("[adjective]")) {
                text = new Text(token + " "); // Lascia lo spazio dopo la parola
                text.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            } else {
                text = new Text(token + " ");
                text.setStyle("-fx-font-size: 14px;");
            }

            // Aggiunge ogni parte al TextFlow
            textFlow.getChildren().add(text);
        }

        return textFlow;
    }

    public void showErrorDialog(String title, String headerText, String contentText) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);

        // Per mostrare il dialogo e aspettare che l'utente lo chiuda
        alert.showAndWait();
    }

    private Map<String, String> loadSyntaxTags(String filePath) {
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream(Paths.get(filePath).toFile())) {
            properties.load(fis); // Load the .properties file
            // Convert Properties to Map<String, String>
            return properties.entrySet().stream()
                    .collect(Collectors.toMap(
                            e -> e.getKey().toString(),
                            e -> e.getValue().toString()
                    ));
        } catch (IOException e) {
            System.err.println("Error loading properties file: " + filePath);
            e.printStackTrace();
            return Map.of(); // Return empty Map if error occurs
        }
    }

    private String getFilePathTags() {
        return ConfigManager.getInstance().getProperty("syntax_tags.properties","./src/main/resources/syntax_tags.properties");
    }
}
