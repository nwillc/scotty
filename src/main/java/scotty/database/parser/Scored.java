package scotty.database.parser;

/**
 * Pair a score with some data item. Scores natural sort order is high to low.
 */
public final class Scored<T> implements Comparable<Scored> {
    private final Integer score;
    private final T data;

    public Scored(Integer score, T data) {
        if (score == null) {
            throw new IllegalArgumentException("Scored object must have a non-null score.");
        }
        this.score = score;
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public Integer getScore() {
        return score;
    }

    @Override
    public int compareTo(Scored o) {
        return o.score.compareTo(score);
    }
}
