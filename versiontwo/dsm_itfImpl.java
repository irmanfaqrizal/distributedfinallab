import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class dsm_itfImpl implements dsm_itf {

	// void printSnapshot ()
	Map <Integer, node_itf> mapAddressNode;
	List <node_itf> nodeList;
	private String dsmName;
	private int idxNodes;

    protected dsm_itfImpl() {
		dsmName = "My DSM";
		nodeList = new ArrayList<node_itf>();
		mapAddressNode = new HashMap<Integer, node_itf>();
		idxNodes = 0;
	}

	@Override
	public void snapshot() throws RemoteException { 
		System.out.println(dsmName + "===>");
        // for (node_itf node_itf : nodeList) {
		// 	System.out.println("Node ["+node_itf.getId() + "] Contains : ");
		// }
	}

	@Override
	public void addNode(node_itf pNode) throws RemoteException {
		this.nodeList.add(pNode);
		for (int i = 0; i < 10; i++) {
			this.mapAddressNode.put(idxNodes, pNode);
			idxNodes++;
		}
	}

	@Override
	public Map <Integer, node_itf> registerClient() throws RemoteException {
		return this.mapAddressNode;
	}
}
