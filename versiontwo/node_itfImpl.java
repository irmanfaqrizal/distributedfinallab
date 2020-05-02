import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class node_itfImpl implements node_itf {

    private int id;
    private int idCount;
    private int memorySize;
    private Map <Integer, String> memoryMap; // only in first node -> easier to manage
    private node_itf nextNode;
    private Map <Integer, node_itf> nodesOrder;
    TimerTask handler_failurecheck;
	Timer timerHandler_failurecheck;
    // private Map <Integer, node_itf> addressNode; // only in first node -> easier to manage

    public void startFailureCheck(node_itf pFirstNode, Map <Integer, node_itf> pNodesOrder) {
        handler_failurecheck = new handler_failurecheck(pFirstNode, pNodesOrder);
        timerHandler_failurecheck = new Timer(true);
        timerHandler_failurecheck.scheduleAtFixedRate(handler_failurecheck, 0, 10000);
    }

    protected node_itfImpl(int pId) {
        this.id = pId;
        this.idCount = 0;
        this.memoryMap = new HashMap<Integer, String>();
        if(pId == 0) {
            // this.addressNode = new HashMap <Integer, node_itf> ();
            nodesOrder = new HashMap<Integer, node_itf>();
            nextNode = this;
            startFailureCheck(this, nodesOrder);
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
    public void registerNode(node_itf pNode) throws RemoteException {
        nodesOrder.put(nodesOrder.size(), pNode);
    }

    @Override
    public void setMemory(int pMemorySize) throws RemoteException {
        this.memorySize = pMemorySize;
        for (int i = 0; i < pMemorySize; i++) {
            memoryMap.put(i + (this.id * pMemorySize), "");
        }
    }

    // @Override
    // public void registerAddress(node_itf pNode) throws RemoteException {
    //     int idx = idCount * memorySize;
    //     for (int i = 0; i < memorySize; i++) {
    //         addressNode.put(idx, pNode);
    //         idx++;
    //     }
    // }

    // @Override
    // public Map<Integer, node_itf> getAddressNode() throws RemoteException {
    //     return addressNode;
    // }

    // @Override
    // public node_itf getNodeFromAdress(int address) throws RemoteException {
    //     return addressNode.get(address);
    // }

    @Override
    public String read(int pAddress, int pId, boolean pStart) throws RemoteException {
        String value = memoryMap.get(pAddress);
        if (value != null) {
            return value;
        } else {
            if (pId == this.id && pStart == false){
                return "<address not found>";
            }
            else {
                return nextNode.read(pAddress, pId, false);
            }
        }
    }

    @Override
    public String write(int pAddress, String pValue, int pId, boolean pStart) throws RemoteException {
        String value = memoryMap.get(pAddress);
        if (value != null) {
            memoryMap.put(pAddress, pValue);
            return "OK";
        } else if (pId == this.id && pStart == false) {
            return "<address not found>";
        } else {
            return nextNode.write(pAddress, pValue, pId, false);
        }
    }

    @Override
    public void snapshot(int pId, Map <Integer, String> tmpMap) throws RemoteException {
        if (this.id != pId || tmpMap.size() == 0) {
            tmpMap.putAll(memoryMap);
            nextNode.snapshot(pId, tmpMap);
        } else {
            for(Map.Entry<Integer, String> entry : tmpMap.entrySet()) {           
                System.out.println("Address [" + entry.getKey() + "] : " + entry.getValue());
            }
        }
    }

    @Override
    public void snapshotLocal() throws RemoteException {
        for(Map.Entry<Integer, String> entry : this.memoryMap.entrySet()) {           
            System.out.println("Address [" + entry.getKey() + "] : " + entry.getValue());
        }
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

    @Override
    public boolean failureCheck(int pCount, node_itf pFirstNode, Map <Integer, node_itf> pNodesOrder, boolean pStart) throws RemoteException {
        try {
            if (this.id == 0 && pStart == false) { // if checking arrived at first node
                return true;
            } else { // continue to next node
                return nextNode.failureCheck(pCount + 1, pFirstNode, pNodesOrder, false);
            }
        } catch (Exception e) {
            if (pNodesOrder.size() == pCount + 2 ) { // if the nextnode is connected to first node
                this.nextNode = pFirstNode;
            } else {
                this.nextNode = pNodesOrder.get(pCount + 2);
            }
            return false;
        }
    }

    @Override
    public void failureRestoreNodesOrder(int pCount, boolean pStart, Map <Integer, node_itf> pNodesOrder) throws RemoteException {
        if(this.id == 0 && pStart == true) {
            pNodesOrder.put(0, this);
            this.nextNode.failureRestoreNodesOrder(pCount+1, false, pNodesOrder);
        } else if (this.id == 0 && pStart == false) {
            this.nodesOrder = pNodesOrder;
        } else {
            pNodesOrder.put(pCount, this);
            this.nextNode.failureRestoreNodesOrder(pCount+1, false, pNodesOrder);
        }
    }

    @Override
    public void test() throws RemoteException {
        for(Map.Entry<Integer, node_itf> entry : this.nodesOrder.entrySet()) {           
            System.out.println("Order [" + entry.getKey() + "], Id " + entry.getValue().getId());
        }
    }
}