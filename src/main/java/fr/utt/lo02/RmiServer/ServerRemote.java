package fr.utt.lo02.RmiServer;

import java.rmi.RemoteException;
import java.rmi.Remote;

public interface ServerRemote extends Remote {
    public void ping() throws RemoteException;
    public boolean registerClient(ClientRemote client) throws RemoteException;
    // return the client playerId, or null if the client need to choose its name (create a new human player)
}
