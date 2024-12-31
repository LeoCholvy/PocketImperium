package fr.utt.lo02.app;

import fr.utt.lo02.RmiServer.Client;
import fr.utt.lo02.core.Game;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeSupport;

/**
 * The ChatGUI class represents the graphical user interface for the chat functionality in the game.
 * It provides a window with a scrollable text area for displaying chat messages and an input field for sending messages.
 */
public class ChatGUI {
    private final JTextArea textArea;
    private JFrame frame;
    private int playerId;
    private StringBuilder chatHist = new StringBuilder();
    private Game game;
    private Client client;

    /**
     * Constructs a new ChatGUI instance and initializes the chat window.
     */
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

    /**
     * Sets the player ID for the chat.
     *
     * @param playerId the ID of the player
     */
    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    /**
     * The main method to create and display the chat window.
     *
     * @param args the command line arguments
     */
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

    /**
     * Sets the game instance and client for the chat.
     *
     * @param game the game instance
     * @param client the client instance
     */
    public void setGameInstance(Game game, Client client) {
        this.game = game;
        this.client = client;
        try {
            this.frame.setTitle("Chat - "+game.getPlayer(this.playerId).getName());
        } catch (Exception _) {

        }
    }

    /**
     * Receives a message from a player and updates the chat history.
     *
     * @param playerId the ID of the player sending the message
     * @param message the message content
     */
    public void receiveMessage(int playerId, String message) {
        String name = this.game.getPlayer(playerId).getName();
        chatHist.append("\n"+name+": "+message);
        this.textArea.setText(chatHist.toString());
    }
}