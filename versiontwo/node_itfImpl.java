import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class node_itfImpl implements node_itf {

    private int id;
    private int idCount;
    private int memorySize;
    private Map <Integer, String> memoryMap;
    private node_itf nextNode;
    private Map <Integer, node_itf> nodesOrder;
    
    private Map<Integer, String> memoryBackup;
    private Map<Integer, Map <Integer, String>> backupMap;
    
    TimerTask handler_failurecheck;
	Timer timerHandler_failurecheck;

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
            nodesOrder = new HashMap<Integer, node_itf>();
            nextNode = this;
            startFailureCheck(this, nodesOrder);

            memoryBackup = new HashMap<Integer, String>(); 
            backupMap = new HashMap<Integer, Map <Integer, String>>();
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
    public void setMemory(int pMemorySize, Map<Integer, String> pMemoryBackup) throws RemoteException {
        if(pMemoryBackup.isEmpty()) {
            this.memorySize = pMemorySize;
            for (int i = 0; i < pMemorySize; i++) {
                memoryMap.put(i + (this.id * pMemorySize), "");
            }
        } else {
            memoryMap = pMemoryBackup;
        }
    }

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
    public boolean failureCheck(int pCount, node_itf pFirstNode, Map <Integer, node_itf> pNodesOrder, boolean pStart, Map<Integer, Map <Integer, String>> pBackupMap) throws RemoteException {
        
        try {
            if (this.id == 0 && pStart == false) { // if checking arrived at first node
                backupMap = pBackupMap;
                return true;
            } else { 
                if(this.id == 0 && pStart == true) { // when we just begin we have to re-initialize the parameter
                    pNodesOrder = nodesOrder;
                    pBackupMap.put(pCount, memoryMap);
                    return nextNode.failureCheck(pCount + 1, pFirstNode, pNodesOrder, false, pBackupMap);
                } else { // continue to next node
                    pBackupMap.put(pCount, memoryMap);
                    return nextNode.failureCheck(pCount + 1, pFirstNode, pNodesOrder, false, pBackupMap);
                }
            }
        } catch (Exception e) {
            if (pNodesOrder.size() == 2 && id == 0 && pStart == true) { // if failure happened on the second node and the first node is the only one left
                pFirstNode.setBackup(1);
                this.nextNode = pFirstNode;
            } else if (pNodesOrder.size() == pCount + 2 ) { // if the nextnode is connected to first node (that node is the last node)
                pFirstNode.setBackup(pCount + 1);;
                this.nextNode = pFirstNode;
            } else { // connect this node with the node after the failed node
                pFirstNode.setBackup(pCount + 1);
                this.nextNode = pNodesOrder.get(pCount + 2);
            }
            return false;
        }
    }

    @Override
    public void failureRestoreNodesOrder(int pCount, boolean pStart, Map <Integer, node_itf> pNodesOrder) throws RemoteException {
        if (this.id == 0 && pStart == true) { 
            if (nodesOrder.size() == 2) { // if failure happened on the second/next node and the first node is the only one left
                pNodesOrder.put(0, this);
                nodesOrder = pNodesOrder;
            } else { // if just begin
                pNodesOrder.put(0, this);
                this.nextNode.failureRestoreNodesOrder(pCount+1, false, pNodesOrder);
            }
        }  else if (this.id == 0 && pStart == false) { // arrived at first node
            nodesOrder = pNodesOrder;
        } else { //  put the node inside the order and continue
            pNodesOrder.put(pCount, this);
            this.nextNode.failureRestoreNodesOrder(pCount+1, false, pNodesOrder);
        }
    }

    @Override
    public void printNodes() throws RemoteException {
        for(Map.Entry<Integer, node_itf> entry : nodesOrder.entrySet()) {           
            System.out.println("Order [" + entry.getKey() + "], Id " + entry.getValue().getId());
        }
    }

    @Override
    public void printBackup() throws RemoteException {
        for(Map.Entry<Integer, String> entry : this.memoryBackup.entrySet()) {           
            System.out.println("Address [" + entry.getKey() + "] : " + entry.getValue());
        }
    }

    @Override
    public void printAllBackups() throws RemoteException {
        for(Map.Entry<Integer, Map<Integer, String>> entry : backupMap.entrySet()) {  
            for(Map.Entry<Integer, String> entryMemory : entry.getValue().entrySet()){ 
                System.out.println("Order [" + entry.getKey() + "] => Address [" + entryMemory.getKey() + "] : " + entryMemory.getValue());
            }
        }
    }

    @Override
    public void setBackup(int order) throws RemoteException {
        memoryBackup = backupMap.get(order);
    }

    @Override
    public void clearBackup() throws RemoteException {
        memoryBackup.clear();
    }

    @Override
    public Map<Integer, String> getBackup() throws RemoteException {
        return memoryBackup;
    }
}