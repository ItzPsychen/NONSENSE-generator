package unipd.edids.userInterface;

import java.util.Properties;

import edu.stanford.nlp.trees.Tree;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.logging.log4j.Logger;
import unipd.edids.logicBusiness.*;
import unipd.edids.logicBusiness.entities.Sentence;
import unipd.edids.logicBusiness.entities.SentenceStructure;
import unipd.edids.logicBusiness.managers.ConfigManager;
import unipd.edids.logicBusiness.managers.LoggerManager;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.stream.Collectors;

public class FormController {
    private static final Logger logger = LoggerManager.getInstance().getLogger(FormController.class);

    private static String currentTheme = "light"; // Tema di default
    @FXML
    private CheckBox checkSaveSentence;
    @FXML
    private CheckBox checkSyntax;
    @FXML
    private CheckBox newWords;
    @FXML
    private ProgressBar toxicityBar;
    @FXML
    private ProgressBar profanityBar;
    @FXML
    private ProgressBar insultBar;
    @FXML
    private ProgressBar sexualBar;
    @FXML
    private ProgressBar politicsBar;
    @FXML
    private CheckBox futureTenseCheck;
    @FXML
    private CheckBox toxicityLevels;
    private TextFlow lastSyntaxFlow; // Campo che salva il contenuto del syntaxArea
    private TextFlow lastGenerateFlow; // Campo che salva il contenuto del generateArea
    @FXML
    private BorderPane rootPane;

    @FXML
    private TextField inputText;
    @FXML
    private ScrollPane scrollPaneSyntax;
    @FXML
    private ScrollPane scrollPaneGenerate;
    @FXML
    private TextFlow syntaxArea;
    @FXML
    private TextFlow generateArea;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private ComboBox<String> structureComboBox;
    @FXML
    private RadioButton randomStructureRadio;

    @FXML
    private RadioButton sameAsAnalyzeRadio;

    @FXML
    private RadioButton selectStructureRadio;
    private ToggleGroup structureToggleGroup;

    private java.util.Timer progressTimer;

    // Close everything
    @FXML
    private void handleClose() {
        if (progressTimer != null) {
            progressTimer.cancel();
            progressTimer.purge();
            progressTimer = null;
        }
        Platform.exit();
    }

    // Delete input (and optionally output)
    @FXML
    private void handleDelete() {
        inputText.clear();
        syntaxArea.getChildren().clear();
        generateArea.getChildren().clear();
        appManager.clearAll();
    }

