package unipd.edids;


import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class VocabularyController {
    @FXML
    private BorderPane wordInputPane;
    @FXML
    private TextField adjectiveInputField;
    @FXML
    private TextField nounInputField;
    @FXML
    private TextField verbInputField;
    private Stage stage;

    public void initialize() {
        ConfigManager configManager = ConfigManager.getInstance();
        if (configManager.getProperty("ui.theme", "light").equals("dark")) {
            wordInputPane.getStylesheets().add(Objects.requireNonNull(FormController.class.getResource("/style/dark-theme.css")).toExternalForm());
        } else {
            wordInputPane.getStylesheets().clear();
        }
    }

    public void addVerb() throws IOException {
        String inputText = verbInputField.getText();
        if (inputText == null || inputText.trim().isEmpty()) {
            verbInputField.setPromptText("Empty field: please enter a verb!");
            verbInputField.setStyle("-fx-prompt-text-fill: red;");
            return;
        }
        String[] parts = inputText.split("[ ,]+");
        String result = String.join(System.lineSeparator(), parts);
        FileManager.getInstance().appendLineToVocabularyFile(ConfigManager.getInstance().getProperty("verb.file", ""), result);
        verbInputField.clear();
        verbInputField.setPromptText("Enter a verb"); // Reset the error prompt
        verbInputField.setStyle(""); // Reset the style
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
        FileManager.getInstance().appendLineToVocabularyFile(ConfigManager.getInstance().getProperty("noun.file", ""), result);
        nounInputField.clear();
        nounInputField.setPromptText("Enter a noun"); // Reset the error prompt
        nounInputField.setStyle(""); // Reset the style
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
        FileManager.getInstance().appendLineToVocabularyFile(ConfigManager.getInstance().getProperty("adjective.file", ""), result);
        adjectiveInputField.clear();
        adjectiveInputField.setPromptText("Enter an adjective"); // Reset the error prompt
        adjectiveInputField.setStyle(""); // Reset the style
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
