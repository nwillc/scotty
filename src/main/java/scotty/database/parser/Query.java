package scotty.database.parser;

import scotty.database.Context;

import java.util.Collection;

/**
 *
 */
public interface Query<T> {
    T find(Context context);

    Collection<T> filter(Context context);
}
