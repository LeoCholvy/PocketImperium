package fr.utt.lo02.app;

import fr.utt.lo02.RmiServer.Client;
import fr.utt.lo02.core.Game;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeSupport;

public class ChatGUI {
    private final JTextArea textArea;
    private JFrame frame;
    private int playerId;
    private StringBuilder chatHist = new StringBuilder();
    private Game game;
    private Client client;

    public ChatGUI() {
        this.frame = new JFrame("Chat");
        this.frame.setSize(400, 400);
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setLayout(new BorderLayout());
        this.frame.setResizable(true);

        // Zone de texte scrollable
        this.textArea = new JTextArea("");
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false); // Rendre la zone non éditable

        JScrollPane scrollPane = new JScrollPane(textArea);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Champ de texte pour saisir du texte (2 lignes de hauteur)
        JTextArea inputField = new JTextArea(2, 20);
        inputField.setLineWrap(true);
        inputField.setWrapStyleWord(true);

        JScrollPane inputScrollPane = new JScrollPane(inputField);
        frame.add(inputScrollPane, BorderLayout.SOUTH);

        // Add event listener to the input field for the Enter key
        inputField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    // Handle the Enter key event
                    String inputText = inputField.getText().trim();
                    if (game != null) {
                        String name = game.getPlayer(playerId).getName();
                        chatHist.append("\n"+name+": "+inputText);
                        client.sendMessage(playerId, inputText);
                    }
                    textArea.setText(chatHist.toString());
                    inputField.setText(""); // Clear the input field
                }
            }
        });

        this.frame.setVisible(true);
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }


    public static void main(String[] args) {
        // Créer la fenêtre principale
        JFrame frame = new JFrame("Fenêtre avec Zone Scrollable");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);
        frame.setLayout(new BorderLayout());

        // Zone de texte scrollable
        JTextArea textArea = new JTextArea("Voici une très longue chaîne de caractères qui dépasse largement la taille visible de la fenêtre. Vous pouvez défiler pour voir le reste.");
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false); // Rendre la zone non éditable

        JScrollPane scrollPane = new JScrollPane(textArea);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Champ de texte pour saisir du texte (2 lignes de hauteur)
        JTextArea inputField = new JTextArea(2, 20);
        inputField.setLineWrap(true);
        inputField.setWrapStyleWord(true);

        JScrollPane inputScrollPane = new JScrollPane(inputField);
        frame.add(inputScrollPane, BorderLayout.SOUTH);

        // Rendre la fenêtre visible
        frame.setVisible(true);
    }

    public void setGameInstance(Game game, Client client) {
        this.game = game;
        this.client = client;
        this.frame.setTitle("Chat - "+game.getPlayer(this.playerId).getName());
    }

    public void receiveMessage(int playerId, String message) {
        String name = this.game.getPlayer(playerId).getName();
        chatHist.append("\n"+name+": "+message);
        this.textArea.setText(chatHist.toString());
    }
}
