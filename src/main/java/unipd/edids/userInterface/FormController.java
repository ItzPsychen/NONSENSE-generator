package unipd.edids.userInterface;

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
import unipd.edids.logicBusiness.AppManager;
import unipd.edids.logicBusiness.entities.Sentence;
import unipd.edids.logicBusiness.entities.SentenceStructure;
import unipd.edids.logicBusiness.managers.ConfigManager;
import unipd.edids.logicBusiness.managers.LoggerManager;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Timer;
import java.util.stream.Collectors;

/**
 * The `FormController` class is responsible for managing the application's user interface and coordinating interactions
 * between the backend logic and frontend components.
 *
 * <p>Responsibilities:
 * - Acts as the Controller in the Model-View-Controller (MVC) architecture, connecting the View (UI) with the backend services.
 * - Manages application states and user interactions such as text input, analysis, generation, and settings updates.
 * - Provides utilities for manipulating UI components, including syntax highlighting, structure formatting,
 *   and progress bar animations.
 * - Handles logic for displaying and interacting with different application modes, such as text analysis and text generation.
 *
 * <p>Design Pattern:
 * - Implements the **Facade Pattern** by abstracting and managing interactions with complex backend systems,
 *   primarily through the `AppManager`.
 * - Utilizes the **Command Pattern** for encapsulating operations like text analysis and sentence generation into discrete tasks.
 */
public class FormController {
    private static final Logger logger = LoggerManager.getInstance().getLogger(FormController.class);

    private static String currentTheme = "light"; // Default theme
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
    private TextFlow lastSyntaxFlow; // Field that saves the content of syntaxArea 
    private TextFlow lastGenerateFlow; // Field that saves the content of generateArea
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
    private AppManager appManager;
    private String textColor;
    private Map<String, String> treeTags;
    @FXML
    private AnchorPane bottomBar;
    private Stage primaryStage; // Variable to save primaryStage

    /**
     * Terminates the application by stopping active timers and exiting the application platform.
     *
     * <p>
     * This method cancels and purges the `progressTimer` if it exists to ensure proper resource cleanup.
     * It then exits the JavaFX application via the `Platform.exit()` call, effectively shutting down
     * the entire application.
     */
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

    /**
     * Deletes the current input text, clears the syntax and generation areas, and resets the application state.
     */
    // Delete input (and optionally output)
    @FXML
    private void handleDelete() {
        inputText.clear();
        syntaxArea.getChildren().clear();
        generateArea.getChildren().clear();
        appManager.clearAll();
    }

