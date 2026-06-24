package org.example;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;

public class VocabularyViewModel {
    @FXML
    private TextField firstLanguageInput;

    @FXML
    private TextField secondLanguageInput;

    @FXML
    private TextField firstWordInput;

    @FXML
    private TextField secondWordInput;

    @FXML
    private TextField deleteInput;

    @FXML
    private TextField filePathInput;

    @FXML
    private TableView<Vocabulary> vocabularyTable;

    @FXML
    private TableColumn<Vocabulary, String> firstWordColumn;

    @FXML
    private TableColumn<Vocabulary, String> secondWordColumn;

    @FXML
    private Label questionLabel;

    @FXML
    private Label directionLabel;

    @FXML
    private TextField answerInput;

    @FXML
    private Label dictionaryFeedbackLabel;

    @FXML
    private Label trainerFeedbackLabel;

    private final VocabularyModel model = new VocabularyModel();
    private final ObservableList<Vocabulary> observableVocabularyList = FXCollections.observableArrayList();
    private Vocabulary currentVocabulary;
    private boolean firstToSecond = true;

    @FXML
    public void initialize() {
        firstWordColumn.setCellValueFactory(new PropertyValueFactory<>("firstWord"));
        secondWordColumn.setCellValueFactory(new PropertyValueFactory<>("secondWord"));
        vocabularyTable.setItems(observableVocabularyList);
        firstLanguageInput.setText("Deutsch");
        secondLanguageInput.setText("Englisch");
        handleCreateFileName();
        updateLanguageNames();
    }

    @FXML
    public void handleCreateFileName() {
        try {
            filePathInput.setText(model.createFileName(firstLanguageInput.getText(), secondLanguageInput.getText()));
            updateLanguageNames();
            showDictionaryMessage("Dateiname wurde erstellt.", Color.BLACK);
        } catch (VocabularyException exception) {
            showDictionaryMessage("Fehler: " + exception.getMessage(), Color.RED);
        }
    }

    @FXML
    public void handleAddVocabulary() {
        try {
            model.addVocabulary(firstWordInput.getText(), secondWordInput.getText());
            updateTable();
            firstWordInput.clear();
            secondWordInput.clear();
            showDictionaryMessage("Vokabel wurde hinzugefügt.", Color.BLACK);
        } catch (VocabularyException exception) {
            showDictionaryMessage("Fehler: " + exception.getMessage(), Color.RED);
        }
    }

    @FXML
    public void handleDeleteVocabulary() {
        try {
            model.deleteVocabulary(deleteInput.getText());
            updateTable();
            deleteInput.clear();
            showDictionaryMessage("Vokabel wurde gelöscht.", Color.BLACK);
        } catch (VocabularyException exception) {
            showDictionaryMessage("Fehler: " + exception.getMessage(), Color.RED);
        }
    }

    @FXML
    public void handleNextVocabulary() {
        currentVocabulary = model.nextVocabulary();

        if (currentVocabulary == null) {
            questionLabel.setText("Keine Vokabeln vorhanden");
            showTrainerMessage("Füge zuerst eine Vokabel hinzu.", Color.RED);
            return;
        }

        questionLabel.setText(model.getQuestion(currentVocabulary, firstToSecond));
        answerInput.clear();
        trainerFeedbackLabel.setText("");
    }

    @FXML
    public void handleCheckAnswer() {
        if (currentVocabulary == null) {
            showTrainerMessage("Starte zuerst eine Vokabel.", Color.RED);
            return;
        }

        if (model.isCorrectAnswer(currentVocabulary, answerInput.getText(), firstToSecond)) {
            showTrainerMessage("Richtig!", Color.GREEN);
        } else {
            showTrainerMessage("Falsch. Richtig ist: " + model.getCorrectAnswer(currentVocabulary, firstToSecond), Color.RED);
        }
    }

    @FXML
    public void handleChangeDirection() {
        firstToSecond = !firstToSecond;
        updateDirectionLabel();
        handleNextVocabulary();
    }

    @FXML
    public void handleLoadVocabulary() {
        try {
            model.load(filePathInput.getText());
            updateLanguagesFromFileName();
            updateLanguageNames();
            updateTable();
            showDictionaryMessage("Wörterbuch wurde geladen.", Color.BLACK);
        } catch (VocabularyFileException exception) {
            showDictionaryMessage("Fehler beim Laden: " + exception.getMessage(), Color.RED);
        }
    }

    @FXML
    public void handleSaveVocabulary() {
        try {
            updateLanguageNames();
            model.save(filePathInput.getText());
            showDictionaryMessage("Wörterbuch wurde gespeichert.", Color.BLACK);
        } catch (VocabularyFileException exception) {
            showDictionaryMessage("Fehler beim Speichern: " + exception.getMessage(), Color.RED);
        }
    }

    private void updateTable() {
        observableVocabularyList.setAll(model.getVocabularyList());
    }

    private void updateLanguageNames() {
        firstWordColumn.setText(firstLanguageInput.getText().trim());
        secondWordColumn.setText(secondLanguageInput.getText().trim());
        updateDirectionLabel();
    }

    private void updateLanguagesFromFileName() {
        String fileName = filePathInput.getText().trim();
        int lastSlash = Math.max(fileName.lastIndexOf("/"), fileName.lastIndexOf("\\"));

        if (lastSlash >= 0) {
            fileName = fileName.substring(lastSlash + 1);
        }

        if (fileName.endsWith(".txt")) {
            fileName = fileName.substring(0, fileName.length() - 4);
        }

        String[] parts = fileName.split("_");

        if (parts.length >= 3 && isVocabularyFileName(parts[parts.length - 1])) {
            firstLanguageInput.setText(parts[0]);
            secondLanguageInput.setText(parts[1]);
        }
    }

    private boolean isVocabularyFileName(String text) {
        return text.equalsIgnoreCase("vokabeln") || text.equalsIgnoreCase("vokablen");
    }

    private void updateDirectionLabel() {
        if (firstToSecond) {
            directionLabel.setText(firstLanguageInput.getText().trim() + " -> " + secondLanguageInput.getText().trim());
        } else {
            directionLabel.setText(secondLanguageInput.getText().trim() + " -> " + firstLanguageInput.getText().trim());
        }
    }

    private void showDictionaryMessage(String message, Color color) {
        dictionaryFeedbackLabel.setText(message);
        dictionaryFeedbackLabel.setTextFill(color);
    }

    private void showTrainerMessage(String message, Color color) {
        trainerFeedbackLabel.setText(message);
        trainerFeedbackLabel.setTextFill(color);
    }
}
