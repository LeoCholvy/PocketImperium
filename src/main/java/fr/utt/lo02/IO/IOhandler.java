package fr.utt.lo02.IO;

import fr.utt.lo02.core.Player;
import fr.utt.lo02.IO.CLI;
// import fr.utt.lo02.IO.GUI;

public class IOHandler {
    private IOMode mode;
    public IOHandler(IOMode mode) {
        this.mode = mode;
    }
    public void displayError(String message) {
        if (mode == IOMode.CLI) {
            CLI.displayError(message);
        } else {
            // TODO
        }
    }
    public int placeTwoShips(Player player) {
        if (mode == IOMode.CLI) {
            return CLI.placeTwoShips(player);
        } else {
            // TODO
            return 0;
        }
    }
}
