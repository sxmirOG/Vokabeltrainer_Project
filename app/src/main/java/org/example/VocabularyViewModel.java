package org.example;

import java.io.IOException;
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
    private TextField germanInput;

    @FXML
    private TextField translationInput;

    @FXML
    private TextField deleteInput;

    @FXML
    private TextField filePathInput;

    @FXML
    private TableView<Vocabulary> vocabularyTable;

    @FXML
    private TableColumn<Vocabulary, String> germanColumn;

    @FXML
    private TableColumn<Vocabulary, String> translationColumn;

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
    private boolean germanToEnglish = true;

    @FXML
    public void initialize() {
        germanColumn.setCellValueFactory(new PropertyValueFactory<>("german"));
        translationColumn.setCellValueFactory(new PropertyValueFactory<>("translation"));
        vocabularyTable.setItems(observableVocabularyList);
        filePathInput.setText("vocabulary.txt");
        updateDirectionLabel();
    }

    @FXML
    public void handleAddVocabulary() {
        try {
            model.addVocabulary(germanInput.getText(), translationInput.getText());
            updateTable();
            germanInput.clear();
            translationInput.clear();
            showDictionaryMessage("Vokabel wurde hinzugefuegt.", Color.BLACK);
        } catch (IllegalArgumentException exception) {
            showDictionaryMessage("Fehler: " + exception.getMessage(), Color.RED);
        }
    }

    @FXML
    public void handleDeleteVocabulary() {
        try {
            model.deleteVocabulary(deleteInput.getText());
            updateTable();
            deleteInput.clear();
            showDictionaryMessage("Vokabel wurde geloescht.", Color.BLACK);
        } catch (IllegalArgumentException exception) {
            showDictionaryMessage("Fehler: " + exception.getMessage(), Color.RED);
        }
    }

    @FXML
    public void handleNextVocabulary() {
        currentVocabulary = model.nextVocabulary();

        if (currentVocabulary == null) {
            questionLabel.setText("Keine Vokabeln vorhanden");
            showTrainerMessage("Fuege zuerst eine Vokabel hinzu.", Color.RED);
            return;
        }

        questionLabel.setText(model.getQuestion(currentVocabulary, germanToEnglish));
        answerInput.clear();
        trainerFeedbackLabel.setText("");
    }

    @FXML
    public void handleCheckAnswer() {
        if (currentVocabulary == null) {
            showTrainerMessage("Starte zuerst eine Vokabel.", Color.RED);
            return;
        }

        if (model.isCorrectAnswer(currentVocabulary, answerInput.getText(), germanToEnglish)) {
            showTrainerMessage("Richtig!", Color.GREEN);
        } else {
            showTrainerMessage("Falsch. Richtig ist: " + model.getCorrectAnswer(currentVocabulary, germanToEnglish), Color.RED);
        }
    }

    @FXML
    public void handleChangeDirection() {
        germanToEnglish = !germanToEnglish;
        updateDirectionLabel();
        handleNextVocabulary();
    }

    @FXML
    public void handleLoadVocabulary() {
        try {
            model.load(filePathInput.getText());
            updateTable();
            showDictionaryMessage("Woerterbuch wurde geladen.", Color.BLACK);
        } catch (IOException exception) {
            showDictionaryMessage("Fehler beim Laden: " + exception.getMessage(), Color.RED);
        }
    }

    @FXML
    public void handleSaveVocabulary() {
        try {
            model.save(filePathInput.getText());
            showDictionaryMessage("Woerterbuch wurde gespeichert.", Color.BLACK);
        } catch (IOException exception) {
            showDictionaryMessage("Fehler beim Speichern: " + exception.getMessage(), Color.RED);
        }
    }

    private void updateTable() {
        observableVocabularyList.setAll(model.getVocabularyList());
    }

    private void updateDirectionLabel() {
        if (germanToEnglish) {
            directionLabel.setText("Deutsch -> Englisch");
        } else {
            directionLabel.setText("Englisch -> Deutsch");
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
