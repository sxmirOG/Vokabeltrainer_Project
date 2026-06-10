package org.example;

public class Vocabulary {
    private String german;
    private String translation;

    public Vocabulary(String german, String translation) {
        this.german = german;
        this.translation = translation;
    }

    public String getGerman() {
        return german;
    }

    public String getTranslation() {
        return translation;
    }

    public String toFileLine() {
        return german + ";" + translation;
    }
}