    /**
     * Responsible for handling the "About" action in the application.
     * It displays an informational alert dialog about the project and its creators.
     */
    // Show About info
    @FXML
    private void handleAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("NONSENSE Generator");
        alert.setContentText("Created by team NoIdeaName\n\n" + "→ Casarotto Milo\n→ Donnagemma Davide\n→ Hu Stefania\n→ Maniglio Federico\n\n" + "2024/2025 Project\nAnalysis and Generation of syntactic nonsense from your Sentences!");

        Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
        alertStage.getIcons().add(new Image(ConfigManager.getInstance().getProperty("icon.about")));
        alert.showAndWait();
    }

    /**
     * Sets the Facade for the FormController by assigning an instance of AppManager.
     *
     * @param appManager an instance of AppManager, acting as the Facade for managing core application logic and services.
     */
    public void setFacade(AppManager appManager) {
        this.appManager = appManager;
    }

    /**
     * Initializes the user interface components and sets up the primary configurations
     * for the application.
     *
     * This method is responsible for:
     * - Applying the configured UI theme.
     * - Setting up toggle groups for radio buttons.
     * - Populating the structure combo box with sentence structure options and initializing
     *   its state.
     * - Loading syntax tags used in the application.
     * - Adding context menus to specific areas in the interface.
     * - Binding UI components to respective layout properties for responsive design.
     * - Initializing a timer instance for UI-related progress tracking or updates.
     */
    public void initialize() {
        ConfigManager configManager = ConfigManager.getInstance();

        // Get configured theme
        String theme = configManager.getProperty("ui.theme");
        // Apply current theme
        updateTheme(theme);

        structureToggleGroup = new ToggleGroup();

        // Associate RadioButtons to ToggleGroup
        randomStructureRadio.setToggleGroup(structureToggleGroup);
        sameAsAnalyzeRadio.setToggleGroup(structureToggleGroup);
        selectStructureRadio.setToggleGroup(structureToggleGroup);

        ObservableList<String> sentenceStructuresList = FXCollections.observableArrayList(SentenceStructure.getInstance().getStructures());
        structureComboBox.setItems(sentenceStructuresList);
        structureComboBox.getSelectionModel().selectFirst(); // Set default selection to the first item
        structureComboBox.setDisable(!selectStructureRadio.isSelected());

        this.treeTags = loadSyntaxTags(getFilePathTags());

        // Add ContextMenu to TextFlow syntaxArea
        addContextMenuToTextFlow(syntaxArea);

        // Add ContextMenu to TextFlow generateArea
        addContextMenuToTextFlow(generateArea);

        syntaxArea.prefWidthProperty().bind(scrollPaneSyntax.widthProperty().subtract(20));
        generateArea.prefWidthProperty().bind(scrollPaneGenerate.widthProperty().subtract(20));

        this.progressTimer = new Timer();
    }

    /**
     * Adds a context menu with a "Copy" option to the specified TextFlow.
     * The context menu allows users to copy the content of the TextFlow to the clipboard.
     *
     * @param textFlow The TextFlow component to which the context menu will be added.
     */
    private void addContextMenuToTextFlow(TextFlow textFlow) {
        // Create context menu
        ContextMenu contextMenu = new ContextMenu();

        // "Copy" option
        MenuItem copyItem = new MenuItem("Copy");
        copyItem.setOnAction(event -> copyTextFlowContentToClipboard(textFlow));

        // Add option to menu
        contextMenu.getItems().add(copyItem);

        // Associate context menu to TextFlow
        textFlow.setOnContextMenuRequested(e -> {
            contextMenu.show(textFlow, e.getScreenX(), e.getScreenY());
        });
    }

    /**
     * Copies all text content from a given {@code TextFlow} node to the system clipboard.
     *
     * @param textFlow The {@code TextFlow} node whose content is to be copied.
     */
    private void copyTextFlowContentToClipboard(TextFlow textFlow) {
        // StringBuilder to contain all text
        StringBuilder content = new StringBuilder();

        // Recursive method to get all text
        extractTextFromNodes(textFlow, content);

        // Copy complete text to clipboard
        javafx.scene.input.Clipboard clipboard = javafx.scene.input.Clipboard.getSystemClipboard();
        javafx.scene.input.ClipboardContent clipboardContent = new javafx.scene.input.ClipboardContent();
        clipboardContent.putString(content.toString());
        clipboard.setContent(clipboardContent);
    }

    /**
     * Extracts text content from a given JavaFX Node recursively and appends it to a StringBuilder.
     *
     * @param node     The JavaFX Node from which text is to be extracted.
     *                 It can include Text or TextFlow instances.
     * @param content  The StringBuilder object where the extracted text will be appended.
     */
    private void extractTextFromNodes(javafx.scene.Node node, StringBuilder content) {
        // Check node type
        if (node instanceof Text) {
            // Add text from `Text` object
            content.append(((Text) node).getText());
        } else if (node instanceof TextFlow) {
            // Iterate on children of a nested `TextFlow`
            for (javafx.scene.Node child : ((TextFlow) node).getChildren()) {
                extractTextFromNodes(child, content);
            }
        }
    }

    /**
     * Updates the application's theme and its related visual properties.
     *
     * @param theme The name of the desired theme to apply (e.g., "dark", "light").
     */
    public void updateTheme(String theme) {
        Platform.runLater(() -> {
            currentTheme = theme;
            // Change style sheet
            if (currentTheme.equals("dark")) {
                this.textColor = "white";
                rootPane.getStylesheets().add(Objects.requireNonNull(FormController.class.getResource("/style/dark-theme.css")).toExternalForm());
                bottomBar.setStyle("-fx-background-color: #444444;"); // Light color
            } else {
                this.textColor = "black";
                rootPane.getStylesheets().clear();
                bottomBar.setStyle("-fx-background-color: #E0E0E0;"); // Dark color
            }

            // Update colors in TextFlows
            updateTextFlowColors(syntaxArea);
            updateTextFlowColors(generateArea);

            if (lastSyntaxFlow != null) updateTextFlowColors(lastSyntaxFlow);
            if (lastGenerateFlow != null) updateTextFlowColors(lastGenerateFlow);
        });

    }

    /**
     * Handles the behavior when the "Analyze" button is clicked.
     *
     * 1. Starts a progress indicator to provide visual feedback to the user.
     * 2. Creates and executes a background task to analyze the given sentence with
     *    options determined by the user interface state.
     * 3. Leverages task management mechanisms to handle asynchronous operations gracefully.
     */
    public void analyzeClick() {
        progressBar.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
        logger.info("Analyze button clicked");

        // Create background task
        Task<Sentence> analyzeTask = new Task<>() {
            @Override
            protected Sentence call() {
                // Simulate processing time (500ms for test)
                logger.info("Analyze task started");
                // Call analysis from AppManager
                return appManager.analyzeSentence(inputText.getText(), checkSaveSentence.isSelected());
            }
        };
        TaskManager.execute(analyzeTask, this::handleAnalyzeSuccess);


        // Execute task using TaskManager
    }

    /**
     * Handles the successful completion of the sentence analysis process, updating the UI components accordingly.
     *
     * @param sentence The analyzed sentence containing its structure and syntax tree information.
     */
    private void handleAnalyzeSuccess(Sentence sentence) {
        Platform.runLater(() -> {
            logger.info("Analyze task finished: {}", sentence.getStructure().toString());

            // Create and save new TextFlow
            lastSyntaxFlow = formatStructure(sentence.getStructure().toString() + " \n");
            lastSyntaxFlow.setMaxWidth(syntaxArea.getWidth());


            // Update syntaxArea content
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
            progressTimer.schedule(new java.util.TimerTask() {
                @Override
                public void run() {
                    javafx.application.Platform.runLater(() -> progressBar.setProgress(0));
                }
            }, 1000);
        });
    }

    /**
     * Initiates the process to generate a new sentence based on current configurations.
     *
     * <p>
     * Responsibilities:
     * - Handles the click action of the "Generate" button.
     * - Initiates a background task for sentence generation with configurable input options.
     *
     * <p>
     * Design Pattern:
     * - Follows the **Command Pattern** to encapsulate the sentence generation functionality into a task.
     */
    public void generateClick() {
        progressBar.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
        logger.info("Generate button clicked");

        // Create background task
        Task<Sentence> generateTask = new Task<>() {
            @Override
            protected Sentence call() {
                logger.info("Generation task started");
                // Call analysis from AppManager

                String strategy = structureToggleGroup.getSelectedToggle().getUserData().toString();
                logger.info(strategy);

                // Get value from combobox if SELECTED is selected
                String selectedStructure = "";
                if ("SELECTED".equals(strategy)) {
                    selectedStructure = structureComboBox.getValue(); // Get selected value
                    logger.info("Selected structure: {}", selectedStructure);
                }

                // Generate sentence passing strategy and selected structure value
                return appManager.generateSentence(strategy, selectedStructure, toxicityLevels.isSelected(), futureTenseCheck.isSelected(), newWords.isSelected(), checkSaveSentence.isSelected());
            }
        };

        // Execute task using TaskManager
        TaskManager.execute(generateTask, this::handleGenerateSuccess);
    }

    /**
     * Handles the successful generation of a sentence, updating the UI components and triggering animations for progress bars.
     *
     * @param sentence The generated sentence object containing text and metadata such as toxicity and structure.
     */
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
            // Create and save new TextFlow
            lastGenerateFlow = formatStructure(sentence.getStructure().toString());
            lastGenerateFlow.setMaxWidth(generateArea.getWidth());
            // Update generateArea content
            generateArea.getChildren().clear();
            lastGenerateFlow.prefWidthProperty().bind(generateArea.widthProperty().subtract(20));
            generateArea.getChildren().add(lastGenerateFlow);
            generateArea.getChildren().add(new Text("\n\n"));
            Text newText = new Text(sentence.getSentence().toString());
            newText.setFill(Color.web(this.textColor));
            newText.wrappingWidthProperty().bind(generateArea.widthProperty().subtract(20));
            generateArea.getChildren().add(newText);

            progressBar.setProgress(1);
            progressTimer.schedule(new java.util.TimerTask() {
                @Override
                public void run() {
                    javafx.application.Platform.runLater(() -> progressBar.setProgress(0));
                }
            }, 1000);
        });
    }

    /**
     * Sets the primary stage of the application to manage the main window elements.
     *
     * @param primaryStage The primary stage representing the main application window.
     */
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    /**
     * Formats a given structure string into a styled {@code TextFlow}, applying specific formatting based on identified tokens.
     *
     * @param structure The structure string to be processed and formatted.
     * @return A {@code TextFlow} containing styled {@code Text} nodes based on the structure input.
     */
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

    /**
     * Opens the settings window of the application.
     *
     * <p> This method loads the settings window from an FXML file and displays it in a modal dialog.
     * It initializes the settings stage, sets its properties including title, size, and modality,
     * and passes the stage to the settings controller. If an error occurs during loading, an alert
     * is displayed to notify the user.
     */
    public void openSettings() {
        logger.info("Open settings button clicked");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Settings.fxml"));
            Parent settingsRoot = loader.load();

            // Get instance of associated controller
            SettingsController controller = loader.getController();

            // Create Stage window
            Stage settingsStage = new Stage();
            settingsStage.setTitle("Settings");
            settingsStage.initModality(Modality.WINDOW_MODAL); // Modal windows
            settingsStage.initOwner(primaryStage);
            settingsStage.getIcons().add(new Image(ConfigManager.getInstance().getProperty("icon.settings")));

            // Set scene with root (settingsRoot) and initial dimensions
            Scene scene = new Scene(settingsRoot); // <-- Scene dimensions
            settingsStage.setScene(scene);

            // Optional: Lock min/max dimensions (if needed)
            settingsStage.setMinWidth(800);
            settingsStage.setMinHeight(600);
            // Pass Stage to controller
            controller.setStage(settingsStage);


            // Show window
            settingsStage.showAndWait();
            // After settings close, update theme
            String newTheme = ConfigManager.getInstance().getProperty("ui.theme");
            updateTheme(newTheme); // Update selected theme
        } catch (IOException e) {

            // Show alert to notify user of error
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Unable to open Settings");
            alert.setContentText("An error occurred while loading the Settings window:\n" + e.getMessage());
            alert.showAndWait();
        }
    }

    /**
     * Updates the colors of all Text nodes within the specified TextFlow.
     *
     * @param textFlow the TextFlow object containing Text nodes whose colors will be updated
     */
    private void updateTextFlowColors(TextFlow textFlow) {
        for (Node node : textFlow.getChildren()) {
            if (node instanceof Text) {
                ((Text) node).setFill(Color.web(this.textColor));
            }
        }
    }

    /**
     * Toggles the disable state of the structureComboBox based on the selected state of selectStructureRadio.
     */
    public void radioPressed() {
        structureComboBox.setDisable(!selectStructureRadio.isSelected());
    }

    /**
     * Enables or disables UI components related to toxicity filtering based on the state of the `toxicityLevels` toggle.
     */
    public void toxicityPressed() {
        toxicityBar.setDisable(!toxicityLevels.isSelected());
        profanityBar.setDisable(!toxicityLevels.isSelected());
        insultBar.setDisable(!toxicityLevels.isSelected());
        sexualBar.setDisable(!toxicityLevels.isSelected());
        politicsBar.setDisable(!toxicityLevels.isSelected());
    }

    /**
     * Generates a color string based on the provided value, transitioning from green to red.
     *
     * @param value The input value (range: 0.0 to 1.0) determining the color intensity.
     * @return A CSS color string representing the calculated color in "-fx-accent: rgb(R, G, 0);" format.
     */
    private String getColorForValue(double value) {
        int red = (int) (255 * value);
        int green = (int) (255 * (1 - value));
        return String.format("-fx-accent: rgb(%d, %d, 0);", red, green);
    }


    /**
     * Loads syntax tags from a properties file and maps them to a key-value pair.
     *
     * @param filePath The file path to the properties file containing syntax tags.
     * @return A map containing the syntax tags loaded from the file, or an empty map if an error occurs.
     */
    private Map<String, String> loadSyntaxTags(String filePath) {
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream(Paths.get(filePath).toFile())) {
            properties.load(fis); // Load the .properties file
            // Convert Properties to Map<String, String>
            return properties.entrySet().stream().collect(Collectors.toMap(e -> e.getKey().toString(), e -> e.getValue().toString()));
        } catch (IOException e) {
            System.err.println("Error loading properties file: " + filePath);
            e.printStackTrace();
            return Map.of(); // Return empty Map if error occurs
        }
    }

    /**
     * Retrieves the file path for syntax tags configuration.
     *
     * @return The file path of the "syntax_tags.properties" configuration.
     */
    private String getFilePathTags() {
        return ConfigManager.getInstance().getProperty("syntax_tags.properties");

    }


    /**
     * Generates a formatted visual representation of a tree structure as a string,
     * starting from the root.
     *
     * @param tree The root node of the tree to be formatted.
     * @return A string that represents the tree in a visually readable format.
     */
    private String prettyTree(Tree tree) {
        return "\nTREE\n" + prettyTreeHelper(tree.children()[0], "", true);
    }

    /**
     * Recursively constructs a string representation of a tree structure with appropriate visual indentation.
     *
     * @param tree   The tree node to be processed.
     * @param prefix The prefix string used for indentation and visual alignment of tree levels.
     * @param isLast A boolean flag indicating whether the current tree node is the last child of its parent.
     * @return A string representing the hierarchical structure of the tree.
     */
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

    /**
     * Opens a new window to manage vocabulary input.
     *
     * <p>This method is responsible for:
     * - Loading the Vocabulary interface from the specified FXML file.
     * - Setting up a modal stage and binding it to the primary application stage.
     * - Configuring scene properties, including minimum window dimensions.
     * - Passing the created stage to the associated VocabularyController instance.
     * - Presenting the Vocabulary window to the user in a blocking manner (showAndWait).
     * - Handling exceptions and user notification in case of errors while opening the window.
     */
    public void vocabularyForm() {
        logger.info("Open vocabulary button clicked");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Vocabulary.fxml"));
            Parent vocabularyRoot = loader.load();

            // Get instance of associated controller
            VocabularyController controller = loader.getController();

            // Create Stage window
            Stage vocabularyStage = new Stage();
            vocabularyStage.setTitle("Vocabulary");
            vocabularyStage.initModality(Modality.WINDOW_MODAL); // Modal windows
            vocabularyStage.initOwner(primaryStage);
            vocabularyStage.getIcons().add(new Image(ConfigManager.getInstance().getProperty("icon.vocabulary")));

            // Set scene with root (settingsRoot) and initial dimensions
            Scene scene = new Scene(vocabularyRoot); // <-- Scene dimensions
            vocabularyStage.setScene(scene);

            // Optional: Lock min/max dimensions (if needed)
            vocabularyStage.setMinWidth(450);
            vocabularyStage.setMinHeight(450);

            // Pass Stage to controller
            controller.setStage(vocabularyStage);


            // Show window
            vocabularyStage.showAndWait();
            // After settings close, update theme
        } catch (IOException e) {

            // Show alert to notify user of error
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Unable to open Settings");
            alert.setContentText("An error occurred while loading the Settings window.");
            alert.showAndWait();
        }
    }

    /**
     * Animates a progress bar's progress and updates its color dynamically using specific easing and duration.
     *
     * @param bar the ProgressBar instance that will be animated
     * @param targetValue the desired progress value to animate to, transformed for visual smoothness
     */
    private void animateProgressBar(ProgressBar bar, double targetValue) {
        double transformed = Math.pow(targetValue, 0.5);

        KeyValue kv = new KeyValue(bar.progressProperty(), transformed, Interpolator.EASE_BOTH);
        KeyFrame kf = new KeyFrame(Duration.millis(500), kv);
        Timeline timeline = new Timeline(kf);
        timeline.play();

        // Also change color dynamically
        bar.setStyle(getColorForValue(targetValue));
    }

    /**
     * Triggers an action when the "use generated" button is clicked.
     *
     * Updates the input text field with the generated output sentence,
     * if available from the application manager's output.
     */
    public void useGenerated() {
        logger.info("Use generated button clicked");
        if (appManager.getOutputSentence() != null) {
            inputText.setText(appManager.getOutputSentence().getSentence().toString());
        }
    }
}
