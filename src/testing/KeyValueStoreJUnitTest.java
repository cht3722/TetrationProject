package testing;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import com.KeyValueStore;
/**
 * 
 * This is the JUnitTest, which will test the given case.
 *
 */
public class KeyValueStoreJUnitTest {

	@Test
	public void test() {
		KeyValueStore keyValueStore = new KeyValueStore();
		List<String> list;
		
		// time start
		keyValueStore.put("A", "c");
		keyValueStore.put("B","d");
		
		// time at 2
		list = keyValueStore.get("A");
		assertEquals(list.get(0), "c");
		assertEquals(list.size(), 1);
		keyValueStore.put("A","e");
		
		// time at 4
		list = keyValueStore.get("A");
		assertEquals(list.get(0), "c");
		assertEquals(list.get(1), "e");
		assertEquals(list.size(), 2);

		// time at 5
		list = keyValueStore.get("A", 2);
		assertEquals(list.get(0), "c");
		keyValueStore.del("A");
		
		// time at 7
		list = keyValueStore.get("A");
		assertEquals(list.size(), 0);
		
		// time at 8
		list = keyValueStore.get("A",5);
		assertEquals(list.get(0), "c");
		assertEquals(list.get(1), "e");
		keyValueStore.put("B","f");
		keyValueStore.del("B","d");
		
		// time at 11
		list = keyValueStore.get("B");
		assertEquals(list.get(0), "f");
		assertEquals(list.size(), 1);

		
		// diff operation
		list = keyValueStore.diff("A", 1, 2);
		assertEquals(list.size(), 0);

		list = keyValueStore.diff("A", 3, 5);
		assertEquals(list.size(), 0);

		list = keyValueStore.diff("A", 1, 4);
		assertEquals(list.get(0), "e");
		assertEquals(list.size(), 1);

		list = keyValueStore.diff("B", 0, 1);
		assertEquals(list.get(0), "d");
		assertEquals(list.size(), 1);

	}
}
