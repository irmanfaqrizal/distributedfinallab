import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

public interface node_itf extends Remote {
    int getId () throws RemoteException;
    int getNextId() throws RemoteException;
    int getMemorySize() throws RemoteException;
    void setMemory(int pMemorySize) throws RemoteException;
    // void registerAddress (node_itf pNode) throws RemoteException;
    // Map <Integer, node_itf> getAddressNode() throws RemoteException;
    // node_itf getNodeFromAdress (int address) throws RemoteException;
    String read (int pAddress, int pId, boolean pStart) throws RemoteException;
    String write (int pAddress, String pValue, int pId, boolean pStart) throws RemoteException;
    void snapshot (int pId, Map <Integer, String> tmpMap) throws RemoteException;
    void snapshotLocal() throws RemoteException; 
    void setNextNodeTraverse (node_itf pNext) throws RemoteException;
    void setNextNode (node_itf pNext) throws RemoteException;
    node_itf getNextNode() throws RemoteException;
}