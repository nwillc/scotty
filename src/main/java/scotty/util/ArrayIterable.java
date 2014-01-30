package scotty.util;

import com.sun.tools.example.debug.bdi.MethodNotFoundException;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 *  Iterable over an Array.
 */
public class ArrayIterable<T> implements Iterable<T> {
	private final T[] data;

	public ArrayIterable(T[] data) {
		this.data = data;
	}

	@Override
	public Iterator<T> iterator() {
		return new ArrayIterator<T>(data);
	}

	private class ArrayIterator<T> implements Iterator<T> {
		private final T[] data;
		private int index;

		private ArrayIterator(T[] data) {
			this.data = data;
			index = 0;
		}

		@Override
		public boolean hasNext() {
			return data != null && index < data.length;
		}

		@Override
		public T next() {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			T next = data[index];
			index++;
			return next;
		}

		@Override
		public void remove() {
			throw new NoSuchMethodError("Remove not implemented for ArrayIterator.");
		}
	}


}
