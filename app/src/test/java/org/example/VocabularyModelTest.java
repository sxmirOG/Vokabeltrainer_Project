package org.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class VocabularyModelTest {
    @TempDir
    Path tempDir;

    private VocabularyModel model;

    @BeforeEach
    void setUp() {
        model = new VocabularyModel();
    }

    @Test
    void testAddVocabularyStoresVocabularyInList() throws Exception {
        model.addVocabulary("Haus", "house");

        assertEquals(1, model.getVocabularyList().size());
        assertEquals("Haus", model.getVocabularyList().get(0).getFirstWord());
        assertEquals("house", model.getVocabularyList().get(0).getSecondWord());
    }

    @Test
    void testAddVocabularyRejectsEmptyGermanWord() {
        assertThrows(VocabularyException.class, () -> model.addVocabulary("", "house"));
    }

    @Test
    void testAddVocabularyRejectsEmptyTranslation() {
        assertThrows(VocabularyException.class, () -> model.addVocabulary("Haus", ""));
    }

    @Test
    void testAddVocabularyRejectsDuplicateGermanWord() throws Exception {
        model.addVocabulary("Haus", "house");

        assertThrows(VocabularyException.class, () -> model.addVocabulary("haus", "building"));
    }

    @Test
    void testAddVocabularyRejectsDuplicateEnglishWord() throws Exception {
        model.addVocabulary("Haus", "house");

        assertThrows(VocabularyException.class, () -> model.addVocabulary("Gebaeude", "HOUSE"));
    }

    @Test
    void testDeleteVocabularyRemovesByGermanWord() throws Exception {
        model.addVocabulary("Haus", "house");

        model.deleteVocabulary("Haus");

        assertEquals(0, model.getVocabularyList().size());
    }

    @Test
    void testDeleteVocabularyRemovesByEnglishWord() throws Exception {
        model.addVocabulary("Haus", "house");

        model.deleteVocabulary("house");

        assertEquals(0, model.getVocabularyList().size());
    }

    @Test
    void testDeleteVocabularyRejectsUnknownWord() {
        assertThrows(VocabularyException.class, () -> model.deleteVocabulary("Baum"));
    }

    @Test
    void testNextVocabularyReturnsNullWhenListIsEmpty() {
        assertNull(model.nextVocabulary());
    }

    @Test
    void testNextVocabularyReturnsVocabularyWhenListHasEntry() throws Exception {
        model.addVocabulary("Haus", "house");

        assertNotNull(model.nextVocabulary());
    }

    @Test
    void testCheckAnswerUsesGermanToEnglishDirection() {
        Vocabulary vocabulary = new Vocabulary("Haus", "house");

        assertTrue(model.isCorrectAnswer(vocabulary, "HOUSE", true));
        assertFalse(model.isCorrectAnswer(vocabulary, "Haus", true));
    }

    @Test
    void testCheckAnswerUsesEnglishToGermanDirection() {
        Vocabulary vocabulary = new Vocabulary("Haus", "house");

        assertTrue(model.isCorrectAnswer(vocabulary, "haus", false));
        assertFalse(model.isCorrectAnswer(vocabulary, "house", false));
    }

    @Test
    void testPointsStartAtZero() {
        assertEquals(0, model.getPoints());
    }

    @Test
    void testAddPointIncreasesPoints() {
        model.addPoint();

        assertEquals(1, model.getPoints());
    }

    @Test
    void testRemovePointDecreasesPoints() {
        model.addPoint();
        model.addPoint();

        model.removePoint();

        assertEquals(1, model.getPoints());
    }

    @Test
    void testRemovePointDoesNotGoBelowZero() {
        model.removePoint();

        assertEquals(0, model.getPoints());
    }

    @Test
    void testResetPointsSetsPointsToZero() {
        model.addPoint();
        model.addPoint();

        model.resetPoints();

        assertEquals(0, model.getPoints());
    }

    @Test
    void testGetQuestionUsesGermanToEnglishDirection() {
        Vocabulary vocabulary = new Vocabulary("Haus", "house");

        assertEquals("Haus", model.getQuestion(vocabulary, true));
    }

    @Test
    void testGetQuestionUsesEnglishToGermanDirection() {
        Vocabulary vocabulary = new Vocabulary("Haus", "house");

        assertEquals("house", model.getQuestion(vocabulary, false));
    }

    @Test
    void testGetCorrectAnswerUsesGermanToEnglishDirection() {
        Vocabulary vocabulary = new Vocabulary("Haus", "house");

        assertEquals("house", model.getCorrectAnswer(vocabulary, true));
    }

    @Test
    void testGetCorrectAnswerUsesEnglishToGermanDirection() {
        Vocabulary vocabulary = new Vocabulary("Haus", "house");

        assertEquals("Haus", model.getCorrectAnswer(vocabulary, false));
    }

    @Test
    void testSaveAndLoadVocabularyUsesGivenFilePath() throws Exception {
        File file = tempDir.resolve("dictionary.txt").toFile();
        VocabularyModel loadedModel = new VocabularyModel();

        model.addVocabulary("Haus", "house");
        model.addVocabulary("Baum", "tree");
        model.save(file.getAbsolutePath());
        loadedModel.load(file.getAbsolutePath());

        assertEquals(2, loadedModel.getVocabularyList().size());
        assertEquals("Haus", loadedModel.getVocabularyList().get(0).getFirstWord());
        assertEquals("tree", loadedModel.getVocabularyList().get(1).getSecondWord());
    }

    @Test
    void testLoadRejectsMissingFile() {
        File file = tempDir.resolve("missing.txt").toFile();

        assertThrows(VocabularyFileException.class, () -> model.load(file.getAbsolutePath()));
    }

    @Test
    void testSaveRejectsFolderAsFilePath() throws Exception {
        File folder = tempDir.resolve("folder").toFile();
        folder.mkdir();

        model.addVocabulary("Haus", "house");

        assertThrows(VocabularyFileException.class, () -> model.save(folder.getAbsolutePath()));
    }

    @Test
    void testCreateFileNameUsesSelectedLanguages() throws Exception {
        assertEquals("Deutsch_Englisch_vokabeln.txt", model.createFileName("Deutsch", "Englisch"));
    }

    @Test
    void testCreateFileNameReplacesSpaces() throws Exception {
        assertEquals("Deutsch_Tuerkisch_Deutsch_vokabeln.txt", model.createFileName("Deutsch", "Tuerkisch Deutsch"));
    }

    @Test
    void testCreateFileNameRejectsMissingLanguage() {
        assertThrows(VocabularyException.class, () -> model.createFileName("Deutsch", ""));
    }
}
