package fr.utt.lo02.IO;

import fr.utt.lo02.core.components.Command;

public interface IOHandlerIA {
    public void displayError(String message, int playerId);
    public int getStartingCellId(int playerId);
    public Command[] getCommandOrder();
    public int[][] expand(int playerId, int nShips);
    public int[][][] explore(int playerId, int nFleet);
    public int[][] exterminate(int playerId, int nFleet);
    public int score(int id);
}
