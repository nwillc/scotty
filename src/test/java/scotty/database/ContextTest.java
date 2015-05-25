/*
 * Copyright (c) 2015, nwillc@gmail.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 *
 */

package scotty.database;

import contracts.SimilarityContract;
import org.junit.Before;
import org.junit.Test;

public class ContextTest extends SimilarityContract<Context> {
    private Context from;
    private Context dislike;

    @Before
    public void setUp() throws Exception {
        from = new Context("foo=bar");
        dislike = new Context("bar=baz");

        setFrom(from);
        setLike(from);
        setDislike(dislike);
    }

    @Test
    public void testToString() throws Exception {
        from.getContained().put("a", dislike);
        System.out.println(from);
    }
}