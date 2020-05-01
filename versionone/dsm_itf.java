import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

public interface dsm_itf extends Remote {
    void snapshot() throws RemoteException;
    void addNode(node_itf pNode) throws RemoteException;
    Map <Integer, node_itf> registerClient() throws RemoteException;
}
