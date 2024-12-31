package fr.utt.lo02.app;

import fr.utt.lo02.RmiServer.Client;
import fr.utt.lo02.core.Game;

/**
 * The GUIManager class manages the GUI and ChatGUI instances for the game.
 * It provides methods to get the GUI instances, set the player ID, and set the game instance.
 */
public class GUIManager {
    private GUI gui;
    private ChatGUI chatGui;

    /**
     * Constructs a new GUIManager instance and initializes the GUI and ChatGUI.
     */
    public GUIManager() {
        this.gui = new GUI();
        this.chatGui = new ChatGUI();
    }

    /**
     * Gets the GUI instance.
     *
     * @return the GUI instance
     */
    public GUI getGUI() {
        return this.gui;
    }

    /**
     * Gets the ChatGUI instance.
     *
     * @return the ChatGUI instance
     */
    public ChatGUI getChatGUI() {
        return this.chatGui;
    }

    /**
     * Sets the player ID for both the GUI and ChatGUI.
     *
     * @param playerId the ID of the player
     */
    public void setPlayerId(int playerId) {
        this.gui.setPlayerId(playerId);
        this.chatGui.setPlayerId(playerId);
    }

    /**
     * Sets the game instance and client for both the GUI and ChatGUI.
     *
     * @param game the game instance
     * @param client the client instance
     */
    public void setGameInstance(Game game, Client client) {
        this.gui.setGameInstance(game);
        this.chatGui.setGameInstance(game, client);
    }
}