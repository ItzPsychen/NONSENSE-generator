package unipd.edids;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;

//package unipd.edids;
//
//import javafx.animation.Interpolator;
//import javafx.animation.KeyFrame;
//import javafx.animation.KeyValue;
//import javafx.animation.Timeline;
//import javafx.application.Platform;
//import javafx.concurrent.Task;
//import javafx.event.ActionEvent;
//import javafx.fxml.FXML;
//import javafx.scene.control.*;
//import javafx.scene.layout.BorderPane;
//import javafx.scene.paint.Color;
//import javafx.scene.text.Text;
//import javafx.scene.text.TextFlow;
//import javafx.scene.text.Font;
//import javafx.scene.text.FontWeight;
//
//import edu.stanford.nlp.trees.Tree;
//import javafx.util.Duration;
//
//import java.nio.file.Paths;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.util.Map;
//import java.util.Properties;
//import java.util.stream.Collectors;
//
//

import javafx.event.ActionEvent;

/// /Fix everything, il controller non si occupa della logica di business
//
//public class FormController {
//    public BorderPane rootPane;
//    private AppManager appManager;
//    private boolean save;
//
//    private boolean darkThemeEnabled = false;
//    private String textColor = "black";
//    private TextFlow lastStructureFlow;
//    private Text lastGeneratedSentenceText;
//
//    private Map<String, String> treeTags;
//
//    public FormController() {
//        this.appManager = new AppManager();
//        this.save = false;
//        this.treeTags = loadSyntaxTags(getFilePathTags());
//    }
//
//    @FXML
//    private TextField inputText;
//
//    @FXML
//    public void initialize() {
//        inputText.textProperty().addListener((observable, oldValue, newValue) -> {
//            appManager.setModified(true);
//        });
//    }
//
//    @FXML
//    private TextFlow syntaxArea;
//    @FXML
//    private CheckBox checkSyntax;
//    @FXML
//    private TextFlow generateArea;
//    @FXML
//    private CheckBox checkSaveSentence;
//    @FXML
//    private ProgressBar progressBar;
//
//    @FXML
//    private ProgressBar toxicityBar;
//    @FXML
//    private ProgressBar profanityBar;
//    @FXML
//    private ProgressBar insultBar;
//    @FXML
//    private ProgressBar threatBar;
//    @FXML
//    private ProgressBar identityThreatBar;
//
//    // Close everything
//    @FXML
//    private void handleClose() {
//        Platform.exit();
//    }
//
//    // Delete input (and optionally output)
//    @FXML
//    private void handleDelete() {
//        inputText.clear();
//        syntaxArea.getChildren().clear();
//        generateArea.getChildren().clear();
//        appManager.clearAll();
//    }
//
//    // Show About info
//    @FXML
//    private void handleAbout() {
//        Alert alert = new Alert(Alert.AlertType.INFORMATION);
//        alert.setTitle("About");
//        alert.setHeaderText("NONSENSE Generator");
//        alert.setContentText("Created by team NoIdeaName\n\n" +
//                "Casarotto Milo\nDonnagemma Davide\nHu Stefania\nManiglio Federico\n\n" +
//                "2024/2025 Project\nAnalysis and Generation of syntactic nonsense from your Sentences!");
//        alert.showAndWait();
//    }
//
//    @FXML
//    public void analyzeClick() {
//        progressBar.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
//
//        // background task
//        Task<Sentence> analyzeTask = new Task<Sentence>() {
//            @Override
//            protected Sentence call() throws Exception {
//                // Simulate processing time (remove in production)
//                Thread.sleep(500);
//                System.out.println(inputText.getText());
//                Sentence result = appManager.analyzeSentence(inputText.getText());
//                if (result.getSentence().toString().charAt(0) == '#') throw new Exception(result.getSentence().toString());
//                return result;
//            }
//        };
//
//        // Completion handler
//        analyzeTask.setOnSucceeded(event -> {
//            Sentence analyzeResult = analyzeTask.getValue();
//
//            // Remove the previous content
//            syntaxArea.getChildren().clear();
//
//            // Display text (styled)
//            Text structureText = new Text(analyzeResult.getStructure().toString() + "\n");
//            structureText.setFont(Font.font(16));
//            structureText.setFill(Color.web(textColor));
//
//            syntaxArea.getChildren().add(structureText);
//
//            // Syntax tree
//            if (checkSyntax.isSelected()) {
//                String analysis = prettyTree(analyzeResult.getSyntaxTree());
//                Text analysisText = new Text(analysis + "\n");
//                analysisText.setFont(Font.font("monospace", 12));
//                analysisText.setFill(Color.web(textColor));
//
//                syntaxArea.getChildren().add(analysisText);
//            }
//
//            // Analysis scores
//            Text scoreAnalysis = new Text();
//            scoreAnalysis.setText("\nToxicity\t\t\t\t" + analyzeResult.getToxicity() +
//                    "\nProfanity\t\t\t\t" + analyzeResult.getProfanity() +
//                    "\nInsult\t\t\t\t" + analyzeResult.getInsult() +
//                    "\nThreat\t\t\t\t" + analyzeResult.getThreat() +
//                    "\nIdentity Threat\t\t" + analyzeResult.getIdentityThreat() + "\n");
//            scoreAnalysis.setFont(Font.font("monospace", 12));
//            scoreAnalysis.setFill(Color.web(textColor));
//            syntaxArea.getChildren().add(scoreAnalysis);
//
//            animateProgressBar(toxicityBar, analyzeResult.getToxicity());
//            animateProgressBar(profanityBar, analyzeResult.getProfanity());
//            animateProgressBar(insultBar, analyzeResult.getInsult());
//            animateProgressBar(threatBar, analyzeResult.getThreat());
//            animateProgressBar(identityThreatBar, analyzeResult.getIdentityThreat());
//
//            // Complete progress
//            progressBar.setProgress(1);
//            new java.util.Timer().schedule(
//                    new java.util.TimerTask() {
//                        @Override
//                        public void run() {
//                            javafx.application.Platform.runLater(() -> progressBar.setProgress(0));
//                        }
//                    },
//                    1000
//            );
//        });
//
//        // Set error handler
//        analyzeTask.setOnFailed(event -> {
//            progressBar.setProgress(0);
//
//            String errorMessage = getErrorMessage(analyzeTask.getException().getMessage());
//            if (errorMessage == null) return;
//
//            Text warningText = new Text("❌ WARNING\n");
//            Text errorText = new Text(errorMessage);
//            warningText.setStyle("-fx-fill: red; -fx-font-size: 16px; -fx-font-weight: bold;");
//            errorText.setStyle("-fx-fill: red; -fx-font-size: 12px;");
//
//            syntaxArea.getChildren().clear();
//            syntaxArea.getChildren().add(warningText);
//            syntaxArea.getChildren().add(errorText);
//
//            showErrorDialog("Analysis Error", "An error occurred during analysis", errorMessage);
//            appManager.clearAll();
//        });
//
//        // Start the task in a new thread
//        new Thread(analyzeTask).start();
//    }
//
//    private String prettyTree(Tree tree) {
//        return "\nTREE\n" + prettyTreeHelper(tree.children()[0], "", true);
//    }
//
//    private String prettyTreeHelper(Tree tree, String prefix, boolean isLast) {
//        StringBuilder builder = new StringBuilder();
//
//        // Add the prefix and the current node's value
//        builder.append(prefix);
//        builder.append(isLast ? "└── " : "├── ");
//        builder.append(treeTags.getOrDefault(tree.value(), tree.value())).append("\n");
//
//        // Prepare prefix for children
//        Tree[] children = tree.children();
//        for (int i = 0; i < children.length; i++) {
//            boolean last = (i == children.length - 1);
//            String newPrefix = prefix + (isLast ? "    " : "│   ");
//            builder.append(prettyTreeHelper(children[i], newPrefix, last));
//        }
//
//        return builder.toString();
//    }
//
//    @FXML
//    public void generateClick() {
//        // Start progress (indeterminate mode)
//        progressBar.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
//
//        // Create a background task
//        Task<Sentence> generateTask = new Task<Sentence>() {
//            @Override
//            protected Sentence call() throws Exception {
//                // Simulate processing time (remove in production)
//                Thread.sleep(500);
//                Sentence result = appManager.generateSentence(checkSaveSentence.isSelected());
//                if (result == null) throw new Exception("Please, analyze your sentence before generating.");
//                return result;
//            }
//        };
//
//        // Set completion handler
//        generateTask.setOnSucceeded(event -> {
//            Sentence generateResult = generateTask.getValue();
//
//            // Retry if null (recursive call)
//            if (generateResult == null) {
//                generateClick();
//                return;
//            }
//
//            // Clear previous content
//            generateArea.getChildren().clear();
//
//            // Display results
//            String structure = generateResult.getStructure().toString();
//
//            String sentence = generateResult.getSentence().toString();
//            lastStructureFlow = formatStructure(structure);
//            lastGeneratedSentenceText = new Text(sentence);
//            lastGeneratedSentenceText.setFont(Font.font(20));
//            lastGeneratedSentenceText.setUnderline(true);
//            lastGeneratedSentenceText.setFill(Color.web(textColor));
//
//            generateArea.getChildren().addAll(lastStructureFlow, new Text("\n\n"), lastGeneratedSentenceText);
//
//
//            // Complete progress
//            progressBar.setProgress(1);
//            new java.util.Timer().schedule(
//                    new java.util.TimerTask() {
//                        @Override
//                        public void run() {
//                            javafx.application.Platform.runLater(() -> progressBar.setProgress(0));
//                        }
//                    },
//                    1000
//            );
//        });
//
//        // Set error handler
//        generateTask.setOnFailed(event -> {
//            progressBar.setProgress(0);
//            showErrorDialog("Generation Error", "An error occurred during generation",
//                    generateTask.getException().getMessage());
//        });
//
//        // Start the task in a new thread
//        new Thread(generateTask).start();
//    }
//
//    // Metodo helper per formattare la struttura
//    private TextFlow formatStructure(String structure) {
//        TextFlow textFlow = new TextFlow();
//        String[] tokens = structure.split(" ");
//
//        for (String token : tokens) {
//            Text text;
//
//            if (token.equals("[noun]") || token.equals("[verb]") || token.equals("[adjective]")) {
//                text = new Text(token + " ");
//                text.setFont(Font.font(null, FontWeight.BOLD, 14));
//            } else {
//                text = new Text(token + " ");
//                text.setFont(Font.font(14));
//            }
//
//            text.setFill(javafx.scene.paint.Color.web(textColor));
//            textFlow.getChildren().add(text);
//        }
//
//        return textFlow;
//    }
//
//
//    public void showErrorDialog(String title, String headerText, String contentText) {
//        Alert alert = new Alert(Alert.AlertType.ERROR);
//        alert.setTitle(title);
//        alert.setHeaderText(headerText);
//        alert.setContentText(contentText);
//
//        // Per mostrare il dialogo e aspettare che l'utente lo chiuda
//        alert.showAndWait();
//    }
//
//    private Map<String, String> loadSyntaxTags(String filePath) {
//        Properties properties = new Properties();
//        try (FileInputStream fis = new FileInputStream(Paths.get(filePath).toFile())) {
//            properties.load(fis); // Load the .properties file
//            // Convert Properties to Map<String, String>
//            return properties.entrySet().stream()
//                    .collect(Collectors.toMap(
//                            e -> e.getKey().toString(),
//                            e -> e.getValue().toString()
//                    ));
//        } catch (IOException e) {
//            System.err.println("Error loading properties file: " + filePath);
//            e.printStackTrace();
//            return Map.of(); // Return empty Map if error occurs
//        }
//    }
//
//    private String getFilePathTags() {
//        return ConfigManager.getInstance().getProperty("syntax_tags.properties","./src/main/resources/properties/syntax_tags.properties");
//    }
//
//    private String getErrorMessage(String message) {
//        switch (message) {
//            case "#notmodified": return null;
//            case "#length": return "The sentence is too short.";
//            case "#chars": return "The sentence must contain words.";
//            case "#invalid": return "The sentence uses improper words.";
//        }
//        return null;
//    }
//
//    @FXML
//    private ToggleButton themeToggle; // New toggle
//
//    @FXML
//    private void handleSettings() {
//        darkThemeEnabled = themeToggle.isSelected();
//        String newColor = darkThemeEnabled ? "white" : "black";
//
//        if (darkThemeEnabled) {
//            rootPane.getStylesheets().add(getClass().getResource("/style/dark-theme.css").toExternalForm());
//        } else {
//            rootPane.getStylesheets().clear();
//        }
//
//        updateTextFlowColors(syntaxArea, newColor);
//        updateTextFlowColors(generateArea, newColor);
//        this.textColor = newColor;
//
//        if (lastStructureFlow != null) updateTextFlowColors(lastStructureFlow, textColor);
//        if (lastGeneratedSentenceText != null) lastGeneratedSentenceText.setFill(Color.web(textColor));
//    }
//
//    private void updateTextFlowColors(TextFlow textFlow, String color) {
//        for (javafx.scene.Node node : textFlow.getChildren()) {
//            if (node instanceof Text) {
//                ((Text) node).setFill(javafx.scene.paint.Color.web(color));
//            }
//        }
//    }
//
//    private String getColorForValue(double value) {
//        int red = (int) (255 * value);
//        int green = (int) (255 * (1 - value));
//        return String.format("-fx-accent: rgb(%d, %d, 0);", red, green);
//    }
//
//    private void animateProgressBar(ProgressBar bar, double targetValue) {
//        double transformed = Math.pow(targetValue, 0.5);
//
//        KeyValue kv = new KeyValue(bar.progressProperty(), transformed, Interpolator.EASE_BOTH);
//        KeyFrame kf = new KeyFrame(Duration.millis(500), kv);
//        Timeline timeline = new Timeline(kf);
//        timeline.play();
//
//        // Cambia anche colore dinamicamente
//        bar.setStyle(getColorForValue(targetValue));
//    }
//}

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Objects;

