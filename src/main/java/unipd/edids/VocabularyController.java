package unipd.edids;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class VocabularyController {
    @FXML
    private TextField adjectiveInputField;
    @FXML
    private TextField nounInputField;
    @FXML
    private TextField verbInputField;
    private Stage stage;

    public void addVerb() throws IOException {
        String inputText = verbInputField.getText();
        if (inputText == null || inputText.trim().isEmpty()) {
            verbInputField.setPromptText("Empty field: please enter a verb!");
            verbInputField.setStyle("-fx-prompt-text-fill: red;");
            return;
        }
        String[] parts = inputText.split("[ ,]+");
        String result = String.join(System.lineSeparator(), parts);
        FileManager.getInstance().appendLineToFile(ConfigManager.getInstance().getProperty("verb.file", ""), result);
        verbInputField.clear();
    }

    public void addNoun() throws IOException {
        String inputText = nounInputField.getText();
        if (inputText == null || inputText.trim().isEmpty()) {
            nounInputField.setPromptText("Empty field: please enter a noun!");
            nounInputField.setStyle("-fx-prompt-text-fill: red;");
            return;
        }
        String[] parts = inputText.split("[ ,]+");
        String result = String.join(System.lineSeparator(), parts);
        FileManager.getInstance().appendLineToFile(ConfigManager.getInstance().getProperty("noun.file", ""), result);
        nounInputField.clear();
    }

    public void addAdjective() throws IOException {
        String inputText = adjectiveInputField.getText();
        if (inputText == null || inputText.trim().isEmpty()) {
            adjectiveInputField.setPromptText("Empty field: please enter an adjective!");
            adjectiveInputField.setStyle("-fx-prompt-text-fill: red;");
            return;
        }
        String[] parts = inputText.split("[ ,]+");
        String result = String.join(System.lineSeparator(), parts);
        FileManager.getInstance().appendLineToFile(ConfigManager.getInstance().getProperty("adjective.file", ""), result);
        adjectiveInputField.clear();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void closeWindow() {
        if (stage != null) {
            stage.close();
        }
    }
}
