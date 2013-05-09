package scotty.database.parser;

/**
 *
 */
public interface Retrieval<T> {
    T get(String label);
    String attr(String label);
}
