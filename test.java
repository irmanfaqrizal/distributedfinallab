import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.Scanner;
import java.util.HashMap;

public class test {
    static void testmap(Map<Integer, String> testmaphere) {
        testmaphere.put(5, "aaa");
    }

    public static void main(String[] args) {
        Map<Integer, String> test = new HashMap<Integer, String>();
        System.out.println(test.size());
        testmap(test);
        System.out.println(test.size());
    }
}