    // Show About info
    @FXML
    private void handleAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("NONSENSE Generator");
        alert.setContentText("Created by team NoIdeaName\n\n" +
                "→ Casarotto Milo\n→ Donnagemma Davide\n→ Hu Stefania\n→ Maniglio Federico\n\n" +
                "2024/2025 Project\nAnalysis and Generation of syntactic nonsense from your Sentences!");

        Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
        alertStage.getIcons().add(new Image(ConfigManager.getInstance().getProperty("icon.about")));
        alert.showAndWait();
    }

    private AppManager appManager;
    private String textColor;

    public void setFacade(AppManager appManager) {
        this.appManager = appManager;
    }

    private Map<String, String> treeTags;


    public void initialize() {
        ConfigManager configManager = ConfigManager.getInstance();

        // Recupera il tema configurato
        String theme = configManager.getProperty("ui.theme");
        // Applica il tema attuale
        updateTheme(theme);

        structureToggleGroup = new ToggleGroup();

        // Associa i RadioButton al ToggleGroup
        randomStructureRadio.setToggleGroup(structureToggleGroup);
        sameAsAnalyzeRadio.setToggleGroup(structureToggleGroup);
        selectStructureRadio.setToggleGroup(structureToggleGroup);

        ObservableList<String> sentenceStructuresList = FXCollections.observableArrayList(SentenceStructure.getInstance().getStructures());
        structureComboBox.setItems(sentenceStructuresList);
        structureComboBox.getSelectionModel().selectFirst(); // Set default selection to the first item
        structureComboBox.setDisable(!selectStructureRadio.isSelected());

        this.treeTags = loadSyntaxTags(getFilePathTags());

        // Aggiungi il ContextMenu al TextFlow syntaxArea
        addContextMenuToTextFlow(syntaxArea);

        // Aggiungi il ContextMenu al TextFlow generateArea
        addContextMenuToTextFlow(generateArea);

        syntaxArea.prefWidthProperty().bind(scrollPaneSyntax.widthProperty().subtract(20));
        generateArea.prefWidthProperty().bind(scrollPaneGenerate.widthProperty().subtract(20));

        this.progressTimer = new Timer();
    }

    private void addContextMenuToTextFlow(TextFlow textFlow) {
        // Crea il menu contestuale
        ContextMenu contextMenu = new ContextMenu();

        // Opzione "Copy"
        MenuItem copyItem = new MenuItem("Copy");
        copyItem.setOnAction(event -> copyTextFlowContentToClipboard(textFlow));

        // Aggiungi l'opzione al menu
        contextMenu.getItems().add(copyItem);

        // Associa il menu contestuale al TextFlow
        textFlow.setOnContextMenuRequested(e -> {
            contextMenu.show(textFlow, e.getScreenX(), e.getScreenY());
        });
    }

    private void copyTextFlowContentToClipboard(TextFlow textFlow) {
        // StringBuilder per contenere tutto il testo
        StringBuilder content = new StringBuilder();

        // Metodo ricorsivo per ottenere tutto il testo
        extractTextFromNodes(textFlow, content);

        // Copia il testo completo nella clipboard
        javafx.scene.input.Clipboard clipboard = javafx.scene.input.Clipboard.getSystemClipboard();
        javafx.scene.input.ClipboardContent clipboardContent = new javafx.scene.input.ClipboardContent();
        clipboardContent.putString(content.toString());
        clipboard.setContent(clipboardContent);
    }

    private void extractTextFromNodes(javafx.scene.Node node, StringBuilder content) {
        // Controlla il tipo di nodo
        if (node instanceof Text) {
            // Aggiungi il testo dell'oggetto `Text`
            content.append(((Text) node).getText());
        } else if (node instanceof TextFlow) {
            // Itera sui figli di un eventuale `TextFlow` nidificato
            for (javafx.scene.Node child : ((TextFlow) node).getChildren()) {
                extractTextFromNodes(child, content);
            }
        }
    }

    @FXML
    private AnchorPane bottomBar;

    public void updateTheme(String theme) {
        Platform.runLater(() -> {
            currentTheme = theme;
            // Cambia il foglio di stile
            if (currentTheme.equals("dark")) {
                this.textColor = "white";
                rootPane.getStylesheets().add(Objects.requireNonNull(FormController.class.getResource("/style/dark-theme.css")).toExternalForm());
                bottomBar.setStyle("-fx-background-color: #444444;"); // Light color
            } else {
                this.textColor = "black";
                rootPane.getStylesheets().clear();
                bottomBar.setStyle("-fx-background-color: #E0E0E0;"); // Dark color
            }

            // Aggiorna i colori nei TextFlow
            updateTextFlowColors(syntaxArea);
            updateTextFlowColors(generateArea);

            if (lastSyntaxFlow != null) updateTextFlowColors(lastSyntaxFlow);
            if (lastGenerateFlow != null) updateTextFlowColors(lastGenerateFlow);
        });

    }

    public void analyzeClick() {
        progressBar.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
        logger.info("Analyze button clicked");

        // Crea il task da eseguire in background
            Task<Sentence> analyzeTask = new Task<>() {
                @Override
                protected Sentence call() {
                    // Simula tempo di elaborazione (500ms per test)
                    logger.info("Analyze task started");
                    // Chiamata all'analisi da AppManager
                    return appManager.analyzeSentence(inputText.getText(), checkSaveSentence.isSelected());
                }
            };
            TaskManager.execute(analyzeTask, this::handleAnalyzeSuccess);


        // Esegui il task utilizzando TaskManager
    }

    private void handleAnalyzeSuccess(Sentence sentence) {
        Platform.runLater(() -> {
            logger.info("Analyze task finished: {}", sentence.getStructure().toString());

            // Crea e salva il nuovo TextFlow
            lastSyntaxFlow = formatStructure(sentence.getStructure().toString() + " \n");
            lastSyntaxFlow.setMaxWidth(syntaxArea.getWidth());


            // Aggiorna il contenuto del syntaxArea
            syntaxArea.getChildren().clear();
            lastSyntaxFlow.prefWidthProperty().bind(syntaxArea.widthProperty().subtract(20));
            syntaxArea.getChildren().add(lastSyntaxFlow);


            if (checkSyntax.isSelected()) {
                String analysis = prettyTree(sentence.getSyntaxTree());
                Text analysisText = new Text(analysis + "\n");
                analysisText.setFont(Font.font("monospace", 12));
                analysisText.setFill(Color.web(textColor));

                analysisText.wrappingWidthProperty().bind(syntaxArea.widthProperty());
                syntaxArea.getChildren().add(analysisText);
            }

            progressBar.setProgress(1);
            progressTimer.schedule(
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            javafx.application.Platform.runLater(() -> progressBar.setProgress(0));
                        }
                    }, 1000
            );
        });
    }


    public void generateClick() {
        progressBar.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
        logger.info("Generate button clicked");

        // Crea il task da eseguire in background
        Task<Sentence> generateTask = new Task<>() {
            @Override
            protected Sentence call() {
                logger.info("Generation task started");
                // Chiamata all'analisi da AppManager

                String strategy = structureToggleGroup.getSelectedToggle().getUserData().toString();
                logger.info(strategy);

                // Recupera il valore dal combobox se SELECTED è selezionato
                String selectedStructure = "";
                if ("SELECTED".equals(strategy)) {
                    selectedStructure = structureComboBox.getValue(); // Ottieni il valore selezionato
                    logger.info("Selected structure: {}", selectedStructure);
                }

                // Genera la frase passando la strategia e il valore della struttura selezionata
                return appManager.generateSentence(strategy, selectedStructure, toxicityLevels.isSelected(), futureTenseCheck.isSelected(), newWords.isSelected(), checkSaveSentence.isSelected());
            }
        };

        // Esegui il task utilizzando TaskManager
        TaskManager.execute(generateTask, this::handleGenerateSuccess);
    }

    private void handleGenerateSuccess(Sentence sentence) {
        Platform.runLater(() -> {
            logger.info("Generate task finished: {}", sentence.getSentence());

            if (toxicityLevels.isSelected()) {
                animateProgressBar(toxicityBar, sentence.getToxicity());
                animateProgressBar(profanityBar, sentence.getProfanity());
                animateProgressBar(insultBar, sentence.getInsult());
                animateProgressBar(sexualBar, sentence.getSexual());
                animateProgressBar(politicsBar, sentence.getPolitics());

            }
            // Crea e salva il nuovo TextFlow
            lastGenerateFlow = formatStructure(sentence.getStructure().toString());
            lastGenerateFlow.setMaxWidth(generateArea.getWidth());
            // Aggiorna il contenuto del generateArea
            generateArea.getChildren().clear();
            lastGenerateFlow.prefWidthProperty().bind(generateArea.widthProperty().subtract(20));
            generateArea.getChildren().add(lastGenerateFlow);
            generateArea.getChildren().add(new Text("\n\n"));
            Text newText = new Text(sentence.getSentence().toString());
            newText.setFill(Color.web(this.textColor));
            newText.wrappingWidthProperty().bind(generateArea.widthProperty().subtract(20));
            generateArea.getChildren().add(newText);

            progressBar.setProgress(1);
            progressTimer.schedule(
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            javafx.application.Platform.runLater(() -> progressBar.setProgress(0));
                        }
                    }, 1000
            );
        });
    }

    private Stage primaryStage; // Variabile per salvare il primaryStage

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    private TextFlow formatStructure(String structure) {
        TextFlow textFlow = new TextFlow();
        String[] tokens = structure.split("(?<=\\s|[.,;:])|(?=[.,;:|\\s])");

        for (String token : tokens) {
            Text text;
            if (token.equals("[noun]") || token.equals("[verb]") || token.equals("[adjective]")) {
                text = new Text(token.trim() + " ");
                text.setFont(Font.font(null, FontWeight.BOLD, 16));
            } else {
                text = new Text(token + " ");
                text.setFont(Font.font(14));
            }
            textFlow.getChildren().add(text);
        }
        updateTextFlowColors(textFlow);
        return textFlow;
    }

    public void openSettings() {
        logger.info("Open settings button clicked");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Settings.fxml"));
            Parent settingsRoot = loader.load();

            // Ottieni istanza del controller associato
            SettingsController controller = loader.getController();

            // Crea la finestra dello Stage
            Stage settingsStage = new Stage();
            settingsStage.setTitle("Settings");
            settingsStage.initModality(Modality.WINDOW_MODAL); // Finestre modali
            settingsStage.initOwner(primaryStage);
            settingsStage.getIcons().add(new Image(ConfigManager.getInstance().getProperty("icon.settings")));

            // Imposta la scena con il root (settingsRoot) e dimensioni iniziali
            Scene scene = new Scene(settingsRoot); // <-- Dimensioni nella scena
            settingsStage.setScene(scene);

            // Opzionale: Blocca dimensioni minime/massime (se necessario)
            settingsStage.setMinWidth(800);
            settingsStage.setMinHeight(600);
            // Passa lo Stage al controller
            controller.setStage(settingsStage);


            // Mostra la finestra
            settingsStage.showAndWait();
            // Dopo la chiusura delle impostazioni, aggiorna il tema
            String newTheme = ConfigManager.getInstance().getProperty("ui.theme");
            updateTheme(newTheme); // Aggiorna il tema selezionato
        } catch (IOException e) {

            // Puoi mostrare un alert per segnalare l'errore all'utente
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Unable to open Settings");
            alert.setContentText("An error occurred while loading the Settings window:\n" + e.getMessage());
            alert.showAndWait();
        }
    }

    private void updateTextFlowColors(TextFlow textFlow) {
        for (Node node : textFlow.getChildren()) {
            if (node instanceof Text) {
                ((Text) node).setFill(Color.web(this.textColor));
            }
        }
    }

    public void radioPressed() {
        structureComboBox.setDisable(!selectStructureRadio.isSelected());
    }

    public void toxicityPressed() {
        toxicityBar.setDisable(!toxicityLevels.isSelected());
        profanityBar.setDisable(!toxicityLevels.isSelected());
        insultBar.setDisable(!toxicityLevels.isSelected());
        sexualBar.setDisable(!toxicityLevels.isSelected());
        politicsBar.setDisable(!toxicityLevels.isSelected());
    }

    private String getColorForValue(double value) {
        int red = (int) (255 * value);
        int green = (int) (255 * (1 - value));
        return String.format("-fx-accent: rgb(%d, %d, 0);", red, green);
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
        return ConfigManager.getInstance().getProperty("syntax_tags.properties");

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

    public void vocabularyForm() {
        logger.info("Open vacabulary button clicked");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Vocabulary.fxml"));
            Parent vocabularyRoot = loader.load();

            // Ottieni istanza del controller associato
            VocabularyController controller = loader.getController();

            // Crea la finestra dello Stage
            Stage vocabularyStage = new Stage();
            vocabularyStage.setTitle("Vocabulary");
            vocabularyStage.initModality(Modality.WINDOW_MODAL); // Finestre modali
            vocabularyStage.initOwner(primaryStage);
            vocabularyStage.getIcons().add(new Image(ConfigManager.getInstance().getProperty("icon.vocabulary")));

// Imposta la scena con il root (settingsRoot) e dimensioni iniziali
            Scene scene = new Scene(vocabularyRoot); // <-- Dimensioni nella scena
            vocabularyStage.setScene(scene);

// Opzionale: Blocca dimensioni minime/massime (se necessario)
            vocabularyStage.setMinWidth(450);
            vocabularyStage.setMinHeight(450);

            // Passa lo Stage al controller
            controller.setStage(vocabularyStage);


            // Mostra la finestra
            vocabularyStage.showAndWait();
            // Dopo la chiusura delle impostazioni, aggiorna il tema
        } catch (IOException e) {

            // Puoi mostrare un alert per segnalare l'errore all'utente
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Unable to open Settings");
            alert.setContentText("An error occurred while loading the Settings window.");
            alert.showAndWait();
        }
    }

    private void animateProgressBar(ProgressBar bar, double targetValue) {
        double transformed = Math.pow(targetValue, 0.5);

        KeyValue kv = new KeyValue(bar.progressProperty(), transformed, Interpolator.EASE_BOTH);
        KeyFrame kf = new KeyFrame(Duration.millis(500), kv);
        Timeline timeline = new Timeline(kf);
        timeline.play();

        // Cambia anche colore dinamicamente
        bar.setStyle(getColorForValue(targetValue));
    }

    public void useGenerated() {
        logger.info("Use generated button clicked");
        if(appManager.getOutputSentence() != null) {
            inputText.setText(appManager.getOutputSentence().getSentence().toString());
        }
    }
}
