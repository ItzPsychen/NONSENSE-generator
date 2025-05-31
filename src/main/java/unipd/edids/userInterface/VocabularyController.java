package unipd.edids.userInterface;


import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import unipd.edids.logicBusiness.managers.ConfigManager;
import unipd.edids.logicBusiness.managers.FileManager;

import java.io.IOException;
import java.util.Objects;

/**
 * The VocabularyController class is responsible for managing user input
 * related to vocabulary (verbs, nouns, adjectives) and updating the associated
 * vocabulary files accordingly.
 *
 * <p>Responsibilities:
 * - Validating and processing user input for adjectives, nouns, and verbs.
 * - Interacting with configuration and file management utilities to persist data.
 * - Updating UI components dynamically based on input validation and operation results.
 *
 */
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
        if (configManager.getProperty("ui.theme").equals("dark")) {
            wordInputPane.getStylesheets().add(Objects.requireNonNull(FormController.class.getResource("/style/dark-theme.css")).toExternalForm());
        } else {
            wordInputPane.getStylesheets().clear();
        }
    }

    /**
     * Adds a verb input by the user into the configured vocabulary file.
     *
     * <p>This method validates the user's input, splits it into distinct verbs if multiple are provided,
     * and appends them as separate lines in the vocabulary file specified in the application's configuration.
     * The input field is cleared and its style is reset after successful operation.
     *
     * <p>Throws:
     * - IOException if an error occurs while appending to the vocabulary file.
     */
    public void addVerb() throws IOException {
        String inputText = verbInputField.getText();
        if (inputText == null || inputText.trim().isEmpty()) {
            verbInputField.setPromptText("Empty field: please enter a verb!");
            verbInputField.setStyle("-fx-prompt-text-fill: red;");
            return;
        }
        String[] parts = inputText.split("[ ,]+");
        String result = String.join(System.lineSeparator(), parts);
        FileManager.appendLineToVocabularyFile(ConfigManager.getInstance().getProperty("verb.file"), result);
        verbInputField.clear();
        verbInputField.setPromptText("Enter a verb"); // Reset error prompt
        verbInputField.setStyle(""); // Reset style
    }

    /**
     * Adds a noun input by the user to a vocabulary file after validation.
     *
     * <p>This method:
     * - Validates the input field for empty or null values.
     * - Splits the provided input text into individual words when multiple nouns are provided.
     * - Writes the nouns to a configured vocabulary file using the FileManager utility.
     * - Resets the input field prompt and style after processing.
     *
     * @throws IOException if an I/O error occurs while appending data to the vocabulary file.
     */
    public void addNoun() throws IOException {
        String inputText = nounInputField.getText();
        if (inputText == null || inputText.trim().isEmpty()) {
            nounInputField.setPromptText("Empty field: please enter a noun!");
            nounInputField.setStyle("-fx-prompt-text-fill: red;");
            return;
        }
        String[] parts = inputText.split("[ ,]+");
        String result = String.join(System.lineSeparator(), parts);
        FileManager.appendLineToVocabularyFile(ConfigManager.getInstance().getProperty("noun.file"), result);
        nounInputField.clear();
        nounInputField.setPromptText("Enter a noun"); // Reset error prompt 
        nounInputField.setStyle(""); // Reset style
    }

    /**
     * Adds a new adjective to the vocabulary file if the input is valid.
     *
     * <p>Validates the input from the adjective input field, ensures the field is
     * not empty or invalid, and splits the text into multiple lines if required.
     * Adds the processed adjectives to the vocabulary file and updates the
     * input field UI accordingly, such as resetting the prompt text and styles.
     *
     * @throws IOException if an error occurs while writing to the vocabulary file
     */
    public void addAdjective() throws IOException {
        String inputText = adjectiveInputField.getText();
        if (inputText == null || inputText.trim().isEmpty()) {
            adjectiveInputField.setPromptText("Empty field: please enter an adjective!");
            adjectiveInputField.setStyle("-fx-prompt-text-fill: red;");
            return;
        }
        String[] parts = inputText.split("[ ,]+");
        String result = String.join(System.lineSeparator(), parts);
        FileManager.appendLineToVocabularyFile(ConfigManager.getInstance().getProperty("adjective.file"), result);
        adjectiveInputField.clear();
        adjectiveInputField.setPromptText("Enter an adjective"); // Reset error prompt
        adjectiveInputField.setStyle(""); // Reset style
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