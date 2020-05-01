import java.rmi.RemoteException;
import java.util.Arrays;

public class node_itfImpl implements node_itf {

    private int id;
    private String [] memory;

    protected node_itfImpl(int pId) {
        this.id = pId;
        memory = new String[10];
        Arrays.fill(memory, "");
	}

    @Override
    public int getId() throws RemoteException {
        return this.id;
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
}