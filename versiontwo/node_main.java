import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class node_main {

    public static void main(String[] args) {
		try {
            int port = 8080;
            boolean isFirst = false;
            int memorySize = 0;
            
            if(args.length > 2 && args[2].matches("-?\\d+") && Integer.parseInt(args[2]) > 1024) {
                port = Integer.parseInt(args[2]);
            }
            if(args.length > 1) {  if(args[0].equals("first")) { isFirst = true; memorySize = Integer.parseInt(args[1]); } }

            if(isFirst) {
                Registry registry= LocateRegistry.createRegistry(port);
                node_itfImpl firstNode = new node_itfImpl(0);
                node_itf firstNodeStub = (node_itf) UnicastRemoteObject.exportObject(firstNode, 0);
                registry.rebind("node0", firstNodeStub);
                firstNodeStub.setMemory(memorySize);
                firstNodeStub.registerAddress(firstNodeStub);
                System.out.println("First node is created...!!!");
            
                Scanner scanner = new Scanner(System.in);
                String message;
                while (true) {
                    message = scanner.nextLine();
                    String [] arr = message.split(" ");
                    if (arr[0].equals("snapshotlocal")) {
                        firstNodeStub.snapshotLocal();
                        System.out.println("");
                    } else if (arr[0].equals("snapshot")) {
                        Map <Integer, String> tmpMap = new HashMap<Integer, String>();
                        firstNodeStub.snapshot(0, tmpMap);
                    } else if((arr[0].equals("read") && arr.length > 1 && arr[1].matches("-?\\d+")) 
                                && Integer.parseInt(arr[1]) > 0) {
                                
                                int address = Integer.parseInt(arr[1]);
                                node_itf nodeToRead = firstNodeStub.getNodeFromAdress(address);
                                String value = nodeToRead.read(address);
                                System.out.println("Address " + arr[1] + " : " +  value);
                    
                    } else if((arr[0].equals("write") && arr.length > 2 && arr[1].matches("-?\\d+")) 
                            && Integer.parseInt(arr[1]) > 0) {
                                
                                int address = Integer.parseInt(arr[1]);
                                String value = arr[2];
                                node_itf nodeToWrite = firstNodeStub.getNodeFromAdress(address);
                                nodeToWrite.write(address, value);
                                System.out.println("OK");
                    } else {
                                System.out.println("Command Unknown...!!!");
                    }
                }
            } else {
                Registry registry = LocateRegistry.getRegistry(port);
                node_itf firstNodeStub = (node_itf) registry.lookup("node0");
                int nodeId = firstNodeStub.getNextId();
                int nodeMemorySize = firstNodeStub.getMemorySize();
                node_itfImpl node = new node_itfImpl(nodeId);
                node_itf nodeStub = (node_itf) UnicastRemoteObject.exportObject(node, 0);
                registry.rebind("node" + nodeId, nodeStub);
                nodeStub.setMemory(nodeMemorySize);

                firstNodeStub.setNextNodeTraverse(nodeStub);
                firstNodeStub.registerAddress(nodeStub);
                System.out.println("Node " + nodeId + " is created...!!!");

                Scanner scanner = new Scanner(System.in);
                String message;
                while (true) {
                    message = scanner.nextLine();
                    String [] arr = message.split(" ");
                    if (arr[0].equals("snapshotlocal")) {
                        nodeStub.snapshotLocal();
                        System.out.println("");
                    } else if (arr[0].equals("snapshot")) {
                        Map <Integer, String> tmpMap = new HashMap<Integer, String>();
                        nodeStub.snapshot(nodeId, tmpMap);
                    } else if((arr[0].equals("read") && arr.length > 1 && arr[1].matches("-?\\d+")) 
                                && Integer.parseInt(arr[1]) > 0) {
                                
                                int address = Integer.parseInt(arr[1]);
                                node_itf nodeToRead = firstNodeStub.getNodeFromAdress(address);
                                String value = nodeToRead.read(address);
                                System.out.println("Address " + address + " : " +  value);
                    
                    } else if((arr[0].equals("write") && arr.length > 2 && arr[1].matches("-?\\d+")) 
                            && Integer.parseInt(arr[1]) > 0) {
                                
                                int address = Integer.parseInt(arr[1]);
                                String value = arr[2];
                                node_itf nodeToWrite = firstNodeStub.getNodeFromAdress(address);
                                nodeToWrite.write(address, value);
                                System.out.println("OK");
                    } else {
                                System.out.println("Command Unknown...!!!\n" + 
                                "Usage =>\n1. write <address> <value>\n2. read <address>\n3. snapshot\n4. snapshotlocal");
                    }
                }
            }
        } catch (Exception ex) {
            System.out.println("Error running a node\nFirst Node Usage : first <memory length> <optional : port>");
        }
    }
}