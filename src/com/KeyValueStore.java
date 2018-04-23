package com;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
/**
 * @author haotianchen
 * Assumption:
 * 1. All the input string are not null and the length is not 0
 * 2. Use an integer to represent the time. After every operation, time will add 1 automatically.
 */
public class KeyValueStore {
	/**
	 /
	
	/**
	 * This class provide the basic function of key value store with history
	 * 
	 * Provide the following API for user to use:
	 * 
	 * 1. void put(String key, String value)
	 * 2. List<String> get(String key)
	 * 3. void del(String key)
	 * 4. void del(String key, String value) 
	 * 5. List<String> get(String key, int t)
	 * 6. List<String> diff(String key, int t1, int t2)
	 */
	
	/** Fields in the object that store the data:
	 * map: stores the key-value pair, map from key to its owned map
	 * timestamp: stores the key-value pair, map from key to its owned timestamp map
	 * time: system time, increase whenever there is a call of function.
	 * 
	 * Storage hierarchy: for a key K and value V:
	 * Map<String, Map<String, List<Integer>>>				--> key:K - value:K's owned map
	 * 						  |
	 * 			   Map<String, List<Integer>>		  	    --> key:V - value:V's owned list
	 * 						  |
	 *						   List<Integer>					--> V's owned list
	 *
	 * V's owned list: List<Integer>
	 *		for list in the field map: it stores the data with 1 and 0. 1 means that at that position,
	 *			it had a put operation, 0 means that at that position, it had a delete operation.
	 *		for list in the field timestamp: it stores that data with system time.
	 */
	/**
	 * 
	 * @author haotianchen
	 * This is a helper class which is used to help sort the list
	 */
	public static class Pair {
		int t;
		String s;
		public Pair(String s, int t) {
			this.s = s;
			this.t = t;
		}
	}
	
	
	Map<String, Map<String, List<Integer>>> map;
	Map<String, Map<String, List<Integer>>> timestamp;
	int time;
	
	/**
	 * construction function
	 */
	public KeyValueStore() {
		this.map = new HashMap<String, Map<String, List<Integer>>>();
		this.timestamp = new HashMap<String, Map<String, List<Integer>>>();
		this.time = -1;
	}

	/**
	 * put operation: adds the key with the value.
	 * Time complexity: O(1), two map will take O(1) time, the operation to add at the end of list 
	 * 					will take O(1) time.
	 * Space complexity : O(n), n is the total operation, because need to add to the corresponding list
	 * 					whatever operation 
	 */
	public void put(String key, String value) {
		time++;
		// if not contains the key, put a new one
		if (!map.containsKey(key)) {
			map.put(key, new HashMap<String, List<Integer>>());
			timestamp.put(key, new HashMap<String, List<Integer>>());
		}
		
		Map<String, List<Integer>> keyMap = map.get(key);
		Map<String, List<Integer>> keyTimeStamp = timestamp.get(key);
		// if not contains the value, put a new one
		if (!keyMap.containsKey(value)) {
			keyMap.put(value, new ArrayList<Integer>());
			keyTimeStamp.put(value, new ArrayList<Integer>());
		}
		
		List<Integer> valueList = keyMap.get(value);
		List<Integer> valueTimeStamp = keyTimeStamp.get(value);
		
		// add the operation list with 1 as sign operation of put
		valueList.add(1);
		// add the timestamp list with current time
		valueTimeStamp.add(time);
	}
	
	/**
	 * get operation: returns all values associated with the key, if present
	 * time complexity: O(k + klogk) = O(klogk), k is the total value that associated with the key,
	 * 					because need to check the ends of all lists and sort them depend
	 * 					on the timestamps.
	 */
	public List<String> get(String key) {
		time++;
		List<String> result = new ArrayList<>();
		List<Pair> helper = new ArrayList<Pair>();
		// if no record in the map, return empty list.
		if (!map.containsKey(key)) {
			return result;
		}
		
		Map<String, List<Integer>> keyMap = map.get(key);
		Map<String, List<Integer>> keyTimeStamp = timestamp.get(key);
		// Iterate all the values and find their corresponding lists
		for (Map.Entry<String, List<Integer>> entry : keyMap.entrySet()) {
			List<Integer> valueList = entry.getValue();
			List<Integer> valueTimeStamp = keyTimeStamp.get(entry.getKey());
			// if the list's last element is 1, which means a put operation, add it to the result
			if (valueList.get(valueList.size() - 1) == 1) {
				helper.add(new Pair(entry.getKey(), valueTimeStamp.get(valueTimeStamp.size() - 1)));
			}
		}
		sort(helper, result);
		return result;
	}
	
	/**
	 * del(key, value) : deletes the specified value from the key.
	 * time complexity:  O(1), similar to put operation
	 * space complexity: O(n), n is the total operation
	 */
	public void del(String key, String value) {
		time++;
		if (!map.containsKey(key)) {
			return;
		}
		Map<String, List<Integer>> keyMap = map.get(key);
		Map<String, List<Integer>> keyTimeStamp = timestamp.get(key);
		
		if (!keyMap.containsKey(value)) {
			return;
		}
		List<Integer> valueList = keyMap.get(value);
		List<Integer> valueTimeStamp = keyTimeStamp.get(value);
		// instead of delete, add a 0 to the operation list as a sign of del operation
		valueList.add(0);
		
		valueTimeStamp.add(time);
	}
	
