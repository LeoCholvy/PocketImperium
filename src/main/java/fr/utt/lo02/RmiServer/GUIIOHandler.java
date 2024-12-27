package fr.utt.lo02.RmiServer;

import fr.utt.lo02.IO.IOHandler;
import fr.utt.lo02.core.components.Command;

import java.util.HashMap;

public interface GUIIOHandler {
    public void displayError(String message, int playerId);
    public int getStartingCellId(int playerId);
    public Command[] getCommandOrders(int playerId);
    public int[][] expand(int playerId, int nShips);
    public int[][][] explore(int playerId, int nFleet);
    public int[][] exterminate(int playerId, int nFleet);
    public int score(int id);
    public void displayWinner(int[] winnersIds);
    public void displayDraw();
    public String getIp();
    public String getUserName();
}
