package unipd.edids;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.FileWriter;
import java.io.IOException;

public class FormController {

    private AppManager appManager;
    private boolean save;

    public FormController() {
        appManager = new AppManager();
        save = false;
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
            analysis = analyzeResult.getSyntaxTree().pennString();
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
}
