package unipd.edids.userInterface;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.logging.log4j.Logger;
import unipd.edids.logicBusiness.exceptions.MissingApiKeyException;
import unipd.edids.logicBusiness.managers.ConfigManager;
import unipd.edids.logicBusiness.managers.LoggerManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Controller responsible for managing the settings interface in the application.
 * <p>
 * - Handles user interactions for settings configuration input.
 * - Applies and validates configuration changes.
 * - Implements a Singleton design pattern for dependency use with ConfigManager and LoggerManager.
 * </p>
 *
 * Attributes:
 * - logger: Logger instance for logging events and errors.
 * - settingsPane: BorderPane component for the settings interface layout.
 * - apiKeyFileField: TextField for the API key file path.
 * - nounFileField: TextField for the noun file path.
 * - verbFileField: TextField for the verb file path.
 * - adjectiveFileField: TextField for the adjective file path.
 * - sentenceStructuresFileField: TextField for sentence structures configuration.
 * - syntaxTagsFileField: TextField for syntax tags configuration.
 * - outputLogFileField: TextField for log file storage path.
 * - generatedNonsenseFileField: TextField for generated nonsense output file.
 * - analysisNonsenseFileField: TextField for analysis nonsense output file.
 * - maxRecursionLevelField: TextField for setting maximum allowed recursion levels.
 * - maxSentenceLengthField: TextField for maximum sentence length configuration.
 * - allowRecursiveSentencesCheck: CheckBox for enabling/disabling recursive sentences.
 * - themeComboBox: ComboBox for selecting the application UI theme.
 * - configManager: Singleton instance for centralized configuration management.
 * - stage: Stage reference for managing the settings window lifecycle.
 * <p>
 * Constructor:
 * Initializes dependencies including the configuration manager and logger setup.
 * <p>
 * Methods:
 * - setStage: Assigns the application `Stage` to the controller.
 * - selectApiKeyFile: Facilitates file selection for the API Key field.
 * - selectNounFile: Facilitates file selection for the Noun file field.
 * - selectVerbFile: Facilitates file selection for the Verb file field.
 * - selectAdjectiveFile: Facilitates file selection for the Adjective file field.
 * - selectSentenceStructuresFile: Facilitates file selection for the Sentence Structures field.
 * - selectSyntaxTagsFile: Facilitates file selection for the Syntax Tags field.
 * - selectOutputLogFile: Facilitates file selection for the Output Log field.
 * - selectGeneratedNonsenseFile: Facilitates file selection for the Generated Nonsense file field.
 * - selectAnalysisNonsenseFile: Facilitates file selection for the Analysis Nonsense file field.
 * - selectFileForField: Generic method to select and assign file paths to text fields.
 * - initialize: Initializes the settings UI, applies current configuration settings and themes.
 * - loadItems: Loads configuration values into respective UI components.
 * - getPropertyOrDefault: Retrieves configuration properties or falls back to default values.
 * - closeSettings: Closes the settings window.
 * - applySettings: Validates and applies configuration changes to the application.
 * - getEmptyFields: Identifies text fields with missing required to be input.
 * - recursionSelection: Toggles enablement of recursion-related input fields.
 * - resetToDefault: Resets all configuration values back to their defined defaults.
 */
public class SettingsController {
    private static final Logger logger = LoggerManager.getInstance().getLogger(SettingsController.class);
    @FXML
    private BorderPane settingsPane;
    @FXML
    private TextField apiKeyFileField;
    @FXML
    private TextField nounFileField;
    @FXML
    private TextField verbFileField;
    @FXML
    private TextField adjectiveFileField;
    @FXML
    private TextField sentenceStructuresFileField;
    @FXML
    private TextField syntaxTagsFileField;
    @FXML
    private TextField outputLogFileField;
    @FXML
    private TextField generatedNonsenseFileField;
    @FXML
    private TextField analysisNonsenseFileField;

    @FXML
    private TextField maxRecursionLevelField;
    @FXML
    private TextField maxSentenceLengthField;

    @FXML
    private CheckBox allowRecursiveSentencesCheck;
    @FXML
    private ComboBox<String> themeComboBox;
    final ConfigManager configManager = ConfigManager.getInstance();

    private Stage stage;

    // Must be called by the Application to provide the Stage
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    // File selectors 
    @FXML
    private void selectApiKeyFile() {
        selectFileForField(apiKeyFileField, "Select API Key File");
    }

    @FXML
    private void selectNounFile() {
        selectFileForField(nounFileField, "Select Noun File");
    }

    @FXML
    private void selectVerbFile() {
        selectFileForField(verbFileField, "Select Verb File");
    }

    @FXML
    private void selectAdjectiveFile() {
        selectFileForField(adjectiveFileField, "Select Adjective File");
    }

