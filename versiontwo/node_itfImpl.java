import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class node_itfImpl implements node_itf {

    private int id;
    private int idCount;
    private int memorySize;
    private String[] memory;
    private node_itf nextNode;
    private Map <Integer, node_itf> addressNode; // only in first node -> easier to manage

    protected node_itfImpl(int pId) {
        this.id = pId;
        this.idCount = 0;
        if(pId == 0) {
            addressNode = new HashMap <Integer, node_itf> ();
        }
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
    public int getMemorySize() throws RemoteException {
        return memorySize;
    }

    @Override
    public void setMemory(int pMemorySize) throws RemoteException {
        this.memorySize = pMemorySize;
        this.memory = new String[pMemorySize];
        Arrays.fill(memory, "");
    }

    @Override
    public void registerAddress(node_itf pNode) throws RemoteException {
        int idx = idCount * memorySize;
        for (int i = 0; i < memorySize; i++) {
            addressNode.put(idx, pNode);
            idx++;
        }
    }

    @Override
    public Map<Integer, node_itf> getAddressNode() throws RemoteException {
        return addressNode;
    }

    @Override
    public node_itf getNodeFromAdress(int address) throws RemoteException {
        return addressNode.get(address);
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

    // @Override
    // public Map<Integer, String> snapshot(int idNodeRequest) throws RemoteException {
    //     if(idNodeRequest == nextNode.getId()) {

    //     }
    //     return null;
    // }

    @Override
    public String[] snapshotLocal() throws RemoteException {
        return memory;
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
    }

    @Override
    public void setNextNode(node_itf pNext) throws RemoteException {
        nextNode = pNext;
    }

    @Override
    public node_itf getNextNode() throws RemoteException {
        return nextNode;
    }
}