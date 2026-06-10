package org.example;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class VocabularyModel {
    private final ArrayList<Vocabulary> vocabularyList = new ArrayList<>();
    private final Random random = new Random();

    public VocabularyModel() {
    }

    public VocabularyModel(File file) {
    }

    public ArrayList<Vocabulary> getVocabularyList() {
        return vocabularyList;
    }

    public void load(String path) throws IOException {
        vocabularyList.clear();
        File file = new File(path);

        if (!file.exists()) {
            throw new IOException("Datei wurde nicht gefunden.");
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();

            while (line != null) {
                String[] parts = line.split(";", 2);

                if (parts.length == 2) {
                    vocabularyList.add(new Vocabulary(parts[0], parts[1]));
                }

                line = reader.readLine();
            }
        }
    }

    public void save(String path) throws IOException {
        File file = new File(path);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (Vocabulary vocabulary : vocabularyList) {
                writer.write(vocabulary.toFileLine());
                writer.newLine();
            }
        }
    }

    public void addVocabulary(String german, String translation) {
        if (german.isBlank() || translation.isBlank()) {
            throw new IllegalArgumentException("Beide Felder muessen ausgefuellt sein.");
        }

        if (containsWord(german) || containsWord(translation)) {
            throw new IllegalArgumentException("Dieses Wort ist bereits im Woerterbuch.");
        }

        vocabularyList.add(new Vocabulary(german.trim(), translation.trim()));
    }

    public void deleteVocabulary(String word) {
        if (word.isBlank()) {
            throw new IllegalArgumentException("Bitte ein deutsches oder englisches Wort eingeben.");
        }

        Vocabulary vocabulary = findVocabulary(word);

        if (vocabulary == null) {
            throw new IllegalArgumentException("Das Wort kommt nicht im Woerterbuch vor.");
        }

        vocabularyList.remove(vocabulary);
    }

    public Vocabulary nextVocabulary() {
        if (vocabularyList.isEmpty()) {
            return null;
        }

        int index = random.nextInt(vocabularyList.size());
        return vocabularyList.get(index);
    }

    public boolean isCorrectAnswer(Vocabulary vocabulary, String answer, boolean germanToEnglish) {
        if (vocabulary == null || answer == null) {
            return false;
        }

        if (germanToEnglish) {
            return vocabulary.getTranslation().equalsIgnoreCase(answer.trim());
        }

        return vocabulary.getGerman().equalsIgnoreCase(answer.trim());
    }

    public String getQuestion(Vocabulary vocabulary, boolean germanToEnglish) {
        if (vocabulary == null) {
            return "";
        }

        if (germanToEnglish) {
            return vocabulary.getGerman();
        }

        return vocabulary.getTranslation();
    }

    public String getCorrectAnswer(Vocabulary vocabulary, boolean germanToEnglish) {
        if (vocabulary == null) {
            return "";
        }

        if (germanToEnglish) {
            return vocabulary.getTranslation();
        }

        return vocabulary.getGerman();
    }

    private boolean containsWord(String word) {
        return findVocabulary(word) != null;
    }

    private Vocabulary findVocabulary(String word) {
        String searchedWord = word.trim();

        for (Vocabulary vocabulary : vocabularyList) {
            if (vocabulary.getGerman().equalsIgnoreCase(searchedWord)) {
                return vocabulary;
            }

            if (vocabulary.getTranslation().equalsIgnoreCase(searchedWord)) {
                return vocabulary;
            }
        }

        return null;
    }
}
