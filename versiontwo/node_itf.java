import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

public interface node_itf extends Remote {
    int getId () throws RemoteException;
    int getNextId() throws RemoteException;
    int getMemorySize() throws RemoteException;
    void registerNode(node_itf pNode) throws RemoteException;
    void setMemory(int pMemorySize, Map<Integer, String> pMemoryBackup) throws RemoteException;
    String read (int pAddress, int pId, boolean pStart) throws RemoteException;
    String write (int pAddress, String pValue, int pId, boolean pStart) throws RemoteException;
    void snapshot (int pId, Map <Integer, String> tmpMap) throws RemoteException;
    void snapshotLocal() throws RemoteException; 
    void setNextNodeTraverse (node_itf pNext) throws RemoteException;
    void setNextNode (node_itf pNext) throws RemoteException;
    node_itf getNextNode() throws RemoteException;
    boolean failureCheck (int count, node_itf pFirstNode, Map <Integer, node_itf> pNodesOrder, boolean pStart, Map<Integer, Map <Integer, String>> pBackupMap) throws RemoteException;
    void failureRestoreNodesOrder (int pCount, boolean pStart, Map <Integer, node_itf> pNodesOrder) throws RemoteException;
    void printNodes () throws RemoteException;
    void printBackup () throws RemoteException;
    void printAllBackups () throws RemoteException;
    void setBackup (int order) throws RemoteException;
    void clearBackup () throws RemoteException;
    Map<Integer, String> getBackup () throws RemoteException;
}