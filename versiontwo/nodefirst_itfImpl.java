// import java.rmi.NotBoundException;
// import java.rmi.RemoteException;
// import java.rmi.registry.LocateRegistry;
// import java.rmi.registry.Registry;
// import java.util.Arrays;

// public class nodefirst_itfImpl implements nodefirst_itf {

//     private int id;
//     private int idCount;
//     private String[] memory;
//     private node_itf nextNode;

//     protected nodefirst_itfImpl(int pId) {
//         this.id = pId;
//         this.idCount = 0;
//         memory = new String[10];
//         Arrays.fill(memory, "");
//     }

//     @Override
//     public int getId() throws RemoteException {
//         return this.id;
//     }

//     @Override
//     public int getNextId() throws RemoteException {
//         idCount++;
//         return idCount;
//     }

//     @Override
//     public String read(int address) throws RemoteException {
//         return memory[address % 10];
//     }

//     @Override
//     public boolean write(int address, String value) throws RemoteException {
//         memory[address % 10] = value;
//         return true;
//     }

//     @Override
//     public dsm_itf createNode(int port) throws RemoteException, NotBoundException {
//         Registry registry = LocateRegistry.getRegistry(port);
//         dsm_itf myDsm = (dsm_itf) registry.lookup("DSM");
//         return myDsm;
//     }

//     @Override
//     public void setNextNode(node_itf pNext) throws RemoteException {
//         nextNode = pNext;
//     }
// }