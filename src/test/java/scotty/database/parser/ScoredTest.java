package scotty.database.parser;

import org.junit.Test;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

/**
 *
 */
public class ScoredTest {
    @Test
    public void sortTest() throws Exception {
        List<Ranked<Integer>> list = new LinkedList<>();

        list.add(new Ranked<>(1, 1));
        list.add(new Ranked<>(3, 3));
        list.add(new Ranked<>(2, 2));

        Collections.sort(list);

        assertEquals(3, list.get(0).getData().intValue());
        assertEquals(2, list.get(1).getData().intValue());
        assertEquals(1, list.get(2).getData().intValue());
    }

}
