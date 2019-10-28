package ca.datastructures.simplemap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class HomemadeMapTest {
    private SimpleMap<String, String> map;
    private static final String key = "key123";
    private static final String value = "value987";

    @BeforeEach
    void init() {
        map = new HomemadeMap<>();
    }

    @Test
    void testGetFromEmptyNull() {
        assertNull(map.get("key"));
    }

    @Test
    void testContains() {
        map.put(key, value);
        assertTrue(map.contains(key));
        assertFalse(map.contains(key+"Q"));
    }

    @Test
    void testDelete() {
        map.put(key, value);
        assertEquals(value, map.remove(key));
        assertFalse(map.contains(key));
        assertEquals(0, map.size());
        assertNull(map.remove(key));
    }

    @Test
    void testPutGetOneObject() {
        map.put(key, value);
        assertEquals(value, map.get(key));
        assertNull(map.get("emptyKey"));
    }

    @Test
    void testPutSameKey() {
        map.put(key, "initial value");
        map.put(key, value);
        assertEquals(value, map.get(key));
        assertEquals(1, map.size());
    }

    @Test
    void testGrow() {
        map = new HomemadeMap<>(1);
        int numberOfElements = 3;
        for (int i = 0; i < numberOfElements; i++) {
            map.put(key + i, value + i);
        }
        assertEquals(numberOfElements, map.size());
        for (int i = 0; i < numberOfElements; i++) {
            assertEquals(value + i, map.get(key + i));
        }
    }

    @Test
    void testKeySetAndValues() {
        int numberOfElements = 3;
        Set<String> keys = new HashSet<>();
        Collection<String> values = new ArrayList<>();
        for (int i = 0; i < numberOfElements; i++) {
            map.put(key + i, value + i);
            keys.add(key + i);
            values.add(value + i);
        }

        assertEquals(keys, map.keySet());
        assertTrue(values.containsAll(map.values()));
        assertTrue(map.values().containsAll(values));
    }

}