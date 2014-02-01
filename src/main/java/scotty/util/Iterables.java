package scotty.util;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;

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

	public static <T,R> R forEach(Iterable<T> i, final Function<T,R> f) {
		Preconditions.checkNotNull(i);
		Preconditions.checkNotNull(f);
		R ret = null;

		for (T t : i) {
			ret = f.apply(t);
		}
		Preconditions.checkNotNull(ret, "Function can not return null");
		return ret;
	}
}
