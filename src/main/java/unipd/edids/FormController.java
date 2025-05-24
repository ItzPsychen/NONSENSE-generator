package unipd.edids;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import edu.stanford.nlp.trees.Tree;

import java.nio.file.Paths;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

public class FormController {
    private AppManager appManager;
    private boolean save;

    private Map<String, String> treeTags;

    public FormController() {
        this.appManager = new AppManager();
        this.save = false;
        this.treeTags = loadSyntaxTags(getFilePathTags());
    }

    @FXML
    private TextField inputText;
    @FXML
    private TextFlow syntaxArea;
    @FXML
    private CheckBox checkSyntax;
    @FXML
    private TextFlow generateArea;
    @FXML
    private CheckBox checkSaveSentence;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Button settingsButton;

    @FXML
    public void analyzeClick() {
        progressBar.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);

        // background task
        Task<Sentence> analyzeTask = new Task<Sentence>() {
            @Override
            protected Sentence call() throws Exception {
                // Simulate processing time (remove in production)
                Thread.sleep(500);
                return appManager.analyzeSentence(inputText.getText());
            }
        };

        // Completion handler
        analyzeTask.setOnSucceeded(event -> {
            Sentence analyzeResult = analyzeTask.getValue();

            // isValid() -> not null
            if (analyzeResult == null) {
                Text warningText = new Text("❌ WARNING\n");
                Text errorText = new Text("The sentence uses improper words.");
                warningText.setStyle("-fx-fill: red; -fx-font-size: 16px; -fx-font-weight: bold;");
                errorText.setStyle("-fx-fill: red; -fx-font-size: 12px;");

                syntaxArea.getChildren().add(warningText);
                syntaxArea.getChildren().add(errorText);
                return;
            }

            // Remove the previous content
            syntaxArea.getChildren().clear();

            // Display text (styled)
            Text structureText = new Text(analyzeResult.getStructure().toString() + "\n");
            structureText.setStyle("-fx-font-size: 16px");

            syntaxArea.getChildren().add(structureText);

            // Syntax tree
            if (checkSyntax.isSelected()) {
                String analysis = prettyTree(analyzeResult.getSyntaxTree());
                Text analysisText = new Text(analysis + "\n");
                analysisText.setStyle("-fx-font-family: monospace;");
                syntaxArea.getChildren().add(analysisText);
            }

            // Analysis scores
            syntaxArea.getChildren().addAll(
                    new Text("\nToxicity\t\t\t" + analyzeResult.getToxicity() + "\n"),
                    new Text("Profanity\t\t\t" + analyzeResult.getProfanity() + "\n"),
                    new Text("Insult\t\t\t\t" + analyzeResult.getInsult() + "\n"),
                    new Text("Threat\t\t\t\t" + analyzeResult.getThreat() + "\n"),
                    new Text("Identity Threat\t\t" + analyzeResult.getIdentityThreat() + "\n")
            );

            // Complete progress
            progressBar.setProgress(1);
            new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            javafx.application.Platform.runLater(() -> progressBar.setProgress(0));
                        }
                    },
                    1000
            );
        });

        // Set error handler
        analyzeTask.setOnFailed(event -> {
            progressBar.setProgress(0);
            showErrorDialog("Analysis Error", "An error occurred during analysis",
                    analyzeTask.getException().getMessage());
        });

        // Start the task in a new thread
        new Thread(analyzeTask).start();
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

    @FXML
    public void generateClick() {
        // Start progress (indeterminate mode)
        progressBar.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);

        // Create a background task
        Task<Sentence> generateTask = new Task<Sentence>() {
            @Override
            protected Sentence call() throws Exception {
                // Simulate processing time (remove in production)
                Thread.sleep(500);
                return appManager.generateSentence(checkSaveSentence.isSelected());
            }
        };

        // Set completion handler
        generateTask.setOnSucceeded(event -> {
            Sentence generateResult = generateTask.getValue();

            // Retry if null (recursive call)
            if (generateResult == null) {
                generateClick();
                return;
            }

            // Clear previous content
            generateArea.getChildren().clear();

            // Display results
            String structure = generateResult.getStructure().toString();
            TextFlow structureTextFlow = formatStructure(structure);

            String sentence = generateResult.getSentence().toString();
            Text sentenceText = new Text(sentence);
            sentenceText.setStyle("-fx-font-size: 20px; -fx-underline: true;");

            generateArea.getChildren().addAll(structureTextFlow, new Text("\n\n"), sentenceText);

            // Complete progress
            progressBar.setProgress(1);
            new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            javafx.application.Platform.runLater(() -> progressBar.setProgress(0));
                        }
                    },
                    1000
            );
        });

        // Set error handler
        generateTask.setOnFailed(event -> {
            progressBar.setProgress(0);
            showErrorDialog("Generation Error", "An error occurred during generation",
                    generateTask.getException().getMessage());
        });

        // Start the task in a new thread
        new Thread(generateTask).start();
    }

    @FXML
    private void openSettings() {
        // TODO
    }

    // Metodo helper per formattare la struttura
    private TextFlow formatStructure(String structure) {
        TextFlow textFlow = new TextFlow();
        String[] tokens = structure.split(" "); // Dividiamo la struttura in sezioni basate sugli spazi

        for (String token : tokens) {
            Text text;
            // Verifica delle categorie grammaticali e applica lo stile
            if (token.equals("[noun]") || token.equals("[verb]") || token.equals("[adjective]")) {
                text = new Text(token + " "); // Lascia lo spazio dopo la parola
                text.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            } else {
                text = new Text(token + " ");
                text.setStyle("-fx-font-size: 14px;");
            }

            // Aggiunge ogni parte al TextFlow
            textFlow.getChildren().add(text);
        }

        return textFlow;
    }

    public void showErrorDialog(String title, String headerText, String contentText) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);

        // Per mostrare il dialogo e aspettare che l'utente lo chiuda
        alert.showAndWait();
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
        return ConfigManager.getInstance().getProperty("syntax_tags.properties","./src/main/resources/properties/syntax_tags.properties");
    }
}
