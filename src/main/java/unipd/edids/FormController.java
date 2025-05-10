package unipd.edids;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

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
    private TextArea generateArea;

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
            generateArea.setText(generateResult.getStructure().append("\n").append(generateResult.getSentence()).toString());
        }
    }
}
