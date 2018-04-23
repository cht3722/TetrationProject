package service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.thrift.TException;

import com.KeyValueStore;
/**
 * 
 * @author haotianchen
 * This class implement the interface generated from WebService
 * and will make sure that the input to the data is not null
 * 
 */
public class KeyValueStoreService implements WebService.Iface {
	KeyValueStore keyValueStore;
	
	public KeyValueStoreService(){
		keyValueStore = new KeyValueStore();
	}
	
	@Override
	public List<String> get(String name) throws TException {
		if (name == null || name.length() == 0) {
			return new ArrayList<String>();
		}
		return keyValueStore.get(name);
	}

	@Override
	public void put(String user, String friend) throws TException {
		if (user == null || user.length() == 0 || friend == null || friend.length() == 0) {
			return;
		}
		keyValueStore.put(user, friend);
	}
	@Override
	public void removeuser(String user) throws TException {
		if (user == null || user.length() == 0) {
			return;
		}
		keyValueStore.del(user);
	}
	@Override
	public void removefriend(String user, String friend) throws TException {
		if (user == null || user.length() == 0 || friend == null || friend.length() == 0) {
			return;
		}
		keyValueStore.del(user, friend);
		
	}
	@Override
	public List<String> getwithtime(String user, int t) throws TException {
		if (user == null || user.length() == 0) {
			return new ArrayList<String>();
		}
		return keyValueStore.get(user, t);
	}
	@Override
	public List<String> diff(String user, int t1, int t2) throws TException {
		if (user == null || user.length() == 0) {
			return new ArrayList<String>();
		}
		return keyValueStore.diff(user, t1, t2);
	}

}
