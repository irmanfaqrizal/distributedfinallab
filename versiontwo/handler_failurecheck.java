import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;

public class handler_failurecheck extends TimerTask {

    private node_itf tmpFirstNode;
    private Map <Integer, node_itf> tmpNodesOrder;

    public handler_failurecheck(node_itf pFirstNode, Map <Integer, node_itf> pNodesOrder) {
        this.tmpFirstNode = pFirstNode;
        tmpNodesOrder = pNodesOrder;
    }

    @Override
    public void run() {
        try {
            if(!this.tmpFirstNode.failureCheck(0, tmpFirstNode, tmpNodesOrder, true)) {
                System.out.print("\nFailure happened...!!!\nInsert Command > ");
                Map <Integer, node_itf> newNoderOrder = new HashMap<Integer, node_itf>();
                tmpFirstNode.failureRestoreNodesOrder(0, true, newNoderOrder);;
            } 
        } catch (RemoteException e) {

        }
    }
}