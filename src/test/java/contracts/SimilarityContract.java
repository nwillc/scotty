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

package contracts;

import org.junit.Test;
import scotty.database.parser.Similarity;

import static org.assertj.core.api.Assertions.assertThat;

public class SimilarityContract<T> {
    private Similarity<T> from;
    private T like;
    private T dislike;

    protected void setFrom(Similarity<T> instance) {
        this.from = instance;
    }

    protected void setLike(T like) {
        this.like = like;
    }

    public void setDislike(T dislike) {
        this.dislike = dislike;
    }

    @Test
    public void shouldRespectSimilarityLimitsMatch() {
        assertThat(from).isNotNull();
        assertThat(like).isNotNull();
        assertThat(from.similarity(like)).isLessThanOrEqualTo(Similarity.SIMILAR).isGreaterThanOrEqualTo(Similarity.NOT_SIMILAR);
    }

    @Test
    public void shouldRespectSimilarityLimitsNotMatch() {
        assertThat(from).isNotNull();
        assertThat(dislike).isNotNull();
        assertThat(from.similarity(dislike)).isLessThanOrEqualTo(Similarity.SIMILAR).isGreaterThanOrEqualTo(Similarity.NOT_SIMILAR);
    }

    @Test
    public void shouldNotBeSimilarToNull() throws Exception {
        assertThat(from).isNotNull();
        assertThat(from.similarity(null)).isEqualTo(Similarity.NOT_SIMILAR);
    }
}