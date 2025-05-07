package unipd.edids;

import java.util.ArrayList;
import java.util.List;

public abstract class Word {
    // shared attributes across all word types
    protected String text;
    public static String input = "";
    // Constructor
    public Word(String value) {
        this.text = value;
        setAttributes();
    }

    // getter for text
    public String getText() {
        return this.text;
    }

    // setter for text
    public void setText(String value) {
        this.text = value;
        setAttributes();
    }

    // abstract method implemented by subclasses
    protected abstract void setAttributes();

    // placeholder for vocabulary check
    public abstract boolean isInVocabulary();
}
