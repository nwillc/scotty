package scotty.util;

import com.google.common.base.Preconditions;

public class Iterables {
	public static <T> void forEach(Iterable<T> i, final Consumer<T> c) {
		Preconditions.checkNotNull(i);
		Preconditions.checkNotNull(c);

		for (T t : i) {
			c.accept(t);
		}
	}
}
