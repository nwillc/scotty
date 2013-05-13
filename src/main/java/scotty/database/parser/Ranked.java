package scotty.database.parser;

/**
 * Pair a score with some data item. Scores natural sort order is high to low.
 */
public final class Ranked<T> implements Comparable<Ranked> {
    private final Integer score;
    private final Integer age;
    private final T data;

    public Ranked(Integer score, Integer age, T data) {
        if (score == null || age == null) {
            throw new IllegalArgumentException("Scores and ages must have a non-null to rank objects.");
        }
        this.score = score;
        this.age = age;
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public Integer getScore() {
        return score;
    }

    @Override
    public int compareTo(Ranked o) {
        int scoreCompare = o.score.compareTo(score);

        return scoreCompare != 0 ? scoreCompare : age.compareTo(o.age);
    }
}
