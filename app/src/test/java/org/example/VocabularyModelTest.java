package org.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class VocabularyModelTest {
    @TempDir
    Path tempDir;

    @Test
    void addVocabularyStoresVocabularyInList() throws Exception {
        VocabularyModel model = new VocabularyModel();

        model.addVocabulary("Haus", "house");

        assertEquals(1, model.getVocabularyList().size());
        assertEquals("Haus", model.getVocabularyList().get(0).getGerman());
        assertEquals("house", model.getVocabularyList().get(0).getTranslation());
    }

    @Test
    void addVocabularyRejectsDuplicateGermanWord() throws Exception {
        VocabularyModel model = new VocabularyModel();

        model.addVocabulary("Haus", "house");

        assertThrows(IllegalArgumentException.class, () -> model.addVocabulary("haus", "building"));
    }

    @Test
    void addVocabularyRejectsDuplicateEnglishWord() throws Exception {
        VocabularyModel model = new VocabularyModel();

        model.addVocabulary("Haus", "house");

        assertThrows(IllegalArgumentException.class, () -> model.addVocabulary("Gebaeude", "HOUSE"));
    }

    @Test
    void deleteVocabularyRemovesByGermanWord() throws Exception {
        VocabularyModel model = new VocabularyModel();
        model.addVocabulary("Haus", "house");

        model.deleteVocabulary("Haus");

        assertEquals(0, model.getVocabularyList().size());
    }

    @Test
    void deleteVocabularyRemovesByEnglishWord() throws Exception {
        VocabularyModel model = new VocabularyModel();
        model.addVocabulary("Haus", "house");

        model.deleteVocabulary("house");

        assertEquals(0, model.getVocabularyList().size());
    }

    @Test
    void deleteVocabularyRejectsUnknownWord() {
        VocabularyModel model = new VocabularyModel();

        assertThrows(IllegalArgumentException.class, () -> model.deleteVocabulary("Baum"));
    }

    @Test
    void checkAnswerUsesSelectedDirection() {
        VocabularyModel model = new VocabularyModel();
        Vocabulary vocabulary = new Vocabulary("Haus", "house");

        assertTrue(model.isCorrectAnswer(vocabulary, "HOUSE", true));
        assertTrue(model.isCorrectAnswer(vocabulary, "haus", false));
        assertFalse(model.isCorrectAnswer(vocabulary, "tree", true));
    }

    @Test
    void saveAndLoadVocabularyUsesGivenFilePath() throws Exception {
        File file = tempDir.resolve("dictionary.txt").toFile();
        VocabularyModel savedModel = new VocabularyModel();
        VocabularyModel loadedModel = new VocabularyModel();

        savedModel.addVocabulary("Haus", "house");
        savedModel.addVocabulary("Baum", "tree");
        savedModel.save(file.getAbsolutePath());
        loadedModel.load(file.getAbsolutePath());

        assertEquals(2, loadedModel.getVocabularyList().size());
        assertEquals("Haus", loadedModel.getVocabularyList().get(0).getGerman());
        assertEquals("tree", loadedModel.getVocabularyList().get(1).getTranslation());
    }

    @Test
    void loadRejectsMissingFile() {
        VocabularyModel model = new VocabularyModel();
        File file = tempDir.resolve("missing.txt").toFile();

        assertThrows(IOException.class, () -> model.load(file.getAbsolutePath()));
    }
}