public class FormController {
    private static final Logger logger = LoggerManager.getInstance().getLogger(FormController.class);

    private static String currentTheme = "light"; // Tema di default
    private TextFlow lastSyntaxFlow; // Campo che salva il contenuto del syntaxArea
    private TextFlow lastGenerateFlow; // Campo che salva il contenuto del generateArea
    @FXML
    private BorderPane rootPane;

    @FXML
    private TextField inputText;
    @FXML
    private TextFlow syntaxArea;
    @FXML
    private TextFlow generateArea;
    @FXML
    private ProgressBar progressBar;

    private AppManager appManager;

    public void setFacade(AppManager appManager) {
        this.appManager = appManager;
    }

    public void initialize() {
        ConfigManager configManager = ConfigManager.getInstance();

        // Recupera il tema configurato
        String theme = configManager.getProperty("ui.theme", "light");

        // Applica il tema attuale
        updateTheme(theme);
    }

    public void updateTheme(String theme) {
        Platform.runLater(() -> {
            currentTheme = theme;
            // Cambia il foglio di stile
            if (currentTheme.equals("dark")) {
                rootPane.getStylesheets().add(Objects.requireNonNull(FormController.class.getResource("/style/dark-theme.css")).toExternalForm());
            } else {
                rootPane.getStylesheets().clear();
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
                return appManager.analyzeSentence(inputText.getText());
            }
        };

        // Esegui il task utilizzando TaskManager
        TaskManager.execute(analyzeTask, this::handleAnalyzeSuccess);
    }

    private void handleAnalyzeSuccess(Sentence sentence) {
        Platform.runLater(() -> {
            logger.info("Analyze task finished: {}", sentence.getStructure().toString());

            // Crea e salva il nuovo TextFlow
            lastSyntaxFlow = formatStructure(sentence.getStructure().toString() + " \n");

            // Aggiorna il contenuto del syntaxArea
            syntaxArea.getChildren().clear();
            syntaxArea.getChildren().add(lastSyntaxFlow);

            progressBar.setProgress(1);
        });
    }


    public void generateClick() {
        progressBar.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
        logger.info("Generate button clicked");

        // Crea il task da eseguire in background
        Task<Sentence> analyzeTask = new Task<>() {
            @Override
            protected Sentence call() {
                logger.info("Generation task started");
                // Chiamata all'analisi da AppManager
                return appManager.generateSentence(false);
            }
        };

        // Esegui il task utilizzando TaskManager
        TaskManager.execute(analyzeTask, this::handleGenerateSuccess);
    }

    private void handleGenerateSuccess(Sentence sentence) {
        Platform.runLater(() -> {
            logger.info("Generate task finished: {}", sentence.getSentence());

            // Crea e salva il nuovo TextFlow
            lastGenerateFlow = formatStructure(sentence.getStructure().toString());

            // Aggiorna il contenuto del generateArea
            generateArea.getChildren().clear();
            generateArea.getChildren().add(lastGenerateFlow);
            generateArea.getChildren().add(new Text("\n\n"));
            generateArea.getChildren().add(new Text(sentence.getSentence().toString()));

            progressBar.setProgress(1);
        });
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
            settingsStage.setScene(new Scene(settingsRoot));

            // Passa lo Stage al controller
            controller.setStage(settingsStage);

            // Mostra la finestra
            settingsStage.showAndWait();
            // Dopo la chiusura delle impostazioni, aggiorna il tema
            String newTheme = ConfigManager.getInstance().getProperty("ui.theme", "light");
            updateTheme(newTheme); // Aggiorna il tema selezionato
        } catch (IOException e) {

            // Puoi mostrare un alert per segnalare l'errore all'utente
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Unable to open Settings");
            alert.setContentText("An error occurred while loading the Settings window.");
            alert.showAndWait();
        }
    }

    private void updateTextFlowColors(TextFlow textFlow) {
        String textColor = currentTheme.equals("dark") ? "white" : "black";
//        for (javafx.scene.Node node : textFlow.getChildren()) {
//            if (node instanceof Text) {
//                ((Text) node).setFill(javafx.scene.paint.Color.web(textColor)); // Applica il colore in base al tema
//            }
//        }


        for (Node node : textFlow.getChildren()) {
            if (node instanceof Text textNode) {
                textNode.setFill(Color.web(textColor)); // Cambia colore a rosso
            }
        }

    }
}