    @FXML
    private void selectSentenceStructuresFile() {
        selectFileForField(sentenceStructuresFileField, "Select Sentence Structures File");
    }

    @FXML
    private void selectSyntaxTagsFile() {
        selectFileForField(syntaxTagsFileField, "Select Syntax Tags File");
    }

    @FXML
    private void selectOutputLogFile() {
        selectFileForField(outputLogFileField, "Select Log File");
    }

    @FXML
    private void selectGeneratedNonsenseFile() {
        selectFileForField(generatedNonsenseFileField, "Select Generated Nonsense Output File");
    }

    @FXML
    private void selectAnalysisNonsenseFile() {
        selectFileForField(analysisNonsenseFileField, "Select Details Nonsense Output File");
    }

    /**
     * Opens a file chooser dialog, selects a file, and sets its relative path in the specified TextField.
     *
     * @param field The TextField where the selected file's relative path will be displayed.
     * @param title The title for the file chooser dialog.
     */
    private void selectFileForField(TextField field, String title) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            // Get the main project directory
            File projectDirectory = new File(System.getProperty("user.dir"));
            // Calculate relative path 
            String relativePath = projectDirectory.toURI().relativize(file.toURI()).getPath();
            // Assign a relative path to the text field
            field.setText(relativePath);
        }
    }

    @FXML
    public void initialize() {
        if (configManager.getProperty("ui.theme").equals("dark")) {
            settingsPane.getStylesheets().add(Objects.requireNonNull(FormController.class.getResource("/style/dark-theme.css")).toExternalForm());
        } else {
            settingsPane.getStylesheets().clear();
        }

        loadItems();
    }

    /**
     * Loads and initializes the settings fields with default or configured values.
     * <p>
     * This method retrieves stored configuration values using `getPropertyOrDefault`
     * and assigns them to UI components such as text fields, combo boxes,
     * and checkboxes. If no configuration exists for a particular field,
     * a specified default value is used. Fields such as numeric inputs and
     * checkboxes are initialized with additional logic to handle interdependent states.
     * <p>
     * Logs information about the successful initialization of settings.
     */
    private void loadItems() {

        // Set text fields with empty default values if properties don't exist
        apiKeyFileField.setText(getPropertyOrDefault(configManager, "api.key.file", ""));
        nounFileField.setText(getPropertyOrDefault(configManager, "noun.file", ""));
        verbFileField.setText(getPropertyOrDefault(configManager, "verb.file", ""));
        adjectiveFileField.setText(getPropertyOrDefault(configManager, "adjective.file", ""));
        sentenceStructuresFileField.setText(getPropertyOrDefault(configManager, "sentence.structures", ""));
        syntaxTagsFileField.setText(getPropertyOrDefault(configManager, "syntax_tags.properties", ""));
        outputLogFileField.setText(getPropertyOrDefault(configManager, "output.logfile", ""));
        generatedNonsenseFileField.setText(getPropertyOrDefault(configManager, "generated.save.file", ""));
        analysisNonsenseFileField.setText(getPropertyOrDefault(configManager, "analyzed.save.file", ""));

        // Set numeric fields with default values (if needed)
        maxRecursionLevelField.setText(getPropertyOrDefault(configManager, "max.recursion.level", "10"));
        maxSentenceLengthField.setText(getPropertyOrDefault(configManager, "max.sentence.length", "50"));

        // Set CheckBox value with default 
        allowRecursiveSentencesCheck.setSelected(Boolean.parseBoolean(getPropertyOrDefault(configManager, "allow.recursive.sentences", "false")));
        maxRecursionLevelField.disableProperty().setValue(!allowRecursiveSentencesCheck.isSelected());

        // Set ComboBox with default theme
        themeComboBox.setValue(getPropertyOrDefault(configManager, "ui.theme", "light"));

        logger.info("Settings initialized with default values from configuration.");
    }

    /**
     * Retrieves the value of a property for the given key from the ConfigManager.
     * If the property is not found or an exception occurs, it returns the specified default value.
     *
     * @param configManager The configuration manager used to retrieve the property value.
     * @param key The key of the property to retrieve.
     * @param defaultValue The default value to return if the property is not found.
     * @return The property value if found, otherwise the default value.
     */
    private String getPropertyOrDefault(ConfigManager configManager, String key, String defaultValue) {
        try {
            // Try to get property value
            return configManager.getProperty(key);
        } catch (IllegalArgumentException | MissingApiKeyException e) {
            // If a property doesn't exist or an API key is missing, return the default value
            logger.warn("Property '{}' not found. Using default value '{}'.", key, defaultValue);
            return defaultValue;
        }
    }

    public void closeSettings() {
        if (stage != null) {
            stage.close();
        }
    }

    /**
     * Applies and saves user-configured settings.
     * <p>
     * This method performs the following operations:
     * - Validates that required fields are not empty, showing a warning if necessary.
     * - Updates the configuration manager with user-provided values from text fields, combo boxes, and checkboxes.
     * - Persists the updated settings by saving them to the configuration file.
     * - Logs the successful application of settings and closes the settings window.
     */
    public void applySettings() {
        // Check if any required fields are empty
        List<String> emptyFields = getEmptyFields();

        if (!emptyFields.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Missing Required Fields");
            alert.setHeaderText("The following fields cannot be empty:");
            alert.setContentText(String.join("\n", emptyFields));
            alert.showAndWait();
            return;
        }

        // Update ConfigManager with values from text fields
        configManager.setProperty("api.key.file", apiKeyFileField.getText());
        configManager.setProperty("noun.file", nounFileField.getText());
        configManager.setProperty("verb.file", verbFileField.getText());
        configManager.setProperty("adjective.file", adjectiveFileField.getText());
        configManager.setProperty("sentence.structures", sentenceStructuresFileField.getText());
        configManager.setProperty("syntax_tags.properties", syntaxTagsFileField.getText());
        configManager.setProperty("output.logfile", outputLogFileField.getText());
        configManager.setProperty("generated.save.file", generatedNonsenseFileField.getText());
        configManager.setProperty("analyzed.save.file", analysisNonsenseFileField.getText());

        // Update ConfigManager with numeric and boolean fields
        configManager.setProperty("max.recursion.level", maxRecursionLevelField.getText());
        configManager.setProperty("max.sentence.length", maxSentenceLengthField.getText());
        configManager.setProperty("allow.recursive.sentences", String.valueOf(allowRecursiveSentencesCheck.isSelected()));

        // Update ConfigManager with theme
        configManager.setProperty("ui.theme", themeComboBox.getValue());
        // Save changes to ConfigManager
        configManager.saveProperties();

        logger.info("Settings applied");
        closeSettings();
    }

    /**
     * Retrieves a list of field names that are empty.
     * <p>
     * This method checks multiple text fields to determine whether their text values
     * are null or empty and returns a list of field names that need input.
     *
     * @return A list of string names of the empty fields, indicating required input fields.
     */
    private List<String> getEmptyFields() {
        List<String> emptyFields = new ArrayList<>();

        if (apiKeyFileField.getText() == null || apiKeyFileField.getText().trim().isEmpty()) {
            emptyFields.add("API Key File");
        }
        if (nounFileField.getText() == null || nounFileField.getText().trim().isEmpty()) {
            emptyFields.add("Noun File");
        }
        if (verbFileField.getText() == null || verbFileField.getText().trim().isEmpty()) {
            emptyFields.add("Verb File");
        }
        if (adjectiveFileField.getText() == null || adjectiveFileField.getText().trim().isEmpty()) {
            emptyFields.add("Adjective File");
        }
        if (sentenceStructuresFileField.getText() == null || sentenceStructuresFileField.getText().trim().isEmpty()) {
            emptyFields.add("Sentence Structures File");
        }
        if (syntaxTagsFileField.getText() == null || syntaxTagsFileField.getText().trim().isEmpty()) {
            emptyFields.add("Syntax Tags File");
        }
        if (outputLogFileField.getText() == null || outputLogFileField.getText().trim().isEmpty()) {
            emptyFields.add("Output Log File");
        }
        if (generatedNonsenseFileField.getText() == null || generatedNonsenseFileField.getText().trim().isEmpty()) {
            emptyFields.add("Generated Nonsense File");
        }
        if (analysisNonsenseFileField.getText() == null || analysisNonsenseFileField.getText().trim().isEmpty()) {
            emptyFields.add("Analysis Nonsense File");
        }
        return emptyFields;
    }

    public void recursionSelection() {
        maxRecursionLevelField.disableProperty().setValue(!allowRecursiveSentencesCheck.isSelected());
    }


    /**
     * Resets all settings to their default values and reloads the interface.
     *
     * <p>
     * - Retrieves the file path for the API key from the configuration manager.
     * - Invokes the configuration manager's reset method to apply default settings.
     * - Reloads interface components by calling the method to initialize UI values.
     * - Logs the operation's success or failure, throwing an IOException on error.
     *
     * @throws IOException if an error occurs during the reset process.
     */
    public void resetToDefault() throws IOException {
        try {
            configManager.resetDefault();
            loadItems(); // Reinitialize interface with updated values

            logger.info("Settings successfully restored to default values.");
        } catch (Exception e) {
            logger.error("Error during reset to default settings: {}", e.getMessage());
            throw new IOException("Error during reset: " + e.getMessage());
        }
    }
}