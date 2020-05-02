import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Arrays;

public class node_itfImpl implements node_itf {

    private int id;
    private int idCount;
    private String[] memory;
    private node_itf nextNode;

    protected node_itfImpl(int pId) {
        this.id = pId;
        idCount = 0;
        memory = new String[10];
        Arrays.fill(memory, "");
    }

    @Override
    public int getId() throws RemoteException {
        return this.id;
    }

    @Override
    public int getNextId() throws RemoteException {
        idCount++;
        return idCount;
    }

    @Override
    public String read(int address) throws RemoteException {
        return memory[address % 10];
    }

    @Override
    public boolean write(int address, String value) throws RemoteException {
        memory[address % 10] = value;
        return true;
    }

    @Override
    public void setNextNodeTraverse(node_itf pNext) throws RemoteException {
        if(nextNode == null && id == 0) { // this is the second node trying to connect (called by first node)
            nextNode = pNext;
            nextNode.setNextNode(this);
        } else if (nextNode != null && nextNode.getId()!= 0) { // when in between the first and the last then continue
            nextNode.setNextNodeTraverse(pNext);
        } else { // when at the last then put node one as the new node nextnode, and put nextnode of the last node the new node
            pNext.setNextNode(nextNode);
            nextNode = pNext;
        }

        // if(id == 0){
        //     nextNode = pNext;
        //     pNext.setNextNode(this);
        // } else {
        //     nextNode = pNext;
        // }
    }

    @Override
    public void setNextNode(node_itf pNext) throws RemoteException {
        System.out.println("here");
        nextNode = pNext;
    }

    @Override
    public node_itf getNextNode() throws RemoteException {
        return nextNode;
    }
}