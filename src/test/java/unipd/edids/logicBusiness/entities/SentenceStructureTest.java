package unipd.edids.logicBusiness.entities;

import org.junit.jupiter.api.*;
import unipd.edids.logicBusiness.managers.ConfigManager;
import unipd.edids.logicBusiness.managers.FileManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SentenceStructureTest {

    private File tempFile;

    @BeforeEach
    void setup() throws IOException {
        // Crea un file temporaneo per simulare la lettura delle strutture
        tempFile = File.createTempFile("sentence_structures", ".txt");
        tempFile.deleteOnExit();
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("[NOUN] [VERB]\n");
            writer.write("[ADJECTIVE] [NOUN]\n");
            writer.write("[VERB] [NOUN] [ADJECTIVE]\n");
        }
        // Configura il ConfigManager con il percorso del file temporaneo
        ConfigManager.getInstance().setProperty("sentence.structures", tempFile.getAbsolutePath());
    }

    @AfterEach
    void tearDown() throws IOException {
        if (tempFile.exists()) {
            assertTrue(tempFile.delete(), "Il file temporaneo non è stato eliminato correttamente.");
        }
        ConfigManager.getInstance().resetDefault(ConfigManager.getInstance().getProperty("api.key.file"));
    }

    @Test
    void testSingletonInstance() {
        SentenceStructure instance1 = SentenceStructure.getInstance();
        SentenceStructure instance2 = SentenceStructure.getInstance();

        assertNotNull(instance1, "L'istanza non dovrebbe essere null.");
        assertSame(instance1, instance2, "Le due istanze dovrebbero essere identiche (singletone).");
    }

    @Test
    void testRandomStructureRetrieval() {
        SentenceStructure instance = SentenceStructure.getInstance();
        String randomStructure = instance.getRandomStructure();

        // Controlla che la struttura casuale sia tra quelle caricate
        List<String> expectedStructures = List.of(
                "[NOUN] [VERB]",
                "[ADJECTIVE] [NOUN]",
                "[VERB] [NOUN] [ADJECTIVE]"
        );
        assertNotNull(randomStructure, "La struttura casuale non dovrebbe essere null.");
        assertTrue(expectedStructures.contains(randomStructure),
                "La struttura casuale dovrebbe essere una tra quelle caricate.");
    }

    @Test
    void testDefaultStructureWhenNoFile() throws IOException {
        // Modifica la configurazione con un file vuoto
        File emptyFile = File.createTempFile("empty_structures", ".txt");
        emptyFile.deleteOnExit();

        ConfigManager.getInstance().setProperty("sentence.structures", emptyFile.getAbsolutePath());
        SentenceStructure.getInstance().onConfigChange("sentence.structures", emptyFile.getAbsolutePath());

        String randomStructure = SentenceStructure.getInstance().getRandomStructure();
        assertEquals("[NOUN] [VERB] [NOUN]", randomStructure,
                "Dovrebbe restituire la struttura predefinita quando non ci sono strutture disponibili.");

        assertTrue(emptyFile.delete(), "Il file vuoto non è stato eliminato correttamente.");
    }

    @Test
    void testOnConfigChangeReloadsStructures() throws IOException {
        // Crea un nuovo file di configurazione
        File newTempFile = File.createTempFile("new_sentence_structures", ".txt");
        newTempFile.deleteOnExit();

        try (FileWriter writer = new FileWriter(newTempFile)) {
            writer.write("[ADJECTIVE] [ADJECTIVE] [NOUN]\n");
            writer.write("[VERB] [NOUN]\n");
        }
        ConfigManager.getInstance().setProperty("sentence.structures", newTempFile.getAbsolutePath());

        SentenceStructure instance = SentenceStructure.getInstance();
        instance.onConfigChange("sentence.structures", newTempFile.getAbsolutePath());

        List<String> newStructures = instance.getStructures();
        assertNotNull(newStructures, "Le strutture aggiornate non dovrebbero essere null.");
        assertEquals(2, newStructures.size(), "Dovrebbero essere caricate due nuove strutture.");
        assertTrue(newStructures.contains("[ADJECTIVE] [ADJECTIVE] [NOUN]"),
                "Le nuove strutture dovrebbero includere quella aggiornata.");
        assertTrue(newStructures.contains("[VERB] [NOUN]"),
                "Le nuove strutture dovrebbero includere quella aggiornata.");
    }

    @Test
    void testGetStructuresReturnsLoadedStructures() {
        SentenceStructure instance = SentenceStructure.getInstance();

        List<String> structures = instance.getStructures();
        assertNotNull(structures, "La lista delle strutture non dovrebbe essere null.");
        assertFalse(structures.isEmpty(), "La lista delle strutture non dovrebbe essere vuota.");
        assertTrue(structures.contains("[NOUN] [VERB]"),
                "Le strutture dovrebbero contenere tutte quelle definite nel file.");
    }
}