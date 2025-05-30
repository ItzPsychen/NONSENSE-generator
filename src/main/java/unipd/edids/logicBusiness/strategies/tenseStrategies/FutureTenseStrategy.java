package unipd.edids.logicBusiness.strategies.tenseStrategies;

public class FutureTenseStrategy implements TenseStrategy {
    @Override
    public String conjugate(String verb) {
        if(verb.equals("is") || verb.equals("am") || verb.equals("are"))
            return "will be";
        return "will " + verb;
    }
}
