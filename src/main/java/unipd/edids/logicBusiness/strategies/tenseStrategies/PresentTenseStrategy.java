package unipd.edids.logicBusiness.strategies.tenseStrategies;

public class PresentTenseStrategy implements TenseStrategy {
    @Override
    public String conjugate(String verb) {
        return verb;
    }
}
