package unipd.edids;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.Objects;

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
    private TextField detailsNonsenseFileField;

    @FXML
    private TextField maxRecursionLevelField;
    @FXML
    private TextField maxSentenceLengthField;

    @FXML
    private CheckBox allowRecursiveSentencesCheck;
    @FXML
    private ComboBox<String> themeComboBox;

    private Stage stage;

    // Deve essere chiamato dalla Application per fornire la Stage
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    // Selettori file
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
    private void selectDetailsNonsenseFile() {
        selectFileForField(detailsNonsenseFileField, "Select Details Nonsense Output File");
    }

    // Metodo comune per scegliere un file e impostare il path nel TextField
    private void selectFileForField(TextField field, String title) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            // Ottieni la directory principale del progetto
            File projectDirectory = new File(System.getProperty("user.dir"));
            // Calcola il percorso relativo
            String relativePath = projectDirectory.toURI().relativize(file.toURI()).getPath();
            // Assegna il percorso relativo al campo testo
            field.setText(relativePath);
        }
    }

    @FXML
    public void initialize() {
        ConfigManager configManager = ConfigManager.getInstance();
        if (configManager.getProperty("ui.theme", "light").equals("dark")) {
            settingsPane.getStylesheets().add(Objects.requireNonNull(FormController.class.getResource("/style/dark-theme.css")).toExternalForm());
        } else {
            settingsPane.getStylesheets().clear();
        }

        // Imposta i campi di testo
        apiKeyFileField.setText(configManager.getProperty("api.key.file", ""));
        nounFileField.setText(configManager.getProperty("noun.file", ""));
        verbFileField.setText(configManager.getProperty("verb.file", ""));
        adjectiveFileField.setText(configManager.getProperty("adjective.file", ""));
        sentenceStructuresFileField.setText(configManager.getProperty("sentence.structures", ""));
        syntaxTagsFileField.setText(configManager.getProperty("syntax_tags.properties", ""));
        outputLogFileField.setText(configManager.getProperty("output.logfile", ""));
        generatedNonsenseFileField.setText(configManager.getProperty("generated.nonsense", ""));
        detailsNonsenseFileField.setText(configManager.getProperty("details.nonsense", ""));

        // Imposta i campi numerici
        maxRecursionLevelField.setText(configManager.getProperty("max.recursion.level", "3"));
        maxSentenceLengthField.setText(configManager.getProperty("max.sentence.length", "150"));

        // Imposta il valore del CheckBox
        allowRecursiveSentencesCheck.setSelected(Boolean.parseBoolean(configManager.getProperty("allow.recursive.sentences", "false")));
        maxRecursionLevelField.disableProperty().setValue(!allowRecursiveSentencesCheck.isSelected());

        // Imposta la ComboBox con i temi (aggiungi i temi disponibili)
        themeComboBox.setValue(configManager.getProperty("ui.theme", "light")); // Imposta il tema default

        logger.info("Settings initialized with default values from configuration.");
    }

    public void closeSettings() {
        if (stage != null) {
            stage.close();
        }
    }

    public void applySettings() {
        ConfigManager configManager = ConfigManager.getInstance();

        // Update ConfigManager with values from text fields
        configManager.setProperty("api.key.file", apiKeyFileField.getText());
        configManager.setProperty("noun.file", nounFileField.getText());
        configManager.setProperty("verb.file", verbFileField.getText());
        configManager.setProperty("adjective.file", adjectiveFileField.getText());
        configManager.setProperty("sentence.structures", sentenceStructuresFileField.getText());
        configManager.setProperty("syntax_tags.properties", syntaxTagsFileField.getText());
        configManager.setProperty("output.logfile", outputLogFileField.getText());
        configManager.setProperty("generated.nonsense", generatedNonsenseFileField.getText());
        configManager.setProperty("details.nonsense", detailsNonsenseFileField.getText());

        // Update ConfigManager with numeric and boolean fields
        configManager.setProperty("max.recursion.level", maxRecursionLevelField.getText());
        configManager.setProperty("max.sentence.length", maxSentenceLengthField.getText());
        configManager.setProperty("allow.recursive.sentences", String.valueOf(allowRecursiveSentencesCheck.isSelected()));

        // Update ConfigManager with theme
        configManager.setProperty("ui.theme", themeComboBox.getValue());
        // Save changes to ConfigManager
        configManager.saveProperties();

        logger.info(configManager.getProperty("verb.file", ""));
        logger.info("Settings applied");
        stage.close();
    }

    public void recursionSelection() {
        maxRecursionLevelField.disableProperty().setValue(!allowRecursiveSentencesCheck.isSelected());
    }
}
