package fr.utt.lo02.app;

import fr.utt.lo02.RmiServer.GUIIOHandler;
import fr.utt.lo02.core.Game;
import fr.utt.lo02.core.IllegalGameStateExeceptions;
import fr.utt.lo02.core.Player;
import fr.utt.lo02.core.components.*;
import fr.utt.lo02.data.DataManipulator;
import fr.utt.lo02.data.GameDataConverter;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.System;
import java.lang.reflect.Array;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

enum State {
    FORM,
    GAME,
    END,
}
enum InputState {
    IDLE,
    CELL,
    NUMBER,
    COMMAND,
}

public class GUI implements GUIIOHandler {
    /**
     * Map of the coordinates of the cells in the GUI
     */
    private final static HashMap<Integer, int[]> coords = new HashMap<>() {{
        put(0, new int[]{200, 299});
        put(1, new int[]{32, 60});
        put(2, new int[]{100, 57});
        put(3, new int[]{168, 62});
        put(4, new int[]{234, 63});
        put(5, new int[]{299, 60});
        put(6, new int[]{367, 63});
        put(7, new int[]{68, 123});
        put(8, new int[]{134, 119});
        put(9, new int[]{202, 125});
        put(10, new int[]{266, 121});
        put(11, new int[]{332, 119});
        put(12, new int[]{34, 179});
        put(13, new int[]{100, 181});
        put(14, new int[]{166, 182});
        put(15, new int[]{232, 182});
        put(16, new int[]{300, 179});
        put(17, new int[]{366, 183});
        put(18, new int[]{66, 240});
        put(19, new int[]{133, 239});
        put(20, new int[]{33, 303});
        put(21, new int[]{99, 301});
        put(22, new int[]{66, 360});
        put(23, new int[]{133, 359});
        put(24, new int[]{267, 239});
        put(25, new int[]{332, 241});
        put(26, new int[]{300, 300});
        put(27, new int[]{365, 301});
        put(28, new int[]{267, 360});
        put(29, new int[]{332, 359});
        put(30, new int[]{33, 421});
        put(31, new int[]{98, 421});
        put(32, new int[]{167, 421});
        put(33, new int[]{232, 419});
        put(34, new int[]{300, 421});
        put(35, new int[]{365, 421});
        put(36, new int[]{66, 481});
        put(37, new int[]{132, 479});
        put(38, new int[]{198, 481});
        put(39, new int[]{266, 479});
        put(40, new int[]{332, 481});
        put(41, new int[]{32, 538});
        put(42, new int[]{100, 541});
        put(43, new int[]{166, 539});
        put(44, new int[]{231, 539});
        put(45, new int[]{300, 541});
        put(46, new int[]{366, 539});
    }};

    private JFrame frame;
    private State state = null;
    private int playerId;
    private InputState inputState = InputState.IDLE;
    // private InputState inputState = InputState.CELL;
    private Color[] playersColor = new Color[]{Color.WHITE, Color.RED, Color.GREEN};
    private int w = 400;
    private int h = 600;
    private int panelHeight = 40;
    private String username;
    private Object tempInputValue;
    private Game game;
    private String[] textInfo = new String[]{"info", "Waiting for your turn"};
    private int tempMaxNumber;
    private final static Object lock = new Object();
    private boolean skipable = false;

    /**
     * Constructor
     */
    public GUI() {
        // init the frame
        this.frame = new JFrame("Pocket Imperium");
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /**
     * Set the game instance and update the GUI
     *
     * @param game
     */
    public void setGameInstance(Game game) {
        this.game = game;
        this.displayGame();
    }

    /**
     * Display the game.
     * This method updates the GUI to show the current state of the game.
     * It removes all existing components from the frame, sets the frame title,
     * and adds the game panel with the map and ships.
     * It also handles mouse clicks on the map to get the clicked cell.
     */
    public synchronized void displayGame() {
        this.frame.getContentPane().removeAll();
        // this.state = State.GAME;

        // change frame name
        this.frame.setTitle("PocketImperium - " + this.username);

        JPanel gamePanel = new JPanel();

        this.displayTopPanel(gamePanel);

        // load the map image
        BufferedImage map;
        try {
            map = ImageIO.read(new File("src/ressources/assets/map_sans_system.jpg"));

            Image scaledMap = map.getScaledInstance(w, h, Image.SCALE_SMOOTH);
            map = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = map.createGraphics();
            g2d.drawImage(scaledMap, 0, 0, null);
            g2d.dispose();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        JLabel mapLabel = new JLabel(new ImageIcon(map));

        // display the ships (number of ships on each cell)
        this.displayShips(gamePanel);

        // display the systems
        this.displaySystems(gamePanel);

        mapLabel.setBounds(0, this.panelHeight, w, h);
        gamePanel.setLayout(null);
        gamePanel.add(mapLabel);

        this.frame.add(gamePanel);

        // handle the click on the map
        mapLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (GUI.this.inputState == InputState.CELL) {
                    int x = e.getX();
                    int y = e.getY();
                    int cellId = GUI.getClickedCell(x, y);
                    GUI.this.tempInputValue = cellId;
                    synchronized (GUI.this) {
                        GUI.this.notifyAll();
                    }
                }
            }
        });

