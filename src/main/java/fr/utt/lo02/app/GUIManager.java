package fr.utt.lo02.app;

public class GUIManager {
    private GUI gui;
    private ChatGUI chatGui;

    public GUIManager() {
        this.gui = new GUI();
        // this.chatGui = new ChatGUI();
    }

    public GUI getGUI() {
        return this.gui;
    }

    public ChatGUI getChatGUI() {
        return this.chatGui;
    }
}
