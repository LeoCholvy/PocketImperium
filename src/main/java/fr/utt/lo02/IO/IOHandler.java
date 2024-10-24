package fr.utt.lo02.IO;

import fr.utt.lo02.core.components.Command;

import java.util.HashMap;

public interface IOHandler {
    public void displayError(String message);
    public int getStartingCellId(int playerId);
    public HashMap<Integer, Command[]> getCommandOrders();
    public int[][] expand(int playerId, int nShips);
    public int[][] explore(int playerId, int nFleet);
    public int[][] exterminate(int playerId, int nFleet);
    public int score(int id);
}
