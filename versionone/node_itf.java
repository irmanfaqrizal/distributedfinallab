import java.rmi.Remote;
import java.rmi.RemoteException;

public interface node_itf extends Remote {
    int getId () throws RemoteException;
    String read (int address) throws RemoteException;
    boolean write (int address, String value) throws RemoteException;
}