        this.frame.setSize(w + 15, this.panelHeight + h + 38);
        // this.frame.pack();
        this.frame.setResizable(false);
        this.frame.setVisible(true);
    }


    /**
     * Display the top panel of the game.
     * This method updates the top panel of the game based on the current input state.
     * It handles different input states such as IDLE, CELL, NUMBER, and COMMAND.
     *
     * @param gamePanel the JPanel to which the top panel components will be added
     */
    private synchronized void displayTopPanel(JPanel gamePanel) {
        if (this.inputState == InputState.IDLE || this.inputState == InputState.CELL) {
            if (this.inputState == InputState.CELL && this.skipable) {
                JButton skip = new JButton("Skip");
                skip.setBounds(this.w - 100, this.panelHeight - 15, 100, 15);
                gamePanel.add(skip);
                skip.addActionListener(e -> {
                    this.tempInputValue = -1;
                    synchronized (GUI.this) {
                        GUI.this.notifyAll();
                    }
                });
            }
            StringBuilder scoreTxt = new StringBuilder();
            for (Player p : this.game.getPlayers()) {
                scoreTxt.append(p.getName()).append(": ").append(p.getScore()).append(" | ");
            }
            scoreTxt.append("round: ").append(this.game.getRound());
            JLabel score = new JLabel(scoreTxt.toString(), SwingConstants.LEFT);
            score.setFont(new Font("Arial", Font.PLAIN, 10));
            score.setForeground(Color.WHITE);
            score.setOpaque(true);
            score.setBackground(Color.BLACK);
            score.setBounds(0, 0, this.w, 10);
            gamePanel.add(score);
            String txt = this.textInfo[1];
            JLabel label = new JLabel(txt, SwingConstants.CENTER);
            label.setFont(new Font("Arial", Font.PLAIN, 12));
            if (this.textInfo[0].equals("error")) {
                label.setForeground(Color.RED);
            } else {
                label.setForeground(Color.BLACK);
            }
            label.setOpaque(true);
            label.setBackground(Color.WHITE);
            label.setBounds(10, 10, this.w - 20, this.panelHeight - 20);
            gamePanel.add(label);
            JLabel panelColor = new JLabel("", SwingConstants.CENTER);
            panelColor.setOpaque(true);
            panelColor.setBackground(this.playersColor[this.playerId]);
            panelColor.setBounds(0, 0, this.w, this.panelHeight);
            gamePanel.add(panelColor);
        } else if (this.inputState == InputState.NUMBER) {
            JButton[] numberButtons = new JButton[this.tempMaxNumber + 1];
            for (int i = 0; i <= this.tempMaxNumber; i++) {
                if (this.skipable) {
                    JButton skip = new JButton("Skip");
                    skip.setBounds(this.w - 100, this.panelHeight - 15, 100, 15);
                    gamePanel.add(skip);
                    skip.addActionListener(e -> {
                        this.tempInputValue = -1;
                        synchronized (GUI.this) {
                            GUI.this.notifyAll();
                        }
                    });
                }

                numberButtons[i] = new JButton(String.valueOf(i));
                numberButtons[i].setFont(new Font("Arial", Font.PLAIN, 14));
                int l = 20;
                numberButtons[i].setBounds((int) (i * l * 2.3), this.panelHeight - l, (int) (l * 2.3), l);
                gamePanel.add(numberButtons[i]);
                int finalI = i;
                numberButtons[i].addActionListener(e -> {
                    this.tempInputValue = finalI;
                    synchronized (GUI.this) {
                        notifyAll();
                    }
                });
            }

            // info
            String txt = this.textInfo[1];
            JLabel label = new JLabel(txt, SwingConstants.CENTER);
            label.setFont(new Font("Arial", Font.PLAIN, 12));
            if (this.textInfo[0].equals("error")) {
                label.setForeground(Color.RED);
            } else {
                label.setForeground(Color.BLACK);
            }
            label.setOpaque(true);
            label.setBackground(Color.WHITE);
            label.setBounds(0, 0, this.w, this.panelHeight / 2);
            gamePanel.add(label);
        } else if (this.inputState == InputState.COMMAND) {
            DefaultListModel<String> listModel = new DefaultListModel<>();
            listModel.addElement("Expand");
            listModel.addElement("Explore");
            listModel.addElement("Exterminate");

            JList<String> list = new JList<>(listModel);
            list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            list.setDragEnabled(true);
            list.setDropMode(DropMode.INSERT);

            list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
            list.setVisibleRowCount(1);

            list.setTransferHandler(new TransferHandler() {
                private int draggedIndex = -1;

                @Override
                public int getSourceActions(JComponent c) {
                    return MOVE;
                }

                @Override
                protected Transferable createTransferable(JComponent c) {
                    JList<?> source = (JList<?>) c;
                    draggedIndex = source.getSelectedIndex(); // Store the index of the dragged element
                    return new StringSelection(source.getSelectedValue().toString());
                }

                @Override
                public boolean canImport(TransferSupport support) {
                    return support.isDataFlavorSupported(DataFlavor.stringFlavor);
                }

                @Override
                public boolean importData(TransferSupport support) {
                    if (!canImport(support)) {
                        return false;
                    }

                    JList.DropLocation dropLocation = (JList.DropLocation) support.getDropLocation();
                    int dropIndex = dropLocation.getIndex();

                    try {
                        String data = (String) support.getTransferable().getTransferData(DataFlavor.stringFlavor);

                        JList<String> target = (JList<String>) support.getComponent();
                        DefaultListModel<String> listModel = (DefaultListModel<String>) target.getModel();

                        // Adjust the index if necessary
                        if (draggedIndex < dropIndex) {
                            dropIndex--; // The dragged element is temporarily removed, so the index decreases
                        }

                        listModel.add(dropIndex, data);
                        listModel.remove(draggedIndex < dropIndex ? draggedIndex : draggedIndex + 1);

                        return true;
                    } catch (Exception ex) {
                        // ex.printStackTrace();
                        return false;
                    }
                }
            });

            JScrollPane scrollPane = new JScrollPane(list);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            scrollPane.setWheelScrollingEnabled(false);
            scrollPane.setBounds(0, this.panelHeight / 2, this.w - 100, this.panelHeight / 2);
            scrollPane.setBackground(Color.BLUE);
            gamePanel.add(scrollPane);

            // submit
            JButton submitButton = new JButton("Submit");

            submitButton.addActionListener(e -> {
                System.out.println("Btn clicked");
                if (inputState != InputState.COMMAND) {
                    return;
                }
                List<String> commands_list = new ArrayList<>();
                for (int i = 0; i < listModel.size(); i++) {
                    commands_list.add(listModel.getElementAt(i));
                }
                Command[] commands = new Command[3];
                for (int i = 0; i < 3; i++) {
                    if (commands_list.get(i).equals("Expand")) {
                        commands[i] = Command.EXPAND;
                    } else if (commands_list.get(i).equals("Explore")) {
                        commands[i] = Command.EXPLORE;
                    } else if (commands_list.get(i).equals("Exterminate")) {
                        commands[i] = Command.EXTERMINATE;
                    }
                }

                this.tempInputValue = commands;
                synchronized (GUI.this) {
                    notifyAll();
                }
            });
            submitButton.setBounds(this.w - 100, this.panelHeight / 2, 100, this.panelHeight / 2);
            gamePanel.add(submitButton);

            // info
            String txt = this.textInfo[1];
            JLabel label = new JLabel(txt, SwingConstants.CENTER);
            label.setFont(new Font("Arial", Font.PLAIN, 12));
            if (this.textInfo[0].equals("error")) {
                label.setForeground(Color.RED);
            } else {
                label.setForeground(Color.BLACK);
            }
            label.setOpaque(true);
            label.setBackground(Color.WHITE);
            label.setBounds(0, 0, this.w, this.panelHeight / 2);
            gamePanel.add(label);
        }
    }

    /**
     * Display the ships on the game panel.
     * This method iterates through the coordinates of the cells and displays the number of ships on each cell.
     * It creates a JLabel for each cell with ships and sets its text to show the number of available and total ships.
     * The label is then added to the game panel.
     *
     * @param gamePanel the JPanel to which the ship labels will be added
     */
    private void displayShips(JPanel gamePanel) {
        for (int id : coords.keySet()) {
            int nb = this.game.getArea().getCell(id).getShips().length;
            if (nb == 0) {
                continue;
            }

            int nbA = this.game.getArea().getCell(id).getAvailableShipsCount();
            String txt = String.valueOf(nbA) + "+" + String.valueOf(nb - nbA);
            if (nbA == nb) {
                txt = String.valueOf(nb);
            }

            JLabel centerLabel = new JLabel(txt, SwingConstants.CENTER);
            centerLabel.setOpaque(false);
            centerLabel.setFont(new Font("Arial", Font.PLAIN, 20));
            centerLabel.setForeground(this.playersColor[this.game.getArea().getCell(id).getOwner().getId()]);
            int x = coords.get(id)[0];
            int y = coords.get(id)[1] + this.panelHeight - 10;
            centerLabel.setBounds(x - 20, y - 10, 40, 20);
            gamePanel.add(centerLabel);
        }
    }


    private void displaySystems(JPanel gamePanel) {
        for (int id : coords.keySet()) {
            if (this.game.getArea().getCell(id).getSystem() == null) {
                continue;
            }

            int lvl = this.game.getArea().getCell(id).getSystem().getLevel();
            if (lvl == 3) {
                continue;
            }
            Color color;
            String txt;
            if (lvl == 1) {
                color = new Color(128, 0, 128);
                txt = "I";
            } else {
                color = new Color(255, 165, 0);
                txt = "II";
            }


            int x = coords.get(id)[0];
            int y = coords.get(id)[1] + this.panelHeight + 5;
            int r = 12;

            JLabel centerLabel = new JLabel(txt, SwingConstants.CENTER);
            centerLabel.setOpaque(true);
            centerLabel.setBackground(color);
            centerLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            centerLabel.setForeground(Color.WHITE);
            centerLabel.setBounds(x - r, y - r, 2 * r, 2 * r);
            gamePanel.add(centerLabel);
        }
    }

    /**
     * Display the form for entering server IP and username.
     * This method creates a form with input fields for the server IP and username.
     * It also adds a submit button that, when clicked, stores the input values and notifies any waiting threads.
     */
    public synchronized void displayForm() {
        this.frame.setSize(300, 150);

        JPanel panel = new JPanel();

        JLabel ipLabel = new JLabel("Enter Server IP:");
        JTextField ipTextField = new JTextField(15);
        JLabel userLabel = new JLabel("Enter Username:");
        JTextField userTextField = new JTextField(15);
        JButton submitButton = new JButton("Submit");

        submitButton.addActionListener(e -> {
            System.out.println("Btn clicked");
            this.tempInputValue = ipTextField.getText();
            this.username = userTextField.getText();

            System.out.println("Server IP: " + tempInputValue);
            System.out.println("Username: " + username);

            synchronized (this) {
                this.notifyAll();
            }
        });

        panel.add(ipLabel);
        panel.add(ipTextField);
        panel.add(userLabel);
        panel.add(userTextField);
        panel.add(submitButton);

        this.frame.add(panel);

        this.frame.setVisible(true);
    }


    public static void main(String[] args) {
        GUI gui = new GUI();
        String json = DataManipulator.loadSave("model");
        Game game = GameDataConverter.fromJson(json, "stonks");
        gui.setGameInstance(game);
        gui.setPlayerId(1);
        game.getPlayer(1).getAvailableShips(1)[0].setCell(game.getArea().getCell(46));
        game.getPlayer(1).getAvailableShips(1)[0].setCell(game.getArea().getCell(3));
        game.getPlayer(1).getAvailableShips(1)[0].setCell(game.getArea().getCell(3));
        game.getPlayer(1).getAvailableShips(1)[0].setCell(game.getArea().getCell(3));
        game.getPlayer(1).getAvailableShips(1)[0].setCell(game.getArea().getCell(3));
        game.getPlayer(1).getAvailableShips(1)[0].setCell(game.getArea().getCell(3));
        game.getPlayer(1).getAvailableShips(1)[0].setCell(game.getArea().getCell(3));
        game.getPlayer(1).getAvailableShips(1)[0].setCell(game.getArea().getCell(4));
        game.getPlayer(1).getAvailableShips(1)[0].setCell(game.getArea().getCell(8));
        game.getPlayer(1).getAvailableShips(1)[0].setCell(game.getArea().getCell(8));
        game.getPlayer(0).getAvailableShips(1)[0].setCell(game.getArea().getCell(9));
        game.getPlayer(0).getAvailableShips(1)[0].setCell(game.getArea().getCell(9));
        game.getPlayer(0).getAvailableShips(1)[0].setCell(game.getArea().getCell(9));
        game.getPlayer(0).getAvailableShips(1)[0].setCell(game.getArea().getCell(9));
        game.getPlayer(0).getAvailableShips(1)[0].setCell(game.getArea().getCell(9));
        game.getPlayer(0).getAvailableShips(1)[0].setCell(game.getArea().getCell(33));
        int[][][] r = gui.explore(0, 2);
        System.out.println(Arrays.deepToString(r));
    }

    /**
     * Get the server IP address from the GUI.
     * This method displays a form for entering the server IP and waits for the user to submit the form.
     * If the server IP or username is empty, it returns the username instead.
     *
     * @return the server IP address or the username if the server IP is empty
     */
    public synchronized String getIp() {
        this.state = State.FORM;
        this.displayForm();

        synchronized (this) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        String serverIp = (String) this.tempInputValue;
        if (serverIp.isEmpty() || this.username.isEmpty()) {
            return getUserName();
        }

        System.out.println("Got Ip from GUI");
        return serverIp;
    }

    /**
     * Get the username.
     *
     * @return the username
     */
    public String getUserName() {
        return this.username;
    }

    /**
     * Get the ID of the clicked cell based on the coordinates.
     * This method calculates the distance between the clicked coordinates and the coordinates of each cell,
     * and returns the ID of the closest cell.
     *
     * @param x the x-coordinate of the click
     * @param y the y-coordinate of the click
     * @return the ID of the closest cell
     */
    private static int getClickedCell(int x, int y) {
        return coords.entrySet().stream()
                .min((entry1, entry2) -> Double.compare(distance(x, y, entry1.getValue()), distance(x, y, entry2.getValue())))
                .map(entry -> entry.getKey())
                .orElseThrow(() -> new IllegalArgumentException("No closest point found"));
    }

    /**
     * Calculate the distance between two points.
     *
     * @param x the x-coordinate of the first point
     * @param y the y-coordinate of the first point
     * @param p the coordinates of the second point
     * @return the distance between the two points
     */
    private static double distance(int x, int y, int[] p) {
        return Math.sqrt(Math.pow(x - p[0], 2) + Math.pow(y - p[1], 2));
    }

    /**
     * Display an error message for a specific player.
     * This method updates the GUI to show an error message if the player ID matches the current player ID.
     *
     * @param message  the error message to display
     * @param playerId the ID of the player to display the error message for
     */
    @Override
    public synchronized void displayError(String message, int playerId) {
        if (playerId != this.playerId) {
            return;
        }
        this.textInfo = new String[]{"error", message};
        this.displayGame();
    }

    /**
     * Wait for user input with a specific input state and message.
     * This method sets the input state, updates the GUI with the provided message, and waits for user input.
     *
     * @param inputState the input state to set
     * @param txt        the message to display
     */
    private synchronized void waitInput(InputState inputState, String txt) {
        if (this.inputState != InputState.IDLE) {
            try {
                lock.wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        this.inputState = inputState;
        this.printTextInfo(txt);
        this.displayGame();
        try {
            this.wait();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Reset the input mode to IDLE and update the GUI.
     * This method resets the input state to IDLE, clears the text info, and updates the GUI.
     */
    private synchronized void resetInputMode() {
        this.inputState = InputState.IDLE;
        this.resetTextInfo();
        this.displayGame();
        try {
            lock.notify();
        } catch (Exception _) {
        }
    }

    /**
     * Get the ID of the starting cell for a specific player.
     * This method waits for the player to select a starting cell and returns the ID of the selected cell.
     *
     * @param playerId the ID of the player selecting the starting cell
     * @return the ID of the selected starting cell
     */
    @Override
    public synchronized int getStartingCellId(int playerId) {
        System.out.println("Chose a starting cell");

        this.waitInput(InputState.CELL, "Chose a cell to place your ship");

        int cellId = (int) this.tempInputValue;
        this.resetInputMode();
        System.out.println(">>> Starting cell : " + cellId);
        return cellId;
    }

    /**
     * Print the provided text info if the current text info is not an error.
     *
     * @param txt the text info to print
     */
    private void printTextInfo(String txt) {
        if (!this.textInfo[0].equals("error")) {
            // System.out.println(this.textInfo[1] + " TO " + txt);
            this.textInfo = new String[]{"info", txt};
        }
    }

    /**
     * Reset the text info to the default message.
     */
    private void resetTextInfo() {
        this.textInfo = new String[]{"info", "Waiting for your turn"};
    }

    /**
     * Get the command orders for a specific player.
     * This method waits for the player to choose the command orders and returns them.
     *
     * @param playerId the ID of the player choosing the command orders
     * @return an array of Command objects representing the chosen command orders
     */
    @Override
    public synchronized Command[] getCommandOrders(int playerId) {
        System.out.println("Chose commands order");

        this.waitInput(InputState.COMMAND, "Chose commands order");

        Command[] commands = (Command[]) this.tempInputValue;
        this.resetInputMode();
        System.out.println(Arrays.toString(commands));
        return commands;
    }

    /**
     * Expand the player's ships to new cells.
     * This method allows the player to choose cells to expand their ships to and returns the details of the expansion.
     *
     * @param playerId the ID of the player expanding their ships
     * @param nShips   the number of ships to expand
     * @return a 2D array where each sub-array contains the cell ID and the number of ships placed on that cell
     */
    @Override
    public synchronized int[][] expand(int playerId, int nShips) {
        System.out.println("Expand");
        ArrayList<int[]> ships = new ArrayList<>();
        Player player = this.game.getPlayer(playerId);
        int nShipsMax = Math.min(nShips, player.getNumberAvailableShips());
        while (nShipsMax > 0) {
            int nShipsOnCell;
            int cellId;
            this.skipable = true;
            this.waitInput(InputState.CELL, "Chose a cell to expand ("+nShipsMax+" ships left)");
            this.skipable = false;
            cellId = (int) this.tempInputValue;
            this.resetInputMode();
            if (cellId == -1) {
                break;
            }
            if (this.game.getArea().getCell(cellId).getSystem() == null) {
                this.displayError("You can't expand on a cell without a system", playerId);
                continue;
            }
            if (this.game.getArea().getCell(cellId).getOwner() != player) {
                this.displayError("You can't expand on a cell you don't own", playerId);
                continue;
            }
            if (nShipsMax == 1) {
                nShipsOnCell = 1;
            } else {
                this.tempMaxNumber = nShipsMax;
                this.waitInput(InputState.NUMBER, "Chose the number of ships to place on this cell");
                nShipsOnCell = (int) this.tempInputValue;
                this.resetInputMode();
                Ship[] temp = this.game.getPlayer(this.playerId).getAvailableShips(nShipsOnCell);
                for (Ship s : temp) {
                    s.setCell(this.game.getArea().getCell(cellId));
                }
            }
            ships.add(new int[]{cellId, nShipsOnCell});
            nShipsMax -= nShipsOnCell;
        }

        this.resetInputMode();
        System.out.println(">>> Expanded ships : " + Arrays.deepToString(ships.toArray(new int[0][0])));
        return ships.toArray(new int[0][0]);
    }

    /**
     * Explore new cells with the player's fleet.
     * This method allows the player to choose cells to explore with their fleet and returns the details of the exploration.
     *
     * @param playerId the ID of the player exploring with their fleet
     * @param nFleet   the number of fleets to explore with
     * @return a 3D array where each sub-array contains the details of the exploration
     */
    @Override
    public synchronized int[][][] explore(int playerId, int nFleet) {
        System.out.println("Explore");
        List<List<int[]>> input = new ArrayList<>();
        Player player = this.game.getPlayer(playerId);
        Area area = this.game.getArea();
        while (nFleet > 0) {
            List<Ship> fleetShips = new ArrayList<>();
            List<int[]> fleet = new ArrayList<>();
            int startCellId, nShips, destCellId;
            this.skipable = true;
            this.waitInput(InputState.CELL, "Chose a cell to start the exploration ("+nFleet+" fleets left)");
            this.skipable = false;
            startCellId = (int) this.tempInputValue;
            this.resetInputMode();
            if (startCellId == -1) {
                break;
            }
            if (area.getCell(startCellId).getOwner() != player) {
                this.displayError("You can't explore from a cell you don't own", playerId);
                continue;
            }

            this.tempMaxNumber = area.getCell(startCellId).getAvailableShipsCount();
            if (this.tempMaxNumber == 0) {
                this.displayError("You don't have any ships on this cell you can move", playerId);
                continue;
            }
            this.waitInput(InputState.NUMBER, "Chose the number of ships to move");
            nShips = (int) this.tempInputValue;
            this.resetInputMode();

            if (nShips == 0) {
                continue;
            }

            this.waitInput(InputState.CELL, "Chose a destination for the fleet");
            destCellId = (int) this.tempInputValue;
            this.resetInputMode();
            if (area.getCell(destCellId).getOwner() != player && area.getCell(destCellId).getOwner() != null) {
                this.displayError("This cell is already owned by another player", playerId);
                continue;
            }
            Integer distance = area.getCell(startCellId).distance(area.getCell(destCellId), 2);
            if (distance == null || distance != 1) {
                this.displayError("The destination cell is not a neighbor of the starting cell", playerId);
                continue;
            }

            // move the ships
            fleetShips.addAll(Arrays.asList(area.getCell(startCellId).getAvailableShips(nShips)));
            for (Ship s : fleetShips) {
                s.setCell(area.getCell(destCellId));
                s.setUsed(true);
            }
            this.displayGame();

            fleet.add(new int[]{startCellId, nShips, destCellId});


            //second move of the fleet
            int nShips2, destCellId2;
            this.tempMaxNumber = area.getCell(destCellId).getAvailableShipsCount();
            this.skipable = true;
            this.waitInput(InputState.NUMBER, "How many ships do you want to add to the fleet?");
            this.skipable = false;

            nShips2 = (int) this.tempInputValue;
            this.resetInputMode();

            if (nShips2 == -1) {
                input.add(fleet);
                nFleet--;
                continue;
            }

            while (true) {
                this.waitInput(InputState.CELL, "Chose a destination for the fleet");
                destCellId2 = (int) this.tempInputValue;
                this.resetInputMode();
                if (area.getCell(destCellId2).getOwner() != player && area.getCell(destCellId2).getOwner() != null) {
                    this.displayError("This cell is already owned by another player", playerId);
                    continue;
                }
                Integer distance2 = area.getCell(destCellId).distance(area.getCell(destCellId2), 2);
                if (distance2 == null || distance2 != 1) {
                    this.displayError("The destination cell is not a neighbor of the starting cell", playerId);
                    continue;
                }
                break;
            }

            // move the ships
            fleetShips.addAll(Arrays.asList(area.getCell(destCellId).getAvailableShips(nShips2)));
            for (Ship s : fleetShips) {
                s.setCell(area.getCell(destCellId2));
                s.setUsed(true);
            }
            this.displayGame();

            fleet.add(new int[]{destCellId, nShips2, destCellId2});

            input.add(fleet);
            nFleet--;
        }

        this.resetInputMode();

        int[][][] result = new int[input.size()][][];
        for (int i = 0; i < input.size(); i++) {
            result[i] = input.get(i).toArray(new int[0][0]);
        }
        System.out.println(">>> Explored ships : " + Arrays.deepToString(result));
        return result;
    }

    /**
     * Exterminate enemy ships on specific cells.
     * This method allows the player to choose cells to exterminate enemy ships and returns the details of the extermination.
     *
     * @param playerId the ID of the player exterminating enemy ships
     * @param nSystem  the number of systems to exterminate
     * @return a 2D array where each sub-array contains the details of the extermination
     */
    @Override
    public synchronized int[][] exterminate(int playerId, int nSystem) {
        System.out.println("Exterminate");
        List<List<Integer>> input = new ArrayList<>();
        Player player = this.game.getPlayer(playerId);
        Area area = this.game.getArea();

        while (nSystem > 0) {
            List<Integer> currentAttack = new ArrayList<>();
            int cellId, id, nShips;

            this.skipable = true;
            this.waitInput(InputState.CELL, "Chose a cell to exterminate ("+nSystem+" systems left)");
            this.skipable = false;
            cellId = (int) this.tempInputValue;
            this.resetInputMode();
            if (cellId == -1) {
                break;
            }
            if (area.getCell(cellId).getOwner() == player || area.getCell(cellId).getOwner() == null) {
                this.displayError("You need to exterminate an enemies cell", playerId);
                continue;
            }
            if (area.getCell(cellId).getSystem() == null) {
                this.displayError("You can't exterminate from a cell without a system", playerId);
                continue;
            }
            Cell attackedCell = area.getCell(cellId);
            List<Ship> attackingShips = new ArrayList<>();
            List<Ship> attackedShips = new ArrayList<>(Arrays.asList(attackedCell.getShips()));

            while (true) {
                this.skipable = true;
                this.waitInput(InputState.CELL, "Chose a cell to attack from");
                this.skipable = false;
                id = (int) this.tempInputValue;
                this.resetInputMode();

                if (id == -1) {
                    break;
                }

                if (area.getCell(id).getOwner() != player) {
                    this.displayError("You can't attack from a cell you don't own", playerId);
                    continue;
                }
                Integer distance = area.getCell(id).distance(area.getCell(cellId), 2);
                if (distance == null || distance != 1) {
                    this.displayError("The destination cell is not a neighbor of the starting cell", playerId);
                    continue;
                }

                this.tempMaxNumber = area.getCell(id).getAvailableShipsCount();
                this.waitInput(InputState.NUMBER, "How many ships do you want to send?");
                nShips = (int) this.tempInputValue;
                this.resetInputMode();
                if (nShips == 0) {
                    continue;
                }
                if (currentAttack.isEmpty()) {
                    currentAttack.add(cellId);
                }

                attackingShips.addAll(Arrays.asList(area.getCell(id).getAvailableShips(nShips)));
                currentAttack.add(id);
                currentAttack.add(nShips);

                //move the ships
                while (attackingShips.size() > 0 && attackedShips.size() > 0) {
                    attackingShips.getFirst().setCell(null);
                    Ship attackingShip = attackingShips.removeFirst();
                    attackedShips.getFirst().setCell(null);
                    Ship attackedShip = attackedShips.removeFirst();
                }
                if (attackingShips.size() > 0) {
                    for (Ship s : attackingShips) {
                        s.setCell(attackedCell);
                        s.setUsed(true);
                    }
                }
                this.displayGame();
            }
            if (currentAttack.size() > 0) {
                input.add(currentAttack);
                nSystem--;
            }
        }

        this.resetInputMode();
        int[][] result = new int[input.size()][];
        for (int i = 0; i < input.size(); i++) {
            result[i] = input.get(i).stream().mapToInt(Integer::intValue).toArray();
        }
        System.out.println(">>> Exterminated ships : " + Arrays.deepToString(result));
        return result;
    }

    /**
     * Score a sector for a specific player.
     * This method allows the player to choose a sector to score and returns the ID of the scored sector.
     *
     * @param id the ID of the player scoring the sector
     * @return the ID of the scored sector
     */
    @Override
    public synchronized int score(int id) {
        System.out.println("Scoring a sector");
        List<Sector> scorableSectors = this.game.getScorablesSectors();
        List<Integer> scorableSectorIds = scorableSectors.stream().map(Sector::getId).collect(Collectors.toList());

        this.waitInput(InputState.CELL, "Chose a sector to score: " + scorableSectorIds.toString());

        int cellId = (int) this.tempInputValue;
        Sector sector = this.game.getArea().getCell(cellId).getSector();
        int sectorId = -1;
        if (sector != null) {
            sectorId = this.game.getArea().getCell(cellId).getSector().getId();
        }

        this.resetInputMode();
        System.out.println(">>> Scored sector : " + sectorId);
        return sectorId;
    }

    /**
     * Display the winners of the game.
     * This method updates the GUI to show the winners of the game.
     *
     * @param winnersIds an array of player IDs representing the winners
     */
    @Override
    public synchronized void displayWinner(int[] winnersIds) {
        this.state = State.END;
        StringBuilder txt = new StringBuilder("The winners are: ");
        for (int i = 0; i < winnersIds.length; i++) {
            Player p = this.game.getPlayer(winnersIds[i]);
            txt.append(p.getName());
            if (i != winnersIds.length - 1) {
                txt.append(" & ");
            }
        }

        this.textInfo = new String[]{"info", txt.toString()};
        this.displayGame();
    }

    /**
     * Display a draw message.
     * This method updates the GUI to show a draw message.
     */
    @Override
    public synchronized void displayDraw() {
        this.state = State.END;
        this.textInfo = new String[]{"info", "It's Draw !!!!"};
        this.displayGame();
    }

    /**
     * Set the player ID for the current game.
     * This method sets the player ID and updates the game state to GAME.
     *
     * @param playerId the ID of the player
     */
    public synchronized void setPlayerId(int playerId) {
        this.playerId = playerId;
        this.state = State.GAME;
    }
}