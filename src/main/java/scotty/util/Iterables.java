package scotty.util;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;

import static com.google.common.collect.Iterables.transform;

/**
 *
 */
public class Iterables {
	public static <T> void forEach(Iterable<T> i, final Consumer<T> c) {
		Preconditions.checkNotNull(i);
		Preconditions.checkNotNull(c);

		for (T t : i) {
			c.accept(t);
		}
	}
}
