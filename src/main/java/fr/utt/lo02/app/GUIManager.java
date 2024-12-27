package fr.utt.lo02.app;

import fr.utt.lo02.RmiServer.Client;
import fr.utt.lo02.core.Game;

public class GUIManager {
    private GUI gui;
    private ChatGUI chatGui;

    public GUIManager() {
        this.gui = new GUI();
        this.chatGui = new ChatGUI();
    }

    public GUI getGUI() {
        return this.gui;
    }

    public ChatGUI getChatGUI() {
        return this.chatGui;
    }

    public void setPlayerId(int playerId) {
        this.gui.setPlayerId(playerId);
        this.chatGui.setPlayerId(playerId);
    }

    public void setGameInstance(Game game, Client client) {
        this.gui.setGameInstance(game);
        this.chatGui.setGameInstance(game, client);
    }
}
