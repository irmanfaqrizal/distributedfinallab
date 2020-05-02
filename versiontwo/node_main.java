import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.Scanner;

public class node_main {

    public static void main(String[] args) {
		try {
            int port = 8080;
            boolean isFirst = false;
            
            if(args.length > 2 && args[1].matches("-?\\d+") && Integer.parseInt(args[1]) > 1024) {
                port = Integer.parseInt(args[1]);
            }
            if(args.length > 0) {  if(args[0].equals("first")) { isFirst = true; } }

            if(isFirst) {
                Registry registry= LocateRegistry.createRegistry(port);
                node_itfImpl firstNode = new node_itfImpl(0);
                node_itf firstNodeStub = (node_itf) UnicastRemoteObject.exportObject(firstNode, 0);
                registry.rebind("node0", firstNodeStub);
                System.out.println("First node is created...!!!");
            
                Scanner scanner = new Scanner(System.in);
                String message;
                while (true) {
                    message = scanner.nextLine();
                    if(message.equals("get")) {
                        System.out.println(firstNodeStub.getNextNode().getId());
                    }
                }

                // Registry registry= LocateRegistry.createRegistry(port);
                // nodefirst_itfImpl firstNode = new nodefirst_itfImpl(0);
                // nodefirst_itf firstNodeStub = (nodefirst_itf) UnicastRemoteObject.exportObject(firstNode, 0);
                // registry.rebind("firstNode", firstNodeStub);
                // System.out.println("First node is created...!!!");
            
                // Scanner scanner = new Scanner(System.in);
                // String message;
                // while (true) {
                //     message = scanner.nextLine();
                //     if(message.equals("get")) {
                //         System.out.println(firstNodeStub.getId());
                //     }
                // }
            } else {
                Registry registry = LocateRegistry.getRegistry(port);
                node_itf firstNodeStub = (node_itf) registry.lookup("node0");
                int nextId = firstNodeStub.getNextId();
                
                node_itfImpl node = new node_itfImpl(nextId);
                node_itf nodeStub = (node_itf) UnicastRemoteObject.exportObject(node, 0);
                registry.rebind("node" + nextId, nodeStub);

                firstNodeStub.setNextNodeTraverse(nodeStub);
                System.out.println("Node " + nextId + " is created...!!!");

                Scanner scanner = new Scanner(System.in);
                String message;
                while (true) {
                    message = scanner.nextLine();
                    if(message.equals("get")) {
                        System.out.println(nodeStub.getNextNode().getId());
                    }
                }

                // Registry registry = LocateRegistry.getRegistry(port);
                // nodefirst_itf firstNodeStub = (nodefirst_itf) registry.lookup("firstNode");
                // int nextId = firstNodeStub.getNextId();

                // node_itfImpl node = new node_itfImpl(nextId);
                // node_itf nodeStub = (node_itf) UnicastRemoteObject.exportObject(node, 0);
                // registry.rebind("node" + nextId, nodeStub);

                // firstNodeStub.setNextNode(nodeStub);
                // System.out.println("Node " + nextId + " is created...!!!");

                // Scanner scanner = new Scanner(System.in);
                // String message;
                // while (true) {
                //     message = scanner.nextLine();
                //     if(message.equals("get")) {
                //         System.out.println(nodeStub.getId());
                //     }
                // }
            }
            
            // Registry registry;
            // node_itf nodeStub;
            // registry = LocateRegistry.getRegistry(port);
            // System.out.println(registry);
            // try {
            //     nodeStub = (node_itf) registry.lookup("DSM");
            //     System.out.println("aaaa");
            //     System.out.println(nodeStub.getId());
            //     nodeStub.createNode(port);
            //     System.out.println("aaaa123");
            //     // my.snapshot();
            // } catch (Exception e) {
            //     registry = LocateRegistry.createRegistry(port);
            //     nodefirst_itfImpl myNode = new nodefirst_itfImpl(0);
            //     nodeStub = (nodefirst_itf) UnicastRemoteObject.exportObject(myNode, 0);
            //     registry.rebind("DSM", nodeStub);
            //     System.out.println(nodeStub.getId());
            // }
        // Scanner scanner = new Scanner(System.in);
        // String message;
        // while (true) {
        //     message = scanner.nextLine();
        //     String [] arr = message.split(" ");

        //     if (arr[0].equals("snapshot")) {
                
        //     } else if((arr[0].equals("read") && arr.length > 1 && arr[1].matches("-?\\d+")) 
        //              && Integer.parseInt(arr[1]) > 0) {

        //         System.out.println("Address " + arr[1] + " : " +  nodeStub.read(Integer.parseInt(arr[1])));
            
        //     } else if((arr[0].equals("write") && arr.length > 2 && arr[1].matches("-?\\d+")) 
        //             && Integer.parseInt(arr[1]) > 0) {
                        
        //                 nodeStub.write(Integer.parseInt(arr[1]), arr[2]);
        //                 System.out.println("OK");
        //     } else {
        //                 System.out.println("Command Unknown...!!!");
        //     }
        // }
			
        } catch (Exception ex) {
            System.out.println("Error running a node");
        }
    }
}