import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

public class dsm_main {
	public static void main(String[] args) {
		try {
			int port = 8080;
			if(args.length > 1 && args[1].matches("-?\\d+") && Integer.parseInt(args[1]) > 1024) {
				port = Integer.parseInt(args[0]);
			}
			Registry registry= LocateRegistry.createRegistry(port);
			dsm_itfImpl myDsm = new dsm_itfImpl();
			dsm_itf dsmStub = (dsm_itf) UnicastRemoteObject.exportObject(myDsm, 0);
			registry.rebind("myDsm", dsmStub);
			
			for(int i = 0; i < Integer.parseInt(args[0]); i++) {
				node_itfImpl node = new node_itfImpl(i);
				node_itf nodeStub = (node_itf) UnicastRemoteObject.exportObject(node, 0);
				dsmStub.addNode(nodeStub);
			}
			
			System.out.println("DSM Ready\n" + args[0] + " nodes created...!!!");

		} catch (Exception e) {
			System.out.println("Failed to run the DSM...!!!");
			System.exit(0);
		}
	}
}
