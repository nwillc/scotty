package scotty.database.parser;

import org.junit.Test;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

/**
 *
 */
public class RankedTest {
    @Test
    public void sortTest() throws Exception {
        List<Ranked<Integer>> list = new LinkedList<>();

        list.add(new Ranked<>(1, 0, 1));
        list.add(new Ranked<>(3, 1, 4));
        list.add(new Ranked<>(3, 0, 3));
        list.add(new Ranked<>(2, 0, 2));

        Collections.sort(list);

        assertEquals(3, list.get(0).getData().intValue());
        assertEquals(4, list.get(1).getData().intValue());
        assertEquals(2, list.get(2).getData().intValue());
        assertEquals(1, list.get(3).getData().intValue());

        list.add(new Ranked<>(3, -1, 0));

        Collections.sort(list);

        assertEquals(0, list.get(0).getData().intValue());
    }

}
