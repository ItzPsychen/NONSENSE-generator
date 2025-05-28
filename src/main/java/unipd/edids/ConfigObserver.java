package unipd.edids;

public interface ConfigObserver {
    void onConfigChange(String key, String value);
}
