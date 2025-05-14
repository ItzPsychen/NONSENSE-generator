package unipd.edids;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class FormController {

    private AppManager appManager;

    public FormController() {
        appManager = new AppManager();
    }

    @FXML
    private TextField inputText;
    @FXML
    private TextArea syntaxArea;
    @FXML
    private CheckBox checkSyntax;
    @FXML
    private TextFlow generateArea;

    public void analyzeClick() {
        Sentence analyzeResult = appManager.analyzeSentence(inputText.getText());
        syntaxArea.setText(analyzeResult.getStructure().toString() +"\n");
        if (checkSyntax.isSelected()) {
            syntaxArea.appendText(analyzeResult.getSyntaxTree().toString());
        }
    }

    public void generateClick() {
        Sentence generateResult = appManager.generateSentence();
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
}
