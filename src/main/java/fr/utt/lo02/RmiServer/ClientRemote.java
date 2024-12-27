package fr.utt.lo02.RmiServer;

import fr.utt.lo02.core.Game;
import fr.utt.lo02.core.components.Command;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientRemote extends Remote {
    public String getUserName() throws RemoteException;
    public void setGameInstance(String json)throws RemoteException;
    public void setPlayerId(int playerId) throws RemoteException;
    // public void setCurrentAction(String action) throws RemoteException;
    public void receiveMessage(int playerId, String message) throws RemoteException;

    public void displayError(String message, int playerId) throws RemoteException;
    public int getStartingCellId(int playerId) throws RemoteException;
    public Command[] getCommandOrder(int playerId) throws RemoteException;
    public int[][] expand(int playerId, int nShips) throws RemoteException;
    public int[][][] explore(int playerId, int nFleet) throws RemoteException;
    public int[][] exterminate(int playerId, int nFleet) throws RemoteException;
    public int score(int id) throws RemoteException;
    public void displayWinner(int[] winnersIds) throws RemoteException;
    public void displayDraw() throws RemoteException;
}
