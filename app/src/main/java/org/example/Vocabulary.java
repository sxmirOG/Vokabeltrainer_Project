package org.example;

public class Vocabulary {
    private final String firstWord;
    private final String secondWord;

    public Vocabulary(String firstWord, String secondWord) {
        this.firstWord = firstWord;
        this.secondWord = secondWord;
    }

    public String getFirstWord() {
        return firstWord;
    }

    public String getSecondWord() {
        return secondWord;
    }

    public String toFileLine() {
        return firstWord + ";" + secondWord;
    }
}
