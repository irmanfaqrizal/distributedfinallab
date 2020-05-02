import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface node_itf extends Remote {
    int getId () throws RemoteException;
    int getNextId() throws RemoteException;
    String read (int address) throws RemoteException;
    boolean write (int address, String value) throws RemoteException;
    void setNextNodeTraverse (node_itf pNext) throws RemoteException;
    void setNextNode (node_itf pNext) throws RemoteException;
    node_itf getNextNode() throws RemoteException;
}