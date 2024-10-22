package fr.utt.lo02.IO;

import fr.utt.lo02.core.Game;
import fr.utt.lo02.core.Player;
import fr.utt.lo02.core.components.Command;

import java.util.HashMap;
// import fr.utt.lo02.IO.GUI;

public class IOHandler {
    private IOMode mode;
    public IOHandler(IOMode mode) {
        this.mode = mode;
        if (this.mode == IOMode.SERVER) {
            // setup the server
        }
    }
    public void displayError(String message) {
        if (mode == IOMode.CLI) {
            CLI.displayError(message);
        }
    }
    public int getStartingCellId(int playerId) {
        if (mode == IOMode.CLI) {
            return CLI.getStartingCellId(playerId);
        }
        return 0;
    }
    public HashMap<Integer, Command[]> getCommandOrders() {
        if (mode == IOMode.CLI) {
            return CLI.getCommandOrders();
        }
        return null;
    }
    public int[][] expand(int playerId, int nShips) {
        if (mode == IOMode.CLI) {
            return CLI.expand(playerId, nShips);
        }
        return null;
    }
    public int[][] explore(int playerId, int nFleet) {
        if (mode == IOMode.CLI) {
            return CLI.explore(playerId, nFleet);
        }
        return null;
    }
    public int[][] exterminate(int playerId, int nFleet) {
        if (mode == IOMode.CLI) {
            return CLI.exterminate(playerId, nFleet);
        }
        return null;
    }

    public int score(int id) {
        if (mode == IOMode.CLI) {
            return CLI.score(id);
        }
        return 0;
    }
}
