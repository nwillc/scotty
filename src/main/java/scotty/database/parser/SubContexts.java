package scotty.database.parser;

import java.util.Map;

/**
 *
 */
public interface SubContexts<T extends NamedContext> {
    Map<String, T> getMap();
}
