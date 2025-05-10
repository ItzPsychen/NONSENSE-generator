package unipd.edids;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class FormController {

    private AppManager appManager;
    public FormController() {
        appManager = new AppManager();
    }

    @FXML
    private TextField inputText;

    public void analyzeClick() {
        appManager.analyzeSentence(inputText.getText());
    }
}
