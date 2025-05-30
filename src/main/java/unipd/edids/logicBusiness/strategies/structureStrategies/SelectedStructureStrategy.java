package unipd.edids.logicBusiness.strategies.structureStrategies;

public class SelectedStructureStrategy implements StructureSentenceStrategy{
    private final String selectedStructure; // Campo per memorizzare il parametro esterno

    public SelectedStructureStrategy(String selectedStructure) {
        this.selectedStructure = selectedStructure; // Inizializzazione del valore
    }
    @Override
    public StringBuilder generateSentence() {
        return new StringBuilder(selectedStructure);
    }
}
