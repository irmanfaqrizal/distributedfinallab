import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Map;
import java.util.Scanner;

public class dsm_client {
    public static void main(String[] args) throws RemoteException, NotBoundException {

        int port = 8080;
        if(args.length > 1 && args[1].matches("-?\\d+") && Integer.parseInt(args[1]) > 1024) {
            port = Integer.parseInt(args[1]);
        }

        Registry registry = LocateRegistry.getRegistry(port);
        dsm_itf myDsm = (dsm_itf) registry.lookup("myDsm");
        Map <Integer, node_itf> myAdrressNodes = myDsm.registerClient();

        System.out.println("Hi there welcome :)");
        System.out.println("Reading\t : read  <address>\nWriting\t : write <address> <string value>");
        // run scanner to handle user inputs
        Scanner scanner = new Scanner(System.in);
        String message;
        while (true) {
            message = scanner.nextLine();
            String [] arr = message.split(" ");

            if (arr[0].equals("snapshot")) {
                
            } else if((arr[0].equals("read") && arr.length > 1 && arr[1].matches("-?\\d+")) 
                    && Integer.parseInt(arr[1]) < myAdrressNodes.size() && Integer.parseInt(arr[1]) > 0) {
                System.out.println("Address " + arr[1] + " : " 
                    + myAdrressNodes.get(Integer.parseInt(arr[1])).read(Integer.parseInt(arr[1])));
            } else if((arr[0].equals("write") && arr.length > 2 && arr[1].matches("-?\\d+")) 
                    && Integer.parseInt(arr[1]) < myAdrressNodes.size() && Integer.parseInt(arr[1]) > 0) {
                myAdrressNodes.get(Integer.parseInt(arr[1])).write(Integer.parseInt(arr[1]), arr[2]);
                System.out.println("OK");
            } else {
                System.out.println("Command Unknown...!!!");
            }
        }
    }
}