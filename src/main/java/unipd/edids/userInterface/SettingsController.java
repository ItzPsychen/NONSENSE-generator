package unipd.edids.userInterface;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.logging.log4j.Logger;
import unipd.edids.logicBusiness.managers.ConfigManager;
import unipd.edids.logicBusiness.managers.FileManager;
import unipd.edids.logicBusiness.managers.LoggerManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
    ConfigManager configManager = ConfigManager.getInstance();

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
        if (configManager.getProperty("ui.theme").equals("dark")) {
            settingsPane.getStylesheets().add(Objects.requireNonNull(FormController.class.getResource("/style/dark-theme.css")).toExternalForm());
        } else {
            settingsPane.getStylesheets().clear();
        }

        loadItems();
    }

    private void loadItems() {

        // Imposta i campi di testo con valori di default vuoti se le proprietà non esistono
        apiKeyFileField.setText(getPropertyOrDefault(configManager, "api.key.file", ""));
        nounFileField.setText(getPropertyOrDefault(configManager, "noun.file", ""));
        verbFileField.setText(getPropertyOrDefault(configManager, "verb.file", ""));
        adjectiveFileField.setText(getPropertyOrDefault(configManager, "adjective.file", ""));
        sentenceStructuresFileField.setText(getPropertyOrDefault(configManager, "sentence.structures", ""));
        syntaxTagsFileField.setText(getPropertyOrDefault(configManager, "syntax_tags.properties", ""));
        outputLogFileField.setText(getPropertyOrDefault(configManager, "output.logfile", ""));
        generatedNonsenseFileField.setText(getPropertyOrDefault(configManager, "generated.save.file", ""));
        detailsNonsenseFileField.setText(getPropertyOrDefault(configManager, "analyzed.save.file", ""));

        // Imposta i campi numerici con valori di default (se necessari)
        maxRecursionLevelField.setText(getPropertyOrDefault(configManager, "max.recursion.level", "10"));
        maxSentenceLengthField.setText(getPropertyOrDefault(configManager, "max.sentence.length", "50"));

        // Imposta il valore del CheckBox con valore di default
        allowRecursiveSentencesCheck.setSelected(Boolean.parseBoolean(getPropertyOrDefault(configManager, "allow.recursive.sentences", "false")));
        maxRecursionLevelField.disableProperty().setValue(!allowRecursiveSentencesCheck.isSelected());

        // Imposta la ComboBox con un tema di default
        themeComboBox.setValue(getPropertyOrDefault(configManager, "ui.theme", "light"));

        logger.info("Settings initialized with default values from configuration.");
    }

    private String getPropertyOrDefault(ConfigManager configManager, String key, String defaultValue) {
        try {
            // Prova ad ottenere il valore della proprietà
            return configManager.getProperty(key);
        } catch (IllegalArgumentException e) {
            // Se la proprietà non esiste, restituisci il valore di default
            logger.warn("Property '{}' not found. Using default value '{}'.", key, defaultValue);
            return defaultValue;
        }
    }

    public void closeSettings() {
        if (stage != null) {
            stage.close();
        }
    }

    public void applySettings() {
        // Check se il campo apiKeyFileField è vuoto
        if (apiKeyFileField.getText() == null || apiKeyFileField.getText().trim().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING); // Mostra un avviso all'utente
            alert.setTitle("Campo obbligatorio mancante");
            alert.setHeaderText("Il campo API Key File non può essere vuoto.");
            alert.setContentText("Inserisci il percorso del file della chiave API prima di applicare le impostazioni.");
            alert.showAndWait();
            return; // Non chiudere la finestra
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
        configManager.setProperty("analyzed.save.file", detailsNonsenseFileField.getText());

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

    public void recursionSelection() {
        maxRecursionLevelField.disableProperty().setValue(!allowRecursiveSentencesCheck.isSelected());
    }


    public void resetToDefault() throws IOException {
        String apiKeyFile = configManager.getProperty("api.key.file");
        try {
            String defaultConfigPath = configManager.getEnv("DEFAULT_CONFIG_FILE_PATH");
            String configFilePath = configManager.getConfigFilePath();

            // Verifica se 'DEFAULT_CONFIG_FILE_PATH' esiste
            File defaultConfigFile = new File(defaultConfigPath);
            if (!defaultConfigFile.exists()) {
                logger.error("File di configurazione di default non trovato: {}", defaultConfigPath);
                throw new IOException("File di configurazione di default mancante o non accessibile: " + defaultConfigPath);
            }

            // Usa un file temporaneo per la scrittura
            File tempFile = new File(configFilePath + ".tmp");

            // Elimina il file corrente esistente
            FileManager.deleteFile(configFilePath);

            // Copia il contenuto del file di default nel file temporaneo
            List<String> lines = new ArrayList<>(FileManager.readFile(defaultConfigPath));
            String newApiLine = "api.key.file=" + apiKeyFile;

            if (lines.removeIf(line -> line.startsWith("api.key.file="))) {
                lines.add(newApiLine);
            } else {
                lines.add(newApiLine);
            }

            for (String line : lines) {
                FileManager.appendLineToSavingFile(tempFile.getAbsolutePath(), line);
            }

            // Rinomina il file temporaneo in quello ufficiale
            if (!tempFile.renameTo(new File(configFilePath))) {
                throw new IOException("Impossibile rinominare il file temporaneo in: " + configFilePath);
            }

            // Ricarica la configurazione nel ConfigManager
            configManager.loadProperties();
            loadItems(); // Reinizializza l'interfaccia con i valori aggiornati

            logger.info("Impostazioni ripristinate correttamente ai valori di default.");
        } catch (IOException e) {
            logger.error("Errore durante il reset alle impostazioni di default: {}", e.getMessage());
            throw new IOException("Errore durante il reset: " + e.getMessage());
        }
    }}
