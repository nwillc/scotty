/*
 * Copyright (c) 2013-2014, nwillc@gmail.com
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

package scotty.database.parser;

import com.google.common.base.Function;

/**
 * Relative similarity of this to another ranging from NOT_SIMILAR to SIMILAR (0.0 .. 1.0). If there is a
 * strong similarity then it is SIMILAR. As the similarity is less the value is reduced. DOWN_GRADE is a
 * suitable amount to express a notable difference.
 */
public interface Similarity<T> {
    float SIMILAR = 1.0F;
    float NOT_SIMILAR = 0.0F;
    float DOWN_GRADE = 0.25F;

    /**
     * Return a ranking NOT_SIMILAR to SIMILAR of how closely related this object is to the one passed in.
     *
     * @param b object being compared too
     * @return the similarity value
     */
    float similarity(T b);

}
