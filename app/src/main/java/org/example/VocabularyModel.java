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
    private final ArrayList<Vocabulary> trainingVocabularyList = new ArrayList<>();
    private final Random random = new Random();
    private int points = 0;
    private int correctAnswers = 0;
    private int answeredVocabularyCount = 0;
    private int trainingVocabularyCount = 0;
    private boolean trainingStarted = false;

    public ArrayList<Vocabulary> getVocabularyList() {
        return vocabularyList;
    }

    public void load(String path) throws VocabularyFileException {
        vocabularyList.clear();
        startTrainingRound();
        File file = new File(path);

        if (!file.exists()) {
            throw new VocabularyFileException("Datei wurde nicht gefunden.");
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

            startTrainingRound();
        } catch (IOException exception) {
            throw new VocabularyFileException("Datei konnte nicht geladen werden.");
        }
    }

    public void save(String path) throws VocabularyFileException {
        File file = new File(path);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (Vocabulary vocabulary : vocabularyList) {
                writer.write(vocabulary.toFileLine());
                writer.newLine();
            }
        } catch (IOException exception) {
            throw new VocabularyFileException("Datei konnte nicht gespeichert werden.");
        }
    }

    public void addVocabulary(String firstWord, String secondWord) throws VocabularyException {
        if (firstWord.isBlank() || secondWord.isBlank()) {
            throw new VocabularyException("Beide Felder müssen ausgefüllt sein.");
        }

        if (containsWord(firstWord) || containsWord(secondWord)) {
            throw new VocabularyException("Dieses Wort ist bereits im Wörterbuch.");
        }

        vocabularyList.add(new Vocabulary(firstWord.trim(), secondWord.trim()));
        startTrainingRound();
    }

    public void deleteVocabulary(String word) throws VocabularyException {
        if (word.isBlank()) {
            throw new VocabularyException("Bitte ein Wort eingeben.");
        }

        Vocabulary vocabulary = findVocabulary(word);

        if (vocabulary == null) {
            throw new VocabularyException("Das Wort kommt nicht im Wörterbuch vor.");
        }

        vocabularyList.remove(vocabulary);
        startTrainingRound();
    }

    public Vocabulary nextVocabulary() {
        if (vocabularyList.isEmpty()) {
            return null;
        }

        int index = random.nextInt(vocabularyList.size());
        return vocabularyList.get(index);
    }

    public void startTrainingRound() {
        trainingVocabularyList.clear();
        trainingVocabularyList.addAll(vocabularyList);
        trainingVocabularyCount = trainingVocabularyList.size();
        correctAnswers = 0;
        answeredVocabularyCount = 0;
        trainingStarted = true;
        resetPoints();
    }

    public Vocabulary nextTrainingVocabulary() {
        if (!trainingStarted) {
            startTrainingRound();
        }

        if (trainingVocabularyList.isEmpty()) {
            return null;
        }

        int index = random.nextInt(trainingVocabularyList.size());
        return trainingVocabularyList.get(index);
    }

    public boolean checkTrainingAnswer(Vocabulary vocabulary, String answer, boolean firstToSecond) {
        boolean correct = isCorrectAnswer(vocabulary, answer, firstToSecond);

        if (trainingVocabularyList.remove(vocabulary)) {
            answeredVocabularyCount++;

            if (correct) {
                correctAnswers++;
                addPoint();
            }
        }

        return correct;
    }

    public boolean isTrainingFinished() {
        return trainingStarted && trainingVocabularyList.isEmpty();
    }

    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public int getAnsweredVocabularyCount() {
        return answeredVocabularyCount;
    }

    public int getTrainingVocabularyCount() {
        return trainingVocabularyCount;
    }

    public boolean isCorrectAnswer(Vocabulary vocabulary, String answer, boolean firstToSecond) {
        if (vocabulary == null || answer == null) {
            return false;
        }

        if (firstToSecond) {
            return vocabulary.getSecondWord().equalsIgnoreCase(answer.trim());
        }

        return vocabulary.getFirstWord().equalsIgnoreCase(answer.trim());
    }

    public int getPoints() {
        return points;
    }

    public void addPoint() {
        points++;
    }

    public void removePoint() {
        if (points > 0) {
            points--;
        }
    }

    public void resetPoints() {
        points = 0;
    }

    public String getQuestion(Vocabulary vocabulary, boolean firstToSecond) {
        if (vocabulary == null) {
            return "";
        }

        if (firstToSecond) {
            return vocabulary.getFirstWord();
        }

        return vocabulary.getSecondWord();
    }

    public String getCorrectAnswer(Vocabulary vocabulary, boolean firstToSecond) {
        if (vocabulary == null) {
            return "";
        }

        if (firstToSecond) {
            return vocabulary.getSecondWord();
        }

        return vocabulary.getFirstWord();
    }

    public String createFileName(String firstLanguage, String secondLanguage) throws VocabularyException {
        if (firstLanguage.isBlank() || secondLanguage.isBlank()) {
            throw new VocabularyException("Beide Sprachen müssen ausgefüllt sein.");
        }

        return cleanLanguageName(firstLanguage) + "_" + cleanLanguageName(secondLanguage) + "_vokabeln.txt";
    }

    private boolean containsWord(String word) {
        return findVocabulary(word) != null;
    }

    private Vocabulary findVocabulary(String word) {
        String searchedWord = word.trim();

        for (Vocabulary vocabulary : vocabularyList) {
            if (vocabulary.getFirstWord().equalsIgnoreCase(searchedWord)) {
                return vocabulary;
            }

            if (vocabulary.getSecondWord().equalsIgnoreCase(searchedWord)) {
                return vocabulary;
            }
        }

        return null;
    }

    private String cleanLanguageName(String language) {
        return language.trim().replace(" ", "_");
    }
}
