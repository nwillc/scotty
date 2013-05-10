package scotty.database.parser;

import scotty.database.Context;
import scotty.database.Instance;

import java.util.List;

/**
 *
 */
public interface InstanceQuery {
	/**
	 * Find the best matching instance to a query.
	 */
	Instance find(Context context);

	/**
	 * Find all the matching instances to a query and return in similarity score order.
	 */
	List<Instance> match(Context context);
}