	/**
	 * del(key) : deletes the key from the store.
	 * time complexity: O(k), k is the value associated with the key
	 * spaec complexity: O(n), n is the total operation
	 */
	public void del(String key) {
		time++;
		if (!map.containsKey(key)) {
			return;
		}
		Map<String, List<Integer>> keyMap = map.get(key);
		Map<String, List<Integer>> keyTimeStamp = timestamp.get(key);
		// check all the operation list and add a 0
		for (List<Integer> list : keyMap.values()) {
			list.add(0);
		}
		// check all the timestamp list and add a current time
		for (List<Integer> list : keyTimeStamp.values()) {
			list.add(time);
		}
	}
	
	/**
	 * get(key, time) : returns all values associated with the key up to the specified time.
	 * time complexity: O(klogm + klogk), k is the values associated with the key, m is the length of list
	 * 					logm is time need for binary search, klogk is the time to sort the result
	 */
	public List<String> get(String key, int t) {
		time++;
		List<String> result = new ArrayList<>();
		List<Pair> helper = new ArrayList<Pair>();
		if (!map.containsKey(key)) {
			return result;
		}
		
		Map<String, List<Integer>> keyMap = map.get(key);
		Map<String, List<Integer>> keyTimeStamp = timestamp.get(key);
		
		for (Map.Entry<String, List<Integer>> entry : keyTimeStamp.entrySet()) {
			// since that the timestamp list is a sorted array, it can use binary search to 
			// find the exact index which is the largest index that is smaller or equal to 
			// the target query time t
			List<Integer> list = entry.getValue();
			
			int idx = binarySearch(list, t);
			if (idx == -1) {
				continue;
			}
			// after getting the index, check whether the index at that position is 1. if it
			// is , add the corresponding string to the result. if not, continue.
			List<Integer> valueList = keyMap.get(entry.getKey());
			if (valueList.get(idx) == 1) {
				helper.add(new Pair(entry.getKey(), list.get(idx)));
			}
		}
		sort(helper, result);
		return result;
	}
	
	/**
	 * returns the difference in value associated with the key between time1 and time2.  time1 <= time2 .
	 * time complexity: O(klogm + klogk), k is the values associated with the key, m is the length of list
	 * 					logm is time need for binary search, klogk is the time to sort the result
	 */
	public List<String> diff(String key, int t1, int t2) {
		time++;
		List<String> result = new ArrayList<>();
		List<Pair> helper = new ArrayList<Pair>();

		if (!map.containsKey(key)) {
			return result;
		}
		
		Map<String, List<Integer>> keyMap = map.get(key);
		Map<String, List<Integer>> keyTimeStamp = timestamp.get(key);
		
		for (Map.Entry<String, List<Integer>> entry : keyTimeStamp.entrySet()) {
			// since that the timestamp list is a sorted array, it can use binary search to 
			// find the exact index which is the largest index that is smaller or equal to 
			// the target query time t1 and t2
			List<Integer> list = entry.getValue();
			int idx1 = binarySearch(list, t1);
			int idx2 = binarySearch(list, t2);
			// if both of its index is -1 which means that there is no operation before both time
			// do nothing
			if (idx1 == -1 && idx2 == -1) {
				continue;
			}
			// if one of them is not -1 and the list operation at that index is 1, add to the result/
			List<Integer> valueList = keyMap.get(entry.getKey());
			if (idx1 == -1) {
				if (valueList.get(idx2) == 1) {
					helper.add(new Pair(entry.getKey(), list.get(idx2)));
				}
				continue;
			}
			if (idx2 == -1) {
				if (valueList.get(idx1) == 1) {
					helper.add(new Pair(entry.getKey(), list.get(idx1)));
				}
				continue;
			}
			// if two of them is not -1, and the list operation at t1 and t2 are (1 and 0) or (0 and 1)
			// add to the result
			if (valueList.get(idx2) + valueList.get(idx1) == 1) {
				helper.add(new Pair(entry.getKey(), list.get(idx1 == 0 ? idx2 : idx1)));
			}
		}
		sort(helper, result);
		return result;
		
	}
	/**
	 * 
	 * @param list the timestamp list which contains the info of timestamp
	 * @param t the time we want to find
	 * @return the last index which is smaller or equal to t
	 */
	private int binarySearch(List<Integer> list, int t) {
		int left = 0;
		int right = list.size() - 1;
		while (left < right - 1) {
			int mid = left + (right - left) / 2;
			int val = list.get(mid);
			if (val > t) {
				right = val;
			} else {
				left = val;
			}
		}
		if (list.get(right) <= t) {
			return right;
		} else if (list.get(left) <= t){
			return left;
		} else {
			return -1;
		}
	}
	/**
	 * 
	 * @param list the Pair which contains the value and time info
	 * @param result the sorted string based on its time
	 */
	private void sort(List<Pair> list, List<String> result) {
		PriorityQueue<Pair> minHeap = new PriorityQueue<>(new Comparator<Pair>() {

			@Override
			public int compare(Pair o1, Pair o2) {
				if (o1.t ==  o2.t) {
					return 0;
				}
				return o1.t > o2.t ? 1 : -1;
			}
			
		});
		for (Pair p : list) {
			minHeap.add(p);
		}
		while (!minHeap.isEmpty()) {
			result.add(minHeap.poll().s);
		}
	}
}
