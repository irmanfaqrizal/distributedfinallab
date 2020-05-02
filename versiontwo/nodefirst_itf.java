// import java.rmi.NotBoundException;
// import java.rmi.Remote;
// import java.rmi.RemoteException;

// public interface nodefirst_itf extends Remote {
//     int getId () throws RemoteException;
//     int getNextId() throws RemoteException;
//     String read (int address) throws RemoteException;
//     boolean write (int address, String value) throws RemoteException;
//     dsm_itf createNode(int port) throws RemoteException, NotBoundException;
//     void setNextNode (node_itf pNext) throws RemoteException;
// }