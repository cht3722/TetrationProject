package service;

import java.net.Socket;

import org.apache.thrift.TProcessor;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

/**
 * 
 * @author haotianchen
 * This file is used to generated the service, and it will open the port at 7911
 * 
 */
public class ServiceProvider {
	public static void main(String[] args) {
		try {
			
			TServerSocket serverTransport = new TServerSocket(7911);
			KeyValueStoreService serve = new KeyValueStoreService();
			TProcessor processor = new WebService.Processor(serve);
			TServer server = new TSimpleServer(new TServer.Args(serverTransport).processor(processor));
			System.out.println("Start server on port 7911...");
			server.serve();

			serverTransport.close();
		} catch (TTransportException e) {
			e.printStackTrace();
		}
	}
}
