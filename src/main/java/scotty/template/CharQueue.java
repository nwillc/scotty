/*
 * Copyright (c) 2013, nwillc@gmail.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any purpose with or
 * without fee is hereby granted, provided that the above copyright notice and this permission
 * notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH REGARD TO
 * THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT
 * SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR
 * ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF
 * CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE
 * OR PERFORMANCE OF THIS SOFTWARE.
 */

package scotty.template;

import java.io.IOException;
import java.io.OutputStream;

/**
 * A fixed size character buffer that overflows into an output source if provided. Used
 * to provide a small window into an advancing stream, allowing a parser to search for a fixed sequence.
 */
public class CharQueue implements Comparable<CharQueue>, AutoCloseable {
    private final OutputStream overflow;
    private final int[] data;
    private int start, end;

    public CharQueue(String str, OutputStream overflow) {
        this(str.length(), overflow);
        try {
            add(str);
        } catch (IOException e) {
            assert str.equals(toString());
        }
    }

    public CharQueue(int capacity, OutputStream overflow) {
        this.overflow = overflow;
        data = new int[capacity];
        clear();
    }

    private void clear() {
        for (int i = 0; i < data.length; i++) {
            data[i] = (char) 0;
        }
        start = end = 0;
    }

    public int size() {
        return end - start;
    }

    public void add(String str) throws IOException {
        for (char c : str.toCharArray()) {
            add(c);
        }
    }

    public void add(char ch) throws IOException {
        if (end >= data.length) {
            overflow.write(data[start % data.length]);
            start++;
        }

        data[end % data.length] = ch;
        end++;
    }

    @Override
    public void close() throws Exception {
        overflow.write(toString().getBytes());
        overflow.flush();
    }

    @Override
    public int compareTo(CharQueue that) {
        if (that == null) {
            throw new NullPointerException("Compare to null");
        }

        final int min = Math.min(this.data.length, that.data.length);

        for (int i = 0; i < min; i++) {
            int thisIndex = (this.start + i) % this.data.length;
            int thatIndex = (that.start + i) % that.data.length;

            if (this.data[thisIndex] == that.data[thatIndex]) {
                continue;
            }

            return this.data[thatIndex] < that.data[thatIndex] ? -1 : 1;
        }

        if (this.size() == that.size()) {
            return 0;
        }

        return this.size() < that.size() ? -1 : 1;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = start; i < end; i++) {
            stringBuilder.append((char) data[i % data.length]);
        }
        return stringBuilder.toString();
    }

}
