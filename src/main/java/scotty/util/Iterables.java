package scotty.util;

import com.google.common.base.Function;

import static com.google.common.collect.Iterables.transform;

/**
 *
 */
public class Iterables {
	public static <T> void forEach(Iterable i, final Consumer<T> c) {
	  transform(i, new Function<T,Void>() {
		  @Override
		  public Void apply(T t) {
			  c.accept(t);
			  return null;
		  }
	  });
	}
}
