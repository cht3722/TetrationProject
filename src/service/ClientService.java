package service;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.apache.thrift.TApplicationException;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
/**
 * 
 * @author haotianchen
 * This file is used for client service
 */
public class ClientService {
	/**
	 * This class provide two api for users: 
	 * void get(String user), it will print the list of friends in the order of time the user added them
	 * void put(String user, String friend), it will add the friend to user's friend list
	 */
	public static void main(String[] args) {
		//demo client request
		put("A", "t");
		get("A");
		put("A", "z");
		put("A", "q");
		put("A", "e");
		get("A");
	}

	public static void get(String user) {
		TTransport transport = new TSocket("0.0.0.0", 7911);
		try {
			transport.open();
			TProtocol protocol = new TBinaryProtocol(transport);
			WebService.Client client = new WebService.Client(protocol);
			List<String> list = client.get(user);
			System.out.println(user + "'s friend list:");
			for (String s : list) {
				System.out.print(s + " ");
			}
			System.out.println();
			transport.close();
		} catch (TApplicationException e) {

			if (e.getType() == TApplicationException.MISSING_RESULT) {
				System.out.println("null");
			}

		} catch (TException e) {

		}
	}
	public static void put(String user, String friend) {
		TTransport transport = new TSocket("0.0.0.0", 7911);
		try {
			transport.open();
			TProtocol protocol = new TBinaryProtocol(transport);
			WebService.Client client = new WebService.Client(protocol);
			client.put(user, friend);
			System.out.println("Add friend successfully");
			transport.close();
		} catch (TApplicationException e) {

			if (e.getType() == TApplicationException.MISSING_RESULT) {
				System.out.println("null");
			}

		} catch (TException e) {

		}
	}

}
