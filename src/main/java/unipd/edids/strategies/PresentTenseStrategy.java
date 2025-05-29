package unipd.edids.strategies;

public class PresentTenseStrategy implements TenseStrategy {
    @Override
    public String conjugate(String verb) {
        return verb;
    }
